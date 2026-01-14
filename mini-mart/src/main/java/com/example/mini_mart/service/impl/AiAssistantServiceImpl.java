package com.example.mini_mart.service.impl;

import com.example.mini_mart.dto.response.*;
import com.example.mini_mart.entity.Category;
import com.example.mini_mart.entity.Customer;
import com.example.mini_mart.entity.Product;
import com.example.mini_mart.repository.CategoryRepository;
import com.example.mini_mart.repository.CustomerRepository;
import com.example.mini_mart.repository.ProductRepository;
import com.example.mini_mart.service.AiAssistantService;
import com.example.mini_mart.service.GeminiService;
import com.example.mini_mart.service.QdrantService;
import com.example.mini_mart.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation của AI Assistant Service
 *
 * QUAN TRỌNG:
 * - AI chỉ đọc và diễn giải dữ liệu, KHÔNG được ghi DB
 * - Tất cả số liệu phải lấy từ DB thật, không được bịa
 * - Export Preview chỉ là preview, không tạo phiếu xuất thật
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiAssistantServiceImpl implements AiAssistantService {
    
    private final GeminiService geminiService;
    private final ReportService reportService;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final CategoryRepository categoryRepository;
    private final QdrantService qdrantService;
    
    private static final String PRODUCTS_COLLECTION = "products";
    private static final String CUSTOMERS_COLLECTION = "customers";
    
    
    @Override
    public AiAssistantResponse processMessage(String message) {
        AiIntentResponse intent = geminiService.parseIntent(message);
        
        log.info("AI parsed intent: action={}, productName={}, quantity={}, customerName={}, products={}, queryType={}, timeRange={}",
                intent.getAction(), intent.getProductName(), intent.getQuantity(), 
                intent.getCustomerName(), intent.getProducts(), intent.getQueryType(), intent.getTimeRange());
        
        if ("EXPORT_PREVIEW".equals(intent.getAction())) {
            return handleExportPreview(intent);
        } else if ("INVENTORY_QUERY".equals(intent.getAction())) {
            return handleInventoryQuery(intent);
        } else {
            return AiAssistantResponse.builder()
                    .action("Q_AND_A")
                    .answer("Xin lỗi, tôi chưa hiểu rõ yêu cầu của bạn. Vui lòng thử lại với câu hỏi cụ thể hơn.")
                    .note("Intent không rõ ràng")
                    .build();
        }
    }
    
    /**
     * Xử lý Q&A về dữ liệu kho
     */
    private AiAssistantResponse handleQAndA(String message) {
        Map<String, Object> context = new HashMap<>();
        String lowerMessage = message.toLowerCase();

        if (containsKeywords(lowerMessage, "sắp hết hạn", "hết hạn", "expir") 
                && !containsKeywords(lowerMessage, "hết hàng", "không còn", "out of stock")) {

            int days = 3;

            LocalDate beforeDate = LocalDate.now().plusDays(days);
            List<ExpiringItemResponse> expiringItems = reportService.getExpiringItems(beforeDate);
            context.put("expiringItems", expiringItems);
            context.put("days", days);
            
        } else if (containsKeywords(lowerMessage, "hết hàng", "không còn", "out of stock", "sắp hết hàng")) {
            List<StockReportResponse> stockData = reportService.getCurrentStock();
            List<StockReportResponse> outOfStockItems = stockData.stream()
                    .filter(s -> s.getTotalStock() == 0)
                    .collect(Collectors.toList());
            context.put("outOfStockItems", outOfStockItems);
            context.put("currentStock", stockData);
            
        } else if (containsKeywords(lowerMessage, "xuất nhiều", "bán nhiều", "top", "nhiều nhất", "hôm nay") 
                || containsKeywords(lowerMessage, "xuất ít", "ít nhất")) {
            boolean isMin = containsKeywords(lowerMessage, "ít nhất", "xuất ít");
            Instant startDate, endDate;
            if (containsKeywords(lowerMessage, "hôm nay", "today")) {
                LocalDateTime todayStart = LocalDate.now().atStartOfDay();
                LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
                startDate = todayStart.atZone(ZoneId.systemDefault()).toInstant();
                endDate = todayEnd.atZone(ZoneId.systemDefault()).toInstant();
            } else {
                LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
                LocalDateTime now = LocalDateTime.now();
                startDate = weekAgo.atZone(ZoneId.systemDefault()).toInstant();
                endDate = now.atZone(ZoneId.systemDefault()).toInstant();
            }
            
            List<TopExportedProductResponse> allProducts = reportService.getTopExportedProducts(startDate, endDate, 100);
            
            if (isMin) {
                List<TopExportedProductResponse> minProducts = allProducts.stream()
                        .sorted(Comparator.comparingInt(TopExportedProductResponse::getTotalQuantity))
                        .limit(5)
                        .collect(Collectors.toList());
                context.put("topExportedProducts", minProducts);
                context.put("isMin", true);
            } else {
                List<TopExportedProductResponse> topProducts = allProducts.stream()
                        .sorted(Comparator.comparingInt(TopExportedProductResponse::getTotalQuantity).reversed())
                        .limit(10)
                        .collect(Collectors.toList());
                context.put("topExportedProducts", topProducts);
                context.put("isMin", false);
            }
            
            context.put("allExportedProducts", allProducts);
            context.put("totalProductsCount", allProducts.size());
            context.put("totalQuantity", allProducts.stream()
                    .mapToInt(TopExportedProductResponse::getTotalQuantity)
                    .sum());
            
        } else if (containsKeywords(lowerMessage, "nhóm", "danh mục", "category", "loại")) {

            List<Float> messageEmbedding = geminiService.generateEmbedding(message);
            if (!messageEmbedding.isEmpty()) {
                // TODO: Nếu có category collection trong Qdrant, dùng vector search
                // Hiện tại fallback: lấy tất cả categories và để AI tự chọn
                List<Category> allCategories = categoryRepository.findAll();
                context.put("allCategories", allCategories);
            }
            
        } else {

            List<StockReportResponse> stockData = reportService.getCurrentStock();
            context.put("currentStock", stockData);
            
            List<Float> messageEmbedding = geminiService.generateEmbedding(message);
            List<Integer> productIds = new ArrayList<>();
            
            if (!messageEmbedding.isEmpty()) {
                // Vector search trong Qdrant
                List<Map<String, Object>> productResults = qdrantService.searchSimilar(
                        PRODUCTS_COLLECTION, messageEmbedding, 5);
                
                log.debug("Vector search returned {} results for query: {}", productResults.size(), message);
                
                if (!productResults.isEmpty()) {
                    // Lấy product IDs từ kết quả
                    productIds = productResults.stream()
                            .map(result -> {
                                Map<String, Object> payload = (Map<String, Object>) result.get("payload");
                                if (payload != null && payload.get("productId") != null) {
                                    Integer id = (Integer) payload.get("productId");
                                    log.debug("Found product ID: {} from vector search", id);
                                    return id;
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                }
            }
            

            if (!productIds.isEmpty()) {
                List<Product> products = productRepository.findAllById(productIds);
                Map<Integer, Integer> stockMap = stockData.stream()
                        .collect(Collectors.toMap(
                                StockReportResponse::getProductId,
                                StockReportResponse::getTotalStock,
                                (a, b) -> a
                        ));
                
                List<Map<String, Object>> productInfo = products.stream()
                        .map(p -> {
                            Map<String, Object> info = new HashMap<>();
                            info.put("id", p.getId());
                            info.put("name", p.getName());
                            info.put("sku", p.getSku());
                            info.put("stock", stockMap.getOrDefault(p.getId(), 0));
                            return info;
                        })
                        .collect(Collectors.toList());
                context.put("foundProducts", productInfo);
                log.debug("Added {} products to context", productInfo.size());
            } else {
                log.warn("No products found for query: {}", message);
            }
        }
        
        // Tạo prompt cho Gemini
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Bạn là trợ lý AI cho hệ thống quản lý kho siêu thị mini. ");
        promptBuilder.append("Nhiệm vụ của bạn là trả lời câu hỏi về tình trạng kho dựa trên dữ liệu thực tế từ hệ thống. ");
        promptBuilder.append("QUAN TRỌNG: Chỉ sử dụng số liệu từ dữ liệu được cung cấp, KHÔNG được bịa số. ");
        promptBuilder.append("Trả lời một cách tự nhiên, ngắn gọn, dễ hiểu bằng tiếng Việt. ");
        promptBuilder.append("KHÔNG liệt kê chi tiết kỹ thuật như 'currentStock', 'foundProducts', v.v. ");
        promptBuilder.append("Chỉ trả lời thông tin mà người dùng hỏi, không giải thích dữ liệu nguồn. ");
        
        // Hướng dẫn cụ thể dựa trên context có sẵn
        if (context.containsKey("expiringItems")) {
            promptBuilder.append("Người dùng hỏi về sản phẩm SẮP HẾT HẠN SỬ DỤNG (expiration date). ");
            promptBuilder.append("Hãy liệt kê các sản phẩm sắp hết hạn một cách rõ ràng, dễ đọc. ");
            promptBuilder.append("Nếu không có sản phẩm nào, hãy nói 'Không có sản phẩm nào sắp hết hạn trong khoảng thời gian này.' ");
            promptBuilder.append("QUAN TRỌNG: KHÔNG nhầm lẫn với 'hết hàng' (out of stock). ");
        } else if (context.containsKey("outOfStockItems")) {
            promptBuilder.append("Người dùng hỏi về sản phẩm HẾT HÀNG (out of stock, tồn kho = 0). ");
            promptBuilder.append("Hãy liệt kê các sản phẩm đã hết hàng (tồn kho = 0). ");
            promptBuilder.append("Nếu không có sản phẩm nào hết hàng, hãy nói 'Hiện tại không có sản phẩm nào hết hàng.' ");
            promptBuilder.append("QUAN TRỌNG: KHÔNG nhầm lẫn với 'sắp hết hạn' (expiring). ");
        } else if (context.containsKey("topExportedProducts")) {
            boolean isMin = context.containsKey("isMin") && (Boolean) context.get("isMin");
            if (isMin) {
                promptBuilder.append("Người dùng hỏi về sản phẩm XUẤT KHO ÍT NHẤT. ");
                promptBuilder.append("Hãy liệt kê các sản phẩm xuất kho ít nhất với số lượng cụ thể. ");
            } else {
                promptBuilder.append("Người dùng hỏi về sản phẩm XUẤT KHO NHIỀU NHẤT. ");
                promptBuilder.append("Hãy liệt kê các sản phẩm xuất kho nhiều nhất với số lượng cụ thể. ");
            }
            promptBuilder.append("Nếu người dùng hỏi 'gồm những sản phẩm nào', hãy liệt kê TẤT CẢ sản phẩm đã xuất kho từ dữ liệu 'allExportedProducts'. ");
            promptBuilder.append("Nếu người dùng hỏi về 'số lượng' vs 'số loại', hãy phân biệt rõ: ");
            promptBuilder.append("- 'Số lượng' = tổng số đơn vị sản phẩm đã xuất (totalQuantity). ");
            promptBuilder.append("- 'Số loại' = số lượng sản phẩm khác nhau đã xuất (totalProductsCount). ");
        } else if (context.containsKey("stockByCategory")) {
            promptBuilder.append("Người dùng hỏi về tồn kho theo danh mục. ");
            promptBuilder.append("Nếu hỏi 'bao nhiêu sản phẩm', hãy phân biệt: ");
            promptBuilder.append("- 'Số loại sản phẩm' = số lượng sản phẩm khác nhau trong danh mục. ");
            promptBuilder.append("- 'Số lượng tồn kho' = tổng số đơn vị sản phẩm. ");
            promptBuilder.append("Nếu hỏi 'gồm những loại nào', hãy liệt kê TẤT CẢ sản phẩm trong danh mục với số lượng tồn kho của từng sản phẩm. ");
        } else if (context.containsKey("foundProducts")) {
            promptBuilder.append("Người dùng hỏi về tồn kho của sản phẩm cụ thể. ");
            promptBuilder.append("Hãy trả lời về tồn kho của sản phẩm được tìm thấy. ");
            promptBuilder.append("Nếu có nhiều sản phẩm, hãy liệt kê ngắn gọn. ");
        } else if (context.containsKey("currentStock")) {
            promptBuilder.append("Người dùng hỏi về tồn kho. ");
            promptBuilder.append("Hãy trả lời tổng số sản phẩm có tồn kho (không phải số lượng đơn vị). ");
            promptBuilder.append("Ví dụ: 'Hiện tại có X sản phẩm có tồn kho trong hệ thống.' ");
        }
        
        promptBuilder.append("Nếu không có đủ dữ liệu để trả lời, hãy nói rõ 'Không đủ dữ liệu để trả lời câu hỏi này.' ");
        
        String prompt = promptBuilder.toString();
        
        log.debug("Context keys: {}", context.keySet());
        if (context.containsKey("foundProducts")) {
            log.debug("Found products in context: {}", context.get("foundProducts"));
        }
        
        String answer = geminiService.chatCompletion(prompt, message, context);
        
        return AiAssistantResponse.builder()
                .action("Q_AND_A")
                .answer(answer)
                .note("Dữ liệu được lấy từ hệ thống thực tế")
                .build();
    }

    private boolean containsKeywords(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @deprecated Không dùng regex nữa, dùng AI parseIntent
     */
    @Deprecated
    private int extractDays(String message) {
        // Tìm "X tuần" trước
        Pattern weekPattern = Pattern.compile("(\\d+)\\s*tuần", Pattern.CASE_INSENSITIVE);
        Matcher weekMatcher = weekPattern.matcher(message);
        if (weekMatcher.find()) {
            int weeks = Integer.parseInt(weekMatcher.group(1));
            return weeks * 7; // Convert tuần thành ngày
        }
        
        // Tìm "X ngày"
        Pattern dayPattern = Pattern.compile("(\\d+)\\s*ngày", Pattern.CASE_INSENSITIVE);
        Matcher dayMatcher = dayPattern.matcher(message);
        if (dayMatcher.find()) {
            return Integer.parseInt(dayMatcher.group(1));
        }
        
        return 0;
    }
    
    /**
     * @deprecated Không dùng regex nữa, dùng AI parseIntent
     */
    @Deprecated
    private String extractCategoryKeyword(String message) {
        // Pattern: tìm từ sau "nhóm", "danh mục", "category"
        Pattern pattern = Pattern.compile("(?:nhóm|danh mục|category|loại)\\s+([^\\s]+(?:\\s+[^\\s]+)?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
    
    /**
     * Xử lý Export Preview
     * Tìm product và customer bằng embedding + Qdrant, validate, tính toán nhưng KHÔNG tạo phiếu xuất
     * KHÔNG dùng regex, chỉ dùng embedding + Qdrant để match
     */
    private AiAssistantResponse handleExportPreview(com.example.mini_mart.dto.response.AiIntentResponse intent) {
        try {
            // Validate intent
            if (intent.getCustomerName() == null || intent.getCustomerName().isEmpty()) {
                return AiAssistantResponse.builder()
                        .action("EXPORT_PREVIEW")
                        .answer("Không tìm thấy tên khách hàng trong câu lệnh. Vui lòng thử lại.")
                        .build();
            }
            
            // Lấy danh sách sản phẩm từ products array hoặc fallback về product_name/quantity
            List<com.example.mini_mart.dto.response.AiIntentResponse.ProductQuantity> productList = new ArrayList<>();
            if (intent.getProducts() != null && !intent.getProducts().isEmpty()) {
                productList = intent.getProducts();
            } else if (intent.getProductName() != null && !intent.getProductName().isEmpty() 
                    && intent.getQuantity() != null && intent.getQuantity() > 0) {
                // Fallback: dùng product_name và quantity cũ
                productList.add(com.example.mini_mart.dto.response.AiIntentResponse.ProductQuantity.builder()
                        .productName(intent.getProductName())
                        .quantity(intent.getQuantity())
                        .build());
            }
            
            if (productList.isEmpty()) {
                return AiAssistantResponse.builder()
                        .action("EXPORT_PREVIEW")
                        .answer("Không tìm thấy sản phẩm hoặc số lượng hợp lệ trong câu lệnh. Vui lòng thử lại.")
                        .build();
            }
            
            // Tìm customer bằng embedding + Qdrant (KHÔNG dùng regex/keyword matching)
            Customer customer = findCustomerByEmbedding(intent.getCustomerName());
            if (customer == null) {
                log.warn("Could not find customer with name: '{}'", intent.getCustomerName());
                return AiAssistantResponse.builder()
                        .action("EXPORT_PREVIEW")
                        .answer("Không tìm thấy khách hàng phù hợp với '" + intent.getCustomerName() + "'. Vui lòng thử lại với tên khách hàng cụ thể hơn.")
                        .build();
            }
            
            // Lấy tồn kho thực tế
            List<StockReportResponse> stockData = reportService.getCurrentStock();
            Map<Integer, Integer> stockMap = stockData.stream()
                    .collect(Collectors.toMap(
                            StockReportResponse::getProductId,
                            StockReportResponse::getTotalStock,
                            (a, b) -> a
                    ));
            
            // Xử lý từng sản phẩm
            List<ExportPreviewData.ExportPreviewItem> items = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;
            boolean allEnough = true;
            List<String> notFoundProducts = new ArrayList<>();
            
            for (com.example.mini_mart.dto.response.AiIntentResponse.ProductQuantity pq : productList) {
                // Tìm product bằng embedding + Qdrant
                Product product = findProductByEmbedding(pq.getProductName());
                if (product == null) {
                    log.warn("Could not find product with name: '{}'", pq.getProductName());
                    notFoundProducts.add(pq.getProductName());
                    continue;
                }
                
                Integer currentStock = stockMap.getOrDefault(product.getId(), 0);
                String stockStatus = pq.getQuantity() <= currentStock ? "ENOUGH" : "INSUFFICIENT";
                if (!stockStatus.equals("ENOUGH")) {
                    allEnough = false;
                }
                
                BigDecimal unitPrice = product.getSellingPrice();
                BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(pq.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
                
                String note = stockStatus.equals("ENOUGH")
                        ? "Đủ tồn kho"
                        : String.format("Không đủ tồn kho. Cần: %d, Có: %d", pq.getQuantity(), currentStock);
                
                items.add(ExportPreviewData.ExportPreviewItem.builder()
                        .product(ExportPreviewData.ProductInfo.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .sku(product.getSku())
                                .build())
                        .quantity(pq.getQuantity())
                        .unitPrice(unitPrice)
                        .totalPrice(itemTotal)
                        .stockStatus(stockStatus)
                        .note(note)
                        .build());
            }
            
            if (!notFoundProducts.isEmpty()) {
                return AiAssistantResponse.builder()
                        .action("EXPORT_PREVIEW")
                        .answer("Không tìm thấy sản phẩm: " + String.join(", ", notFoundProducts) + ". Vui lòng thử lại.")
                        .build();
            }
            
            if (items.isEmpty()) {
                return AiAssistantResponse.builder()
                        .action("EXPORT_PREVIEW")
                        .answer("Không tìm thấy sản phẩm nào. Vui lòng thử lại.")
                        .build();
            }
            
            // Build preview data với nhiều sản phẩm
            ExportPreviewData preview = ExportPreviewData.builder()
                    .customer(ExportPreviewData.CustomerInfo.builder()
                            .id(customer.getId())
                            .name(customer.getFullName())
                            .build())
                    .items(items)
                    .totalAmount(totalAmount)
                    .overallStockStatus(allEnough ? "ENOUGH" : "INSUFFICIENT")
                    .note(allEnough ? "Tất cả sản phẩm đủ tồn kho" : "Một số sản phẩm không đủ tồn kho")
                    .build();
            
            // Tạo answer từ Gemini
            StringBuilder contextBuilder = new StringBuilder();
            contextBuilder.append("Khách hàng: ").append(customer.getFullName()).append("\n");
            contextBuilder.append("Danh sách sản phẩm:\n");
            for (ExportPreviewData.ExportPreviewItem item : items) {
                contextBuilder.append(String.format("- %s: %d x %s = %s (%s)\n",
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice(),
                        item.getStockStatus().equals("ENOUGH") ? "Đủ" : "Không đủ"));
            }
            contextBuilder.append("Tổng tiền: ").append(totalAmount).append("\n");
            contextBuilder.append("Trạng thái tồn kho: ").append(allEnough ? "Đủ" : "Không đủ");
            
            String prompt = "Bạn là trợ lý AI cho hệ thống quản lý kho. " +
                    "Hãy tóm tắt thông tin preview xuất kho một cách ngắn gọn, dễ hiểu bằng tiếng Việt. " +
                    "Nếu có nhiều sản phẩm, hãy liệt kê tất cả.";
            
            Map<String, Object> context = new HashMap<>();
            context.put("preview", contextBuilder.toString());
            String answer = geminiService.chatCompletion(prompt, "Tóm tắt thông tin preview xuất kho", context);
            
            return AiAssistantResponse.builder()
                    .action("EXPORT_PREVIEW")
                    .answer(answer)
                    .exportPreview(preview)
                    .note("Đây chỉ là preview, chưa tạo phiếu xuất. Vui lòng xác nhận để tạo phiếu xuất thật.")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error processing export preview", e);
            return AiAssistantResponse.builder()
                    .action("EXPORT_PREVIEW")
                    .answer("Có lỗi xảy ra khi xử lý preview xuất kho: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Xử lý Inventory Query
     * Dựa vào query_type để trả về dữ liệu kho tương ứng
     */
    private AiAssistantResponse handleInventoryQuery(com.example.mini_mart.dto.response.AiIntentResponse intent) {
        Map<String, Object> context = new HashMap<>();
        String queryType = intent.getQueryType();
        
        if ("STOCK".equals(queryType) || "INVENTORY_SUMMARY".equals(queryType)) {
            // Câu hỏi về tồn kho
            List<StockReportResponse> stockData = reportService.getCurrentStock();
            context.put("currentStock", stockData);
            
            // Nếu có product_name, tìm sản phẩm cụ thể
            if (intent.getProductName() != null && !intent.getProductName().isEmpty()) {
                Product product = findProductByEmbedding(intent.getProductName());
                if (product != null) {
                    Map<Integer, Integer> stockMap = stockData.stream()
                            .collect(Collectors.toMap(
                                    StockReportResponse::getProductId,
                                    StockReportResponse::getTotalStock,
                                    (a, b) -> a
                            ));
                    Map<String, Object> productInfo = new HashMap<>();
                    productInfo.put("id", product.getId());
                    productInfo.put("name", product.getName());
                    productInfo.put("sku", product.getSku());
                    productInfo.put("stock", stockMap.getOrDefault(product.getId(), 0));
                    context.put("foundProducts", List.of(productInfo));
                }
            }
            
        } else if ("EXPIRE_SOON".equals(queryType)) {
            // Câu hỏi về sản phẩm sắp hết hạn
            int days = 3; // Mặc định 3 ngày
            if (intent.getTimeRange() != null && !intent.getTimeRange().isEmpty()) {
                try {
                    String timeRange = intent.getTimeRange().toLowerCase().trim();
                    // Parse time_range như "3days", "3weeks", "1month", "3 tuần", "1 tháng"
                    if (timeRange.contains("week") || timeRange.contains("tuần")) {
                        // Tuần: extract số và nhân 7
                        String numberStr = timeRange.replaceAll("[^0-9]", "");
                        if (!numberStr.isEmpty()) {
                            int weeks = Integer.parseInt(numberStr);
                            days = weeks * 7;
                        }
                    } else if (timeRange.contains("month") || timeRange.contains("tháng")) {
                        // Tháng: extract số và nhân 30
                        String numberStr = timeRange.replaceAll("[^0-9]", "");
                        if (!numberStr.isEmpty()) {
                            int months = Integer.parseInt(numberStr);
                            days = months * 30;
                        }
                    } else {
                        // Ngày: extract số
                        String numberStr = timeRange.replaceAll("[^0-9]", "");
                        if (!numberStr.isEmpty()) {
                            days = Integer.parseInt(numberStr);
                        }
                    }
                } catch (NumberFormatException e) {
                    log.warn("Could not parse time_range: {}", intent.getTimeRange());
                }
            }
            LocalDate beforeDate = LocalDate.now().plusDays(days);
            List<ExpiringItemResponse> expiringItems = reportService.getExpiringItems(beforeDate);
            context.put("expiringItems", expiringItems);
            context.put("days", days);
            
        } else if ("TOP_EXPORT".equals(queryType)) {
            // Câu hỏi về sản phẩm xuất nhiều nhất
            Instant startDate, endDate;
            String timeRange = intent.getTimeRange();
            if (timeRange != null && (timeRange.contains("today") || timeRange.contains("hôm nay"))) {
                LocalDateTime todayStart = LocalDate.now().atStartOfDay();
                LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
                startDate = todayStart.atZone(ZoneId.systemDefault()).toInstant();
                endDate = todayEnd.atZone(ZoneId.systemDefault()).toInstant();
            } else {
                // Mặc định 7 ngày gần nhất
                LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
                LocalDateTime now = LocalDateTime.now();
                startDate = weekAgo.atZone(ZoneId.systemDefault()).toInstant();
                endDate = now.atZone(ZoneId.systemDefault()).toInstant();
            }
            
            List<TopExportedProductResponse> topProducts = reportService.getTopExportedProducts(startDate, endDate, 10);
            context.put("topExportedProducts", topProducts);
        }
        
        // Tạo prompt cho Gemini
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Bạn là trợ lý AI cho hệ thống quản lý kho siêu thị mini. ");
        promptBuilder.append("Nhiệm vụ của bạn là trả lời câu hỏi về tình trạng kho dựa trên dữ liệu thực tế từ hệ thống. ");
        promptBuilder.append("QUAN TRỌNG: Chỉ sử dụng số liệu từ dữ liệu được cung cấp, KHÔNG được bịa số. ");
        promptBuilder.append("Trả lời một cách tự nhiên, ngắn gọn, dễ hiểu bằng tiếng Việt. ");
        
        if (context.containsKey("expiringItems")) {
            promptBuilder.append("Người dùng hỏi về sản phẩm SẮP HẾT HẠN SỬ DỤNG. ");
            promptBuilder.append("Hãy liệt kê các sản phẩm sắp hết hạn một cách rõ ràng, dễ đọc. ");
        } else if (context.containsKey("topExportedProducts")) {
            promptBuilder.append("Người dùng hỏi về sản phẩm XUẤT KHO NHIỀU NHẤT. ");
            promptBuilder.append("Hãy liệt kê các sản phẩm xuất kho nhiều nhất với số lượng cụ thể. ");
        } else if (context.containsKey("foundProducts")) {
            promptBuilder.append("Người dùng hỏi về tồn kho của sản phẩm cụ thể. ");
            promptBuilder.append("Hãy trả lời về tồn kho của sản phẩm được tìm thấy. ");
        } else {
            promptBuilder.append("Người dùng hỏi về tồn kho. ");
            promptBuilder.append("Hãy trả lời tổng số sản phẩm có tồn kho (không phải số lượng đơn vị). ");
        }
        
        promptBuilder.append("Nếu không có đủ dữ liệu để trả lời, hãy nói rõ 'Không đủ dữ liệu để trả lời câu hỏi này.' ");
        
        String prompt = promptBuilder.toString();
        String answer = geminiService.chatCompletion(prompt, "Trả lời câu hỏi về kho", context);
        
        return AiAssistantResponse.builder()
                .action("INVENTORY_QUERY")
                .answer(answer)
                .note("Dữ liệu được lấy từ hệ thống thực tế")
                .build();
    }
    
    /**
     * Tìm customer bằng embedding + Qdrant, với fallback keyword search
     * Strategy: Vector search trước, nếu không tìm thấy thì dùng keyword search trong DB
     */
    private Customer findCustomerByEmbedding(String customerName) {
        // Strategy 1: Vector search trong Qdrant
        List<Float> embedding = geminiService.generateEmbedding(customerName);
        if (!embedding.isEmpty()) {
            List<Map<String, Object>> results = qdrantService.searchSimilar(CUSTOMERS_COLLECTION, embedding, 3);
            log.debug("Vector search returned {} results for customer: {}", results.size(), customerName);
            if (!results.isEmpty()) {
                // Lấy customer ID từ kết quả tốt nhất
                for (Map<String, Object> result : results) {
                    log.debug("Processing result: {}", result);
                    Map<String, Object> payload = (Map<String, Object>) result.get("payload");
                    log.debug("Payload: {}", payload);
                    
                    if (payload != null) {
                        // Thử nhiều cách để lấy customerId
                        Integer customerId = null;
                        if (payload.get("customerId") != null) {
                            Object idObj = payload.get("customerId");
                            if (idObj instanceof Integer) {
                                customerId = (Integer) idObj;
                            } else if (idObj instanceof Number) {
                                customerId = ((Number) idObj).intValue();
                            }
                        }
                        
                        if (customerId != null) {
                            Customer customer = customerRepository.findById(customerId).orElse(null);
                            if (customer != null) {
                                log.info("Found customer via vector search: {} (ID: {})", customer.getFullName(), customerId);
                                return customer;
                            } else {
                                log.warn("Customer with ID {} not found in database", customerId);
                            }
                        } else {
                            log.warn("No customerId found in payload: {}", payload);
                        }
                    } else {
                        log.warn("Payload is null in result: {}", result);
                    }
                }
            } else {
                log.warn("Vector search returned empty results for customer: {}", customerName);
            }
        } else {
            log.warn("Failed to generate embedding for customer: {}", customerName);
        }
        
        // Strategy 2: Fallback - Keyword search trong DB (không phải regex, chỉ search DB)
        log.info("Vector search failed, trying keyword search for: {}", customerName);
        List<Customer> customers = customerRepository.findByFullNameOrPhoneContainingIgnoreCase(customerName);
        if (!customers.isEmpty()) {
            log.debug("Found customer via keyword search: {} (ID: {})", 
                    customers.get(0).getFullName(), customers.get(0).getId());
            return customers.get(0);
        }
        
        // Strategy 3: Word-by-word search (tìm theo từng từ trong tên)
        String[] words = customerName.trim().split("\\s+");
        if (words.length >= 2) {
            // Thử tìm với 2 từ cuối (họ + tên)
            String lastName = words[words.length - 2] + " " + words[words.length - 1];
            customers = customerRepository.findByFullNameOrPhoneContainingIgnoreCase(lastName);
            if (!customers.isEmpty()) {
                log.debug("Found customer via last name search '{}': {} (ID: {})", 
                        lastName, customers.get(0).getFullName(), customers.get(0).getId());
                return customers.get(0);
            }
        }
        
        log.warn("Could not find customer with name: '{}'", customerName);
        return null;
    }
    
    /**
     * Tìm product bằng embedding + Qdrant, với fallback keyword search
     * Strategy: Vector search trước, nếu không tìm thấy thì dùng keyword search trong DB
     */
    private Product findProductByEmbedding(String productName) {
        // Strategy 1: Vector search trong Qdrant
        List<Float> embedding = geminiService.generateEmbedding(productName);
        if (!embedding.isEmpty()) {
            List<Map<String, Object>> results = qdrantService.searchSimilar(PRODUCTS_COLLECTION, embedding, 3);
            if (!results.isEmpty()) {
                // Lấy product ID từ kết quả tốt nhất
                for (Map<String, Object> result : results) {
                    Map<String, Object> payload = (Map<String, Object>) result.get("payload");
                    if (payload != null && payload.get("productId") != null) {
                        Integer productId = (Integer) payload.get("productId");
                        Product product = productRepository.findById(productId).orElse(null);
                        if (product != null && product.getIsActive()) {
                            log.debug("Found product via vector search: {} (ID: {})", product.getName(), productId);
                            return product;
                        }
                    }
                }
            }
        }
        
        // Strategy 2: Fallback - Keyword search trong DB (không phải regex, chỉ search DB)
        log.debug("Vector search failed, trying keyword search for: {}", productName);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(productName);
        if (!products.isEmpty()) {
            log.debug("Found product via keyword search: {} (ID: {})", 
                    products.get(0).getName(), products.get(0).getId());
            return products.get(0);
        }
        
        // Strategy 3: Word-by-word search (tìm theo từng từ trong tên sản phẩm)
        String[] words = productName.trim().split("\\s+");
        for (String word : words) {
            if (word.length() > 2) {
                products = productRepository.findByNameContainingIgnoreCase(word);
                if (!products.isEmpty()) {
                    log.debug("Found product via word search '{}': {} (ID: {})", 
                            word, products.get(0).getName(), products.get(0).getId());
                    return products.get(0);
                }
            }
        }
        
        log.warn("Could not find product with name: '{}'", productName);
        return null;
    }
    
    /**
     * @deprecated Không dùng regex nữa, dùng AI parseIntent
     */
    @Deprecated
    private String extractProductKeyword(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Pattern 1: Tìm sản phẩm sau số lượng (ví dụ: "2 cái Bánh Oreo", "5 chai Coca", "5 sản phẩm Bánh Oreo")
        Pattern pattern1 = Pattern.compile("(\\d+)\\s*(?:chai|lon|thùng|gói|hộp|cái|kg|g|ml|lít|l|sản phẩm)\\s+([^\\s]+(?:\\s+[^\\s]+){0,3})", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(message);
        if (matcher1.find()) {
            String product = matcher1.group(2).trim();
            // Loại bỏ các từ không phải tên sản phẩm
            product = product.replaceAll("^(cho|anh|chị|ông|bà|khách)\\s+", "");
            if (!product.isEmpty() && product.length() > 2) {
                return product;
            }
        }
        
        // Pattern 2: Tìm từ khóa sản phẩm phổ biến (bánh, nước, kẹo, sữa, etc.) và lấy từ sau nó
        String[] productKeywords = {"bánh", "nước", "kẹo", "sữa", "mì", "coca", "pepsi", "snack", "bim bim"};
        for (String keyword : productKeywords) {
            if (lowerMessage.contains(keyword)) {
                Pattern pattern2 = Pattern.compile(keyword + "\\s+([^\\s]+(?:\\s+[^\\s]+){0,2})", Pattern.CASE_INSENSITIVE);
                Matcher matcher2 = pattern2.matcher(message);
                if (matcher2.find()) {
                    String product = matcher2.group(1).trim();
                    // Loại bỏ số lượng nếu có
                    product = product.replaceAll("\\s*\\d+\\s*(chai|lon|thùng|gói|hộp|cái|kg|g|ml|lít|l)\\s*", "");
                    if (!product.isEmpty()) {
                        return keyword + " " + product;
                    }
                }
            }
        }
        
        // Pattern 3: Tìm sản phẩm sau tên khách hàng
        // Ví dụ: "cho anh Hoàng Văn Diện Bánh Oreo 2 cái" hoặc "cho anh Hoàng Văn Diện Bánh Oreo 5 sản phẩm"
        Pattern customerPattern = Pattern.compile("(?:cho|khách)\\s+(?:anh|chị|ông|bà)?\\s*([A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]+(?:\\s+[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]+){1,3})", Pattern.CASE_INSENSITIVE);
        Matcher customerMatcher = customerPattern.matcher(message);
        if (customerMatcher.find()) {
            int customerEnd = customerMatcher.end();
            String afterCustomer = message.substring(customerEnd).trim();
            // Loại bỏ số lượng ở cuối nếu có (ví dụ: "2 cái", "5 sản phẩm")
            afterCustomer = afterCustomer.replaceAll("\\s*\\d+\\s*(chai|lon|thùng|gói|hộp|cái|kg|g|ml|lít|l|sản phẩm)\\s*$", "");
            // Tìm sản phẩm sau tên khách hàng (có thể là 1-3 từ)
            Pattern productPattern = Pattern.compile("([A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]+(?:\\s+[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]+){0,2})", Pattern.CASE_INSENSITIVE);
            Matcher productMatcher = productPattern.matcher(afterCustomer);
            if (productMatcher.find()) {
                String product = productMatcher.group(1).trim();
                if (!product.isEmpty() && product.length() > 2) {
                    return product;
                }
            }
        }
        
        return null;
    }
    
    /**
     * @deprecated Không dùng regex nữa, dùng AI parseIntent
     */
    @Deprecated
    private String extractCustomerKeyword(String message) {
        // Pattern 1: Tìm tên sau "cho", "khách" (có thể có "anh", "chị", "ông", "bà" ở giữa)
        Pattern pattern1 = Pattern.compile("(?:cho|khách)\\s+(?:anh|chị|ông|bà)?\\s*([A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]+(?:\\s+[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]+){0,3})", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(message);
        if (matcher1.find()) {
            String customerName = matcher1.group(1).trim();
            // Loại bỏ các từ không phải tên (như "Bánh", "Oreo" nếu bị match nhầm)
            if (customerName.length() > 2 && !customerName.toLowerCase().matches(".*(bánh|nước|kẹo|sữa|mì|coca|pepsi|snack|bim bim).*")) {
                return customerName;
            }
        }
        
        // Pattern 2: Tìm tên người Việt (thường có 2-4 từ, bắt đầu bằng chữ hoa)
        Pattern pattern2 = Pattern.compile("([A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]+\\s+[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]+(?:\\s+[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]+){0,2})", Pattern.CASE_INSENSITIVE);
        Matcher matcher2 = pattern2.matcher(message);
        while (matcher2.find()) {
            String candidate = matcher2.group(1).trim();
            // Kiểm tra xem có phải tên khách hàng không (không phải tên sản phẩm)
            String lowerCandidate = candidate.toLowerCase();
            if (!lowerCandidate.matches(".*(bánh|nước|kẹo|sữa|mì|coca|pepsi|snack|bim bim|oreo|cola).*") 
                && candidate.length() >= 5) {
                return candidate;
            }
        }
        
        return null;
    }
    
    /**
     * @deprecated Không dùng regex nữa, dùng AI parseIntent
     */
    @Deprecated
    private Integer extractQuantity(String message) {
        // Pattern 1: Tìm số với đơn vị (bao gồm "sản phẩm", "cái", "chai", etc.)
        Pattern pattern = Pattern.compile("(\\d+)\\s*(?:chai|lon|thùng|gói|hộp|cái|kg|g|ml|lít|l|sản phẩm)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        
        // Fallback: tìm số đơn giản
        pattern = Pattern.compile("(\\d+)");
        matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        
        return null;
    }
    
    /**
     * @deprecated Không dùng keyword matching nữa, dùng findProductByEmbedding
     */
    @Deprecated
    private Product findProduct(String productKeyword, String fullMessage) {
        // Strategy 1: Keyword matching
        List<Product> products = productRepository.findByNameContainingIgnoreCase(productKeyword);
        if (!products.isEmpty()) {
            log.debug("Found product via keyword search '{}': {} (ID: {})", 
                    productKeyword, products.get(0).getName(), products.get(0).getId());
            return products.get(0);
        }
        
        // Strategy 2: Word-by-word search
        String[] words = productKeyword.split("\\s+");
        for (String word : words) {
            if (word.length() > 2) {
                products = productRepository.findByNameContainingIgnoreCase(word);
                if (!products.isEmpty()) {
                    log.debug("Found product via word search '{}': {} (ID: {})", 
                            word, products.get(0).getName(), products.get(0).getId());
                    return products.get(0);
                }
            }
        }
        
        // Strategy 3: Vector search
        List<Float> embedding = geminiService.generateEmbedding(productKeyword);
        if (!embedding.isEmpty()) {
            List<Map<String, Object>> results = qdrantService.searchSimilar(PRODUCTS_COLLECTION, embedding, 3);
            for (Map<String, Object> result : results) {
                Map<String, Object> payload = (Map<String, Object>) result.get("payload");
                if (payload != null && payload.get("productId") != null) {
                    Integer productId = (Integer) payload.get("productId");
                    Product product = productRepository.findById(productId).orElse(null);
                    if (product != null && product.getIsActive()) {
                        log.debug("Found product via vector search: {} (ID: {})", product.getName(), productId);
                        return product;
                    }
                }
            }
        }
        
        log.warn("Could not find product with keyword: '{}'", productKeyword);
        return null;
    }
    
    /**
     * @deprecated Không dùng keyword matching nữa, dùng findCustomerByEmbedding
     */
    @Deprecated
    private Customer findCustomer(String customerKeyword, String fullMessage) {
        // Strategy 1: Keyword matching
        List<Customer> customers = customerRepository.findByFullNameOrPhoneContainingIgnoreCase(customerKeyword);
        if (!customers.isEmpty()) {
            log.debug("Found customer via keyword search: {} (ID: {})", 
                    customers.get(0).getFullName(), customers.get(0).getId());
            return customers.get(0);
        }
        
        // Strategy 2: Word-by-word search
        String[] words = customerKeyword.split("\\s+");
        if (words.length >= 2) {
            String lastName = words[words.length - 2] + " " + words[words.length - 1];
            customers = customerRepository.findByFullNameOrPhoneContainingIgnoreCase(lastName);
            if (!customers.isEmpty()) {
                log.debug("Found customer via last name search '{}': {} (ID: {})", 
                        lastName, customers.get(0).getFullName(), customers.get(0).getId());
                return customers.get(0);
            }
        }
        for (String word : words) {
            if (word.length() > 2) {
                customers = customerRepository.findByFullNameOrPhoneContainingIgnoreCase(word);
                if (!customers.isEmpty()) {
                    log.debug("Found customer via word search '{}': {} (ID: {})", 
                            word, customers.get(0).getFullName(), customers.get(0).getId());
                    return customers.get(0);
                }
            }
        }
        
        // Strategy 3: Vector search
        List<Float> embedding = geminiService.generateEmbedding(fullMessage);
        if (!embedding.isEmpty()) {
            List<Map<String, Object>> results = qdrantService.searchSimilar(CUSTOMERS_COLLECTION, embedding, 3);
            for (Map<String, Object> result : results) {
                Map<String, Object> payload = (Map<String, Object>) result.get("payload");
                if (payload != null && payload.get("customerId") != null) {
                    Integer customerId = (Integer) payload.get("customerId");
                    Customer customer = customerRepository.findById(customerId).orElse(null);
                    if (customer != null) {
                        log.debug("Found customer via vector search: {} (ID: {})", 
                                customer.getFullName(), customerId);
                        return customer;
                    }
                }
            }
        }
        
        log.warn("Could not find customer with keyword: '{}'", customerKeyword);
        return null;
    }
}

