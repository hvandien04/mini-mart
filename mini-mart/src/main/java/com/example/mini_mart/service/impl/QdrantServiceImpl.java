package com.example.mini_mart.service.impl;

import com.example.mini_mart.service.QdrantService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QdrantServiceImpl implements QdrantService {
    
    @Value("${qdrant.url:http://qdrant:6333}")
    private String qdrantUrl;
    
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void createCollectionIfNotExists(String collectionName, int vectorSize) {
        try {
            String checkUrl = qdrantUrl + "/collections/" + collectionName;
            Request checkRequest = new Request.Builder()
                    .url(checkUrl)
                    .get()
                    .build();
            
            try (Response response = httpClient.newCall(checkRequest).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "{}";
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    
                    JsonNode config = jsonNode.get("config");
                    if (config != null) {
                        JsonNode vectors = config.get("params");
                        if (vectors != null && vectors.get("vectors") != null) {
                            JsonNode vectorsConfig = vectors.get("vectors");
                            if (vectorsConfig.has("size")) {
                                int existingSize = vectorsConfig.get("size").asInt();
                                if (existingSize != vectorSize) {
                                    log.error("Collection {} exists with dimension {}, but expected {}. " +
                                            "Please delete the collection manually or use a different name.", 
                                            collectionName, existingSize, vectorSize);
                                    throw new IOException(String.format(
                                            "Dimension mismatch: collection %s has dimension %d, expected %d",
                                            collectionName, existingSize, vectorSize));
                                }
                            }
                        }
                    }
                    
                    log.info("Collection {} already exists with correct dimension {}", collectionName, vectorSize);
                    return;
                }
            }
            
            Map<String, Object> config = new HashMap<>();
            config.put("vectors", Map.of("size", vectorSize, "distance", "Cosine"));
            
            String createUrl = qdrantUrl + "/collections/" + collectionName;
            String jsonBody = objectMapper.writeValueAsString(config);
            
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(createUrl)
                    .put(body)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    log.info("Collection {} created successfully", collectionName);
                } else {
                    log.error("Failed to create collection {}: {}", collectionName, 
                            response.body() != null ? response.body().string() : "Unknown error");
                }
            }
        } catch (IOException e) {
            log.error("Error creating collection {}", collectionName, e);
        }
    }
    
    @Override
    public void upsertPoint(String collectionName, Object pointId, List<Float> vector, Map<String, Object> metadata) {
        try {
            Map<String, Object> point = new HashMap<>();
            Object id;
            if (pointId instanceof String) {
                String strId = (String) pointId;
                if (strId.contains("_")) {
                    String numPart = strId.substring(strId.lastIndexOf("_") + 1);
                    try {
                        id = Integer.parseInt(numPart);
                    } catch (NumberFormatException e) {
                        log.error("Invalid point ID format: {}. Must be integer or UUID", pointId);
                        return;
                    }
                } else {
                    try {
                        id = Integer.parseInt(strId);
                    } catch (NumberFormatException e) {
                        log.error("Invalid point ID format: {}. Must be integer or UUID", pointId);
                        return;
                    }
                }
            } else {
                id = pointId;
            }
            point.put("id", id);
            point.put("vector", vector);
            if (metadata != null && !metadata.isEmpty()) {
                point.put("payload", metadata);
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("points", List.of(point));
            
            String url = qdrantUrl + "/collections/" + collectionName + "/points";
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to upsert point {} in collection {}: {}", pointId, collectionName,
                            response.body() != null ? response.body().string() : "Unknown error");
                }
            }
        } catch (IOException e) {
            log.error("Error upserting point {} in collection {}", pointId, collectionName, e);
        }
    }
    
    @Override
    public List<Map<String, Object>> searchSimilar(String collectionName, List<Float> queryVector, int limit) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("vector", queryVector);
            requestBody.put("limit", limit);
            requestBody.put("with_payload", true);
            
            String url = qdrantUrl + "/collections/" + collectionName + "/points/search";
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to search in collection {}: {}", collectionName,
                            response.body() != null ? response.body().string() : "Unknown error");
                    return new ArrayList<>();
                }
                
                String responseBody = response.body() != null ? response.body().string() : "[]";
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                
                List<Map<String, Object>> results = new ArrayList<>();
                if (jsonNode.isArray()) {
                    for (JsonNode result : jsonNode) {
                        Map<String, Object> resultMap = new HashMap<>();
                        JsonNode idNode = result.get("id");
                        if (idNode.isInt()) {
                            resultMap.put("id", idNode.asInt());
                        } else {
                            resultMap.put("id", idNode.asText());
                        }
                        resultMap.put("score", result.get("score").asDouble());
                        
                        JsonNode payload = result.get("payload");
                        if (payload != null) {
                            Map<String, Object> payloadMap = new HashMap<>();
                            payload.fields().forEachRemaining(entry -> {
                                JsonNode value = entry.getValue();
                                if (value.isTextual()) {
                                    payloadMap.put(entry.getKey(), value.asText());
                                } else if (value.isInt()) {
                                    payloadMap.put(entry.getKey(), value.asInt());
                                } else if (value.isDouble()) {
                                    payloadMap.put(entry.getKey(), value.asDouble());
                                } else {
                                    payloadMap.put(entry.getKey(), value.toString());
                                }
                            });
                            resultMap.put("payload", payloadMap);
                        }
                        results.add(resultMap);
                    }
                }
                return results;
            }
        } catch (IOException e) {
            log.error("Error searching in collection {}", collectionName, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public void deletePoint(String collectionName, Object pointId) {
        try {
            Object id;
            if (pointId instanceof String) {
                String strId = (String) pointId;
                if (strId.contains("_")) {
                    String numPart = strId.substring(strId.lastIndexOf("_") + 1);
                    try {
                        id = Integer.parseInt(numPart);
                    } catch (NumberFormatException e) {
                        log.error("Invalid point ID format: {}. Must be integer or UUID", pointId);
                        return;
                    }
                } else {
                    try {
                        id = Integer.parseInt(strId);
                    } catch (NumberFormatException e) {
                        log.error("Invalid point ID format: {}. Must be integer or UUID", pointId);
                        return;
                    }
                }
            } else {
                id = pointId;
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("points", List.of(id));
            
            String url = qdrantUrl + "/collections/" + collectionName + "/points/delete";
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to delete point {} from collection {}: {}", pointId, collectionName,
                            response.body() != null ? response.body().string() : "Unknown error");
                }
            }
        } catch (IOException e) {
            log.error("Error deleting point {} from collection {}", pointId, collectionName, e);
        }
    }
    
    @Override
    public long getCollectionCount(String collectionName) {
        try {
            String url = qdrantUrl + "/collections/" + collectionName;
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("Collection {} does not exist or error: {}", collectionName,
                            response.body() != null ? response.body().string() : "Unknown error");
                    return -1;
                }
                
                String responseBody = response.body() != null ? response.body().string() : "{}";
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                
                JsonNode result = jsonNode.get("result");
                if (result != null) {
                    JsonNode pointsCount = result.get("points_count");
                    if (pointsCount != null) {
                        return pointsCount.asLong();
                    }
                }
                
                return 0;
            }
        } catch (IOException e) {
            log.error("Error getting collection count for {}", collectionName, e);
            return -1;
        }
    }
}

