package com.example.mini_mart.service.impl;

import com.example.mini_mart.dto.response.AiIntentResponse;
import com.example.mini_mart.service.GeminiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation của GeminiService
 * Sử dụng REST API của Google Gemini
 * LƯU Ý: AI chỉ đọc và diễn giải dữ liệu, KHÔNG được ghi DB
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {
    
    @Value("${gemini.api.key:}")
    private String geminiApiKey;
    
    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1}")
    private String geminiApiUrl;
    
    // Tăng timeout cho Gemini API: connect 30s, read/write 60s (vì AI có thể mất thời gian để xử lý)
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Rate limiting: Free tier ~15 requests/second, giới hạn 10 req/s để an toàn
    private static final long MIN_DELAY_BETWEEN_REQUESTS_MS = 100; // 100ms = 10 req/s
    private volatile long lastRequestTime = 0;
    private final Object rateLimitLock = new Object();
    
    /**
     * Rate limiting: đảm bảo không gửi quá nhiều requests cùng lúc
     */
    private void waitForRateLimit() {
        synchronized (rateLimitLock) {
            long currentTime = System.currentTimeMillis();
            long timeSinceLastRequest = currentTime - lastRequestTime;
            
            if (timeSinceLastRequest < MIN_DELAY_BETWEEN_REQUESTS_MS) {
                long waitTime = MIN_DELAY_BETWEEN_REQUESTS_MS - timeSinceLastRequest;
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            lastRequestTime = System.currentTimeMillis();
        }
    }
    
    /**
     * Retry với exponential backoff khi gặp 429
     */
    private List<Float> generateEmbeddingWithRetry(String text, int maxRetries) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                waitForRateLimit();
                return generateEmbeddingInternal(text);
            } catch (IOException e) {
                String errorMsg = e.getMessage() != null ? e.getMessage() : "";
                
                if (errorMsg.contains("429") || errorMsg.contains("TooManyRequests") ||
                    errorMsg.contains("quota") || errorMsg.contains("Quota")) {
                    
                    if (attempt < maxRetries - 1) {
                        long backoffMs = (long) Math.pow(2, attempt) * 1000;
                        log.warn("Rate limit hit (429), retrying in {}ms (attempt {}/{})", 
                                backoffMs, attempt + 1, maxRetries);
                        try {
                            Thread.sleep(backoffMs);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return new ArrayList<>();
                        }
                    } else {
                        log.error("Rate limit exceeded after {} retries", maxRetries);
                        return new ArrayList<>();
                    }
                } else {
                    log.error("Error calling Gemini Embedding API: {}", errorMsg);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                log.error("Unexpected error calling Gemini Embedding API", e);
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }
    
    @Override
    public List<Float> generateEmbedding(String text) {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            log.warn("Gemini API key not configured, cannot generate embedding");
            return new ArrayList<>();
        }
        
        // Retry với exponential backoff khi gặp 429
        return generateEmbeddingWithRetry(text, 3); // Retry tối đa 3 lần
    }
    
    /**
     * Internal method để gọi embedding API (không có retry)
     */
    private List<Float> generateEmbeddingInternal(String text) throws IOException {

        Map<String, Object> requestBody = new HashMap<>();
        String[] embeddingModels = {"gemini-embedding-001", "embedding-001"};
        
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(Map.of("text", text)));
        requestBody.put("content", content);
        requestBody.put("outputDimensionality", 768);
        
        IOException lastError = null;
        for (String modelName : embeddingModels) {
            try {
                requestBody.put("model", "models/" + modelName);
                String url = geminiApiUrl + "/models/" + modelName + ":embedContent?key=" + geminiApiKey;
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                
                RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "{}";
                        JsonNode jsonNode = objectMapper.readTree(responseBody);
                        
                        JsonNode embedding = jsonNode.get("embedding");
                        if (embedding != null && embedding.get("values") != null) {
                            List<Float> vector = new ArrayList<>();
                            for (JsonNode value : embedding.get("values")) {
                                vector.add((float) value.asDouble());
                            }
                            
                            if (vector.size() != 768) {
                                log.warn("Embedding dimension mismatch! Expected 768, got {}. This may cause Qdrant errors.", vector.size());
                            }
                            
                            log.debug("Successfully used embedding model: {}", modelName);
                            return vector;
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        if (response.code() == 404) {
                            log.debug("Model {} not found, trying next model...", modelName);
                            lastError = new IOException("Model not found: " + modelName);
                            continue; // Thử model tiếp theo
                        }
                        throw new IOException("API error: " + errorBody);
                    }
                }
            } catch (IOException e) {
                lastError = e;
                if (e.getMessage() != null && e.getMessage().contains("Model not found")) {
                    continue; // Thử model tiếp theo
                }
                throw e;
            }
        }
        
        log.error("All embedding models failed. Last error: {}", lastError != null ? lastError.getMessage() : "Unknown");
        throw new IOException("No embedding model available. Last error: " + (lastError != null ? lastError.getMessage() : "Unknown"));
    }
    
    @Override
    public String chatCompletion(String prompt, String userMessage, Map<String, Object> context) {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            log.warn("Gemini API key not configured, returning default response");
            return "Xin lỗi, tính năng AI chưa được cấu hình. Vui lòng liên hệ quản trị viên.";
        }
        
        try {
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            
            // System instruction (nếu có)
            if (prompt != null && !prompt.isEmpty()) {
                Map<String, Object> systemContent = new HashMap<>();
                List<Map<String, Object>> systemParts = new ArrayList<>();
                Map<String, Object> systemPart = new HashMap<>();
                systemPart.put("text", prompt);
                systemParts.add(systemPart);
                systemContent.put("parts", systemParts);
                systemContent.put("role", "user");
                contents.add(systemContent);
            }
            
            // User message với context
            StringBuilder userText = new StringBuilder(userMessage);
            if (context != null && !context.isEmpty()) {
                userText.append("\n\nDữ liệu từ hệ thống:\n");
                userText.append(formatContext(context));
            }
            
            Map<String, Object> userContent = new HashMap<>();
            List<Map<String, Object>> userParts = new ArrayList<>();
            Map<String, Object> userPart = new HashMap<>();
            userPart.put("text", userText.toString());
            userParts.add(userPart);
            userContent.put("parts", userParts);
            userContent.put("role", "user");
            contents.add(userContent);
            
            requestBody.put("contents", contents);

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
            

            // Chỉ dùng 1 model: gemini-2.5-flash với v1
            String apiVersion = "v1";
            String modelName = "gemini-2.5-flash";
            
            String baseUrl = "https://generativelanguage.googleapis.com/" + apiVersion;
            String url = baseUrl + "/models/" + modelName + ":generateContent?key=" + geminiApiKey;
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "{}";
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    
                    // Extract text from response
                    JsonNode candidates = jsonNode.get("candidates");
                    if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                        JsonNode content = candidates.get(0).get("content");
                        if (content != null) {
                            JsonNode parts = content.get("parts");
                            if (parts != null && parts.isArray() && parts.size() > 0) {
                                JsonNode text = parts.get(0).get("text");
                                if (text != null) {
                                    log.info("Successfully used model: {} with API version: {}", modelName, apiVersion);
                                    return text.asText();
                                }
                            }
                        }
                    }
                    // Nếu không tìm thấy text trong response
                    log.warn("No text found in Gemini API response");
                    return "Xin lỗi, không nhận được phản hồi từ AI.";
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    throw new IOException("API error: " + errorBody);
                }
            }
        } catch (IOException e) {
            log.error("Error calling Gemini API", e);
            return "Xin lỗi, có lỗi xảy ra khi gọi AI. Vui lòng thử lại sau.";
        }
    }
    
    @Override
    public com.example.mini_mart.dto.response.AiIntentResponse parseIntent(String message) {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            log.warn("Gemini API key not configured, returning UNKNOWN intent");
            return com.example.mini_mart.dto.response.AiIntentResponse.builder()
                    .action("UNKNOWN")
                    .build();
        }
        
        try {
            // Prompt để AI trả về JSON intent
            String systemPrompt = "Bạn là AI Agent phân tích intent cho hệ thống quản lý kho siêu thị mini. " +
                    "Nhiệm vụ của bạn là phân tích câu hỏi tiếng Việt và trả về JSON intent. " +
                    "\n\n" +
                    "CÁC ACTION ĐƯỢC PHÉP:\n" +
                    "- EXPORT_PREVIEW: User muốn xem trước kết quả xuất kho (không ghi DB)\n" +
                    "- INVENTORY_QUERY: User hỏi đáp về dữ liệu kho (read-only)\n" +
                    "- UNKNOWN: Khi không đủ thông tin hoặc intent không rõ\n" +
                    "\n" +
                    "OUTPUT BẮT BUỘC:\n" +
                    "- CHỈ trả về JSON hợp lệ, KHÔNG có text ngoài JSON\n" +
                    "- Thiếu thông tin → set null\n" +
                    "- Không tự điền giá trị mặc định\n" +
                    "\n" +
                    "SCHEMA OUTPUT:\n" +
                    "\n" +
                    "EXPORT_PREVIEW:\n" +
                    "{\n" +
                    "  \"action\": \"EXPORT_PREVIEW\",\n" +
                    "  \"customer_name\": string | null,\n" +
                    "  \"products\": [{\"product_name\": string, \"quantity\": number}] | null\n" +
                    "}\n" +
                    "LƯU Ý: Nếu có nhiều sản phẩm (ví dụ: '5 chai Coca và 5 cái Bánh Oreo'), hãy trả về mảng products với tất cả sản phẩm.\n" +
                    "Nếu chỉ có 1 sản phẩm, vẫn trả về mảng products với 1 phần tử.\n" +
                    "\n" +
                    "INVENTORY_QUERY:\n" +
                    "{\n" +
                    "  \"action\": \"INVENTORY_QUERY\",\n" +
                    "  \"query_type\": \"STOCK | EXPIRE_SOON | TOP_EXPORT | INVENTORY_SUMMARY\",\n" +
                    "  \"product_name\": string | null,\n" +
                    "  \"time_range\": string | null\n" +
                    "}\n" +
                    "\n" +
                    "UNKNOWN:\n" +
                    "{\n" +
                    "  \"action\": \"UNKNOWN\"\n" +
                    "}\n" +
                    "\n" +
                    "VÍ DỤ:\n" +
                    "User: \"Xuất cho khách Nguyễn Văn B 5 chai coca thì hết bao nhiêu tiền?\"\n" +
                    "Output: {\"action\": \"EXPORT_PREVIEW\", \"customer_name\": \"Nguyễn Văn B\", \"products\": [{\"product_name\": \"coca\", \"quantity\": 5}]}\n" +
                    "\n" +
                    "User: \"Xuất cho anh Hoàng Văn Diện 5 chai Coca và 5 cái Bánh Oreo\"\n" +
                    "Output: {\"action\": \"EXPORT_PREVIEW\", \"customer_name\": \"Hoàng Văn Diện\", \"products\": [{\"product_name\": \"Coca\", \"quantity\": 5}, {\"product_name\": \"Bánh Oreo\", \"quantity\": 5}]}\n" +
                    "\n" +
                    "User: \"Xuất cho khách Hoàng Văn Diện 5 chai Coca\"\n" +
                    "Output: {\"action\": \"EXPORT_PREVIEW\", \"customer_name\": \"Hoàng Văn Diện\", \"products\": [{\"product_name\": \"Coca\", \"quantity\": 5}]}\n" +
                    "\n" +
                    "QUAN TRỌNG:\n" +
                    "- Tên khách hàng thường xuất hiện sau \"cho\", \"cho khách\", \"cho anh\", \"cho chị\"\n" +
                    "- Tên khách hàng thường là 2-4 từ tiếng Việt (ví dụ: \"Hoàng Văn Diện\", \"Nguyễn Văn B\")\n" +
                    "- Nếu có nhiều sản phẩm, hãy parse tất cả vào mảng products\n" +
                    "\n" +
                    "User: \"Tồn kho hiện tại?\"\n" +
                    "Output: {\"action\": \"INVENTORY_QUERY\", \"query_type\": \"INVENTORY_SUMMARY\"}\n" +
                    "\n" +
                    "User: \"Sản phẩm nào sắp hết hạn trong 3 ngày?\"\n" +
                    "Output: {\"action\": \"INVENTORY_QUERY\", \"query_type\": \"EXPIRE_SOON\", \"time_range\": \"3days\"}\n" +
                    "\n" +
                    "User: \"Sản phẩm nào sẽ hết hạn trong 3 tuần tới?\"\n" +
                    "Output: {\"action\": \"INVENTORY_QUERY\", \"query_type\": \"EXPIRE_SOON\", \"time_range\": \"3weeks\"}\n" +
                    "\n" +
                    "User: \"Sản phẩm nào sắp hết hạn trong 1 tháng?\"\n" +
                    "Output: {\"action\": \"INVENTORY_QUERY\", \"query_type\": \"EXPIRE_SOON\", \"time_range\": \"1month\"}\n" +
                    "\n" +
                    "LƯU Ý: time_range format:\n" +
                    "- \"3days\" hoặc \"3 ngày\" → \"3days\"\n" +
                    "- \"3weeks\" hoặc \"3 tuần\" → \"3weeks\" (1 tuần = 7 ngày)\n" +
                    "- \"1month\" hoặc \"1 tháng\" → \"1month\" (1 tháng = 30 ngày)\n" +
                    "\n" +
                    "User: \"Hôm nay xuất nhiều nhất là mặt hàng nào?\"\n" +
                    "Output: {\"action\": \"INVENTORY_QUERY\", \"query_type\": \"TOP_EXPORT\", \"time_range\": \"today\"}\n" +
                    "\n" +
                    "QUAN TRỌNG: Chỉ trả về JSON, không có text giải thích.";
            
            String jsonResponse = chatCompletionWithJsonResponse(systemPrompt, message);
            
            jsonResponse = cleanJsonResponse(jsonResponse);
            
            try {
                JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                var builder = com.example.mini_mart.dto.response.AiIntentResponse.builder();
                
                if (jsonNode.has("action")) {
                    builder.action(jsonNode.get("action").asText());
                }
                
                if (jsonNode.has("product_name")) {
                    JsonNode productNameNode = jsonNode.get("product_name");
                    if (!productNameNode.isNull()) {
                        builder.productName(productNameNode.asText());
                    }
                }
                
                if (jsonNode.has("quantity")) {
                    JsonNode quantityNode = jsonNode.get("quantity");
                    if (!quantityNode.isNull()) {
                        builder.quantity(quantityNode.asInt());
                    }
                }
                
                if (jsonNode.has("customer_name")) {
                    JsonNode customerNameNode = jsonNode.get("customer_name");
                    if (!customerNameNode.isNull()) {
                        builder.customerName(customerNameNode.asText());
                    }
                }
                
                if (jsonNode.has("products")) {
                    JsonNode productsNode = jsonNode.get("products");
                    if (!productsNode.isNull() && productsNode.isArray()) {
                        List<com.example.mini_mart.dto.response.AiIntentResponse.ProductQuantity> products = new ArrayList<>();
                        for (JsonNode productNode : productsNode) {
                            if (productNode.has("product_name") && productNode.has("quantity")) {
                                String pn = productNode.get("product_name").asText();
                                Integer qty = productNode.get("quantity").asInt();
                                if (pn != null && !pn.isEmpty() && qty != null && qty > 0) {
                                    products.add(com.example.mini_mart.dto.response.AiIntentResponse.ProductQuantity.builder()
                                            .productName(pn)
                                            .quantity(qty)
                                            .build());
                                }
                            }
                        }
                        if (!products.isEmpty()) {
                            builder.products(products);
                        }
                    }
                }
                
                if (jsonNode.has("query_type")) {
                    JsonNode queryTypeNode = jsonNode.get("query_type");
                    if (!queryTypeNode.isNull()) {
                        builder.queryType(queryTypeNode.asText());
                    }
                }
                
                if (jsonNode.has("time_range")) {
                    JsonNode timeRangeNode = jsonNode.get("time_range");
                    if (!timeRangeNode.isNull()) {
                        builder.timeRange(timeRangeNode.asText());
                    }
                }
                
                return builder.build();
            } catch (Exception e) {
                log.error("Failed to parse AI intent JSON response: {}", jsonResponse, e);
                return com.example.mini_mart.dto.response.AiIntentResponse.builder()
                        .action("UNKNOWN")
                        .build();
            }
        } catch (Exception e) {
            log.error("Error parsing intent from message: {}", message, e);
            return com.example.mini_mart.dto.response.AiIntentResponse.builder()
                    .action("UNKNOWN")
                    .build();
        }
    }

    private String chatCompletionWithJsonResponse(String systemPrompt, String userMessage) {
        try {
            waitForRateLimit();
            
            String apiVersion = "v1";
            String modelName = "gemini-2.5-flash";
            
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            
            Map<String, Object> systemContent = new HashMap<>();
            List<Map<String, Object>> systemParts = new ArrayList<>();
            Map<String, Object> systemPart = new HashMap<>();
            systemPart.put("text", systemPrompt);
            systemParts.add(systemPart);
            systemContent.put("parts", systemParts);
            systemContent.put("role", "user");
            contents.add(systemContent);
            
            Map<String, Object> userContent = new HashMap<>();
            List<Map<String, Object>> userParts = new ArrayList<>();
            Map<String, Object> userPart = new HashMap<>();
            userPart.put("text", userMessage);
            userParts.add(userPart);
            userContent.put("parts", userParts);
            userContent.put("role", "user");
            contents.add(userContent);
            
            requestBody.put("contents", contents);
            
            String jsonInstruction = "\n\nQUAN TRỌNG: Bạn PHẢI trả về CHỈ JSON hợp lệ, không có text giải thích nào khác. JSON phải bắt đầu bằng { và kết thúc bằng }.";
            if (systemParts != null && systemParts.size() > 0) {
                Map<String, Object> firstPart = systemParts.get(0);
                if (firstPart.containsKey("text")) {
                    String currentText = (String) firstPart.get("text");
                    firstPart.put("text", currentText + jsonInstruction);
                }
            }
            
            String baseUrl = "https://generativelanguage.googleapis.com/" + apiVersion;
            String url = baseUrl + "/models/" + modelName + ":generateContent?key=" + geminiApiKey;
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    throw new IOException("API error: " + errorBody);
                }
                String responseBody = response.body() != null ? response.body().string() : "{}";
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                JsonNode textNode = jsonNode.at("/candidates/0/content/parts/0/text");
                if (textNode.isTextual()) {
                    log.info("Successfully used model: {} with API version: {} for intent parsing", modelName, apiVersion);
                    return textNode.asText();
                }
                throw new IOException("Invalid response format from Gemini API");
            }
        } catch (Exception e) {
            log.error("Unexpected error in chatCompletionWithJsonResponse", e);
            return "{\"action\": \"UNKNOWN\"}";
        }
    }
    
    @Override
    @Deprecated
    public String analyzeIntent(String message) {
        AiIntentResponse intent = parseIntent(message);
        if ("EXPORT_PREVIEW".equals(intent.getAction())) {
            return "EXPORT_PREVIEW";
        }
        return "Q_AND_A";
    }
    
    private String formatContext(Map<String, Object> context) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
    

    private String cleanJsonResponse(String jsonResponse) {
        if (jsonResponse == null) {
            return null;
        }
        
        String cleaned = jsonResponse.trim();
        
        if (cleaned.startsWith("```")) {
            int startIdx = cleaned.indexOf('\n');
            if (startIdx == -1) {
                startIdx = cleaned.indexOf('\r');
            }
            if (startIdx != -1) {
                cleaned = cleaned.substring(startIdx + 1).trim();
            } else {
                cleaned = cleaned.replaceFirst("^```(json)?", "").trim();
            }
            
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
            }
        }
        
        return cleaned;
    }
}

