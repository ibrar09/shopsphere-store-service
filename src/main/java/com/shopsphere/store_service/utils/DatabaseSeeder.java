//package com.shopsphere.store_service.config;
//
//import com.shopsphere.store_service.model.Store;
//import com.shopsphere.store_service.model.StoreStatus;
//import com.shopsphere.store_service.repository.StoreRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DatabaseSeeder implements CommandLineRunner {
//
//    private final StoreRepository storeRepository;
//    private final JdbcTemplate jdbcTemplate; // Allows us to run raw SQL
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        log.warn("Wiping all existing stores from the database using native SQL...");
//
//        // 1. Native SQL TRUNCATE: Bypasses soft-deletes and resets IDs to 1
//        jdbcTemplate.execute("TRUNCATE TABLE stores RESTART IDENTITY CASCADE");
//
//        log.info("Database wiped clean. Generating 10,000 fresh test stores...");
//
//        List<Store> storesToSave = new ArrayList<>();
//        Long mockOwnerId = 999L;
//
//        for (int i = 1; i <= 10000; i++) {
//            String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
//
//            storesToSave.add(Store.builder()
//                    .name("Test Store " + i + " [" + uniqueSuffix + "]")
//                    .description("This is a description to test projection memory savings. ".repeat(5))
//                    .ownerId(mockOwnerId)
//                    .status(StoreStatus.ACTIVE)
//                    .build());
//        }
//
//        storeRepository.saveAll(storesToSave);
//        log.info("Successfully seeded 10,000 new stores!");
//    }
//}