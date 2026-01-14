package com.example.mini_mart.config;

import com.example.mini_mart.entity.User;
import com.example.mini_mart.repository.UserRepository;
import com.example.mini_mart.service.VectorSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VectorSyncService vectorSyncService;

    @Override
    public void run(String... args) {
        // Tạo admin account nếu chưa có
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setFullName("Administrator");
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@minimart.com");
            admin.setPhone("0123456789");
            admin.setRole("Admin");
            admin.setIsActive(true);
            admin.setCreatedAt(Instant.now());
            
            userRepository.save(admin);
            log.info("Admin account created successfully - username: admin, password: admin123");
        } else {
            log.info("Admin account already exists");
        }

        try {
            log.info("Checking Qdrant collections...");
            
            if (vectorSyncService.isCollectionEmpty("products")) {
                log.info("Products collection is empty, starting sync...");
                log.warn("WARNING: This will consume Gemini API quota. " +
                        "Free tier limit: ~1500 embedding requests/day. " +
                        "If you have many products, consider syncing manually later.");
                vectorSyncService.syncAllProducts();
                log.info("Products sync completed");
            } else {
                log.info("Products collection already has data, skipping sync to save Gemini API quota");
            }
            
            // Chỉ sync customers nếu collection trống
            if (vectorSyncService.isCollectionEmpty("customers")) {
                log.info("Customers collection is empty, starting sync...");
                vectorSyncService.syncAllCustomers();
                log.info("Customers sync completed");
            } else {
                log.info("Customers collection already has data, skipping sync to save Gemini API quota");
            }
        } catch (Exception e) {
            log.error("Error syncing vectors to Qdrant. This is not critical, but vector search may not work.", e);
            if (e.getMessage() != null && e.getMessage().contains("quota")) {
                log.error("QUOTA EXCEEDED: You have exceeded Gemini API free tier quota. " +
                        "Please wait 24 hours or upgrade to paid tier. " +
                        "Vector search will use keyword matching as fallback.");
            }
        }
    }
}


