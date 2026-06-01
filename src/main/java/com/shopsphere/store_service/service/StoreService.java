package com.shopsphere.store_service.service;

import com.shopsphere.store_service.dto.StoreRequest;
import com.shopsphere.store_service.dto.StoreResponse;
import com.shopsphere.store_service.exception.DuplicateResourceException;
import com.shopsphere.store_service.exception.ResourceNotFoundException;
import com.shopsphere.store_service.model.Store;
import com.shopsphere.store_service.model.StoreStatus;
import com.shopsphere.store_service.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j // Injects the 'log' variable for structured logging
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional // Ensures atomicity. If anything fails, the DB rolls back.
    public StoreResponse createStore(StoreRequest request, Long ownerId) {
        log.info("Initiating store creation for owner ID: {} with store name: {}", ownerId, request.getName());

        // 1. Fail Fast Validation: Check DB before attempting to save
        if (storeRepository.existsByNameIgnoreCase(request.getName())) {
            log.warn("Store creation rejected: Name '{}' is already in use.", request.getName());
            throw new DuplicateResourceException("The store name '" + request.getName() + "' is already taken.");
        }

        // 2. Build the Entity
        Store store = Store.builder()
                .name(request.getName())
                .description(request.getDescription())
                .supportEmail(request.getSupportEmail())
                .supportPhone(request.getSupportPhone())
                .commercialRegistrationNumber(request.getCommercialRegistrationNumber())
                .taxId(request.getTaxId())
                .logoUrl(request.getLogoUrl())
                .ownerId(ownerId)
                // status and createdAt are handled by @PrePersist and @CreatedDate
                .build();

        // 3. Save to the database
        Store savedStore = storeRepository.save(store);
        log.info("Successfully created store ID: {} for owner ID: {}", savedStore.getId(), ownerId);

        return mapToResponse(savedStore);
    }

    @Transactional(readOnly = true) // Optimizes Hibernate memory usage and allows replica routing
    public Page<StoreRepository.StoreSummary> getMyStores(Long ownerId, Pageable pageable) {
        log.debug("Fetching paginated stores for owner ID: {}", ownerId);

        // Utilizing the lightweight Projection and Pagination from the Repository
        return storeRepository.findStoreSummariesByOwnerId(ownerId, pageable);
    }

    @Cacheable(value = "storeOwnerIds", key = "#storeId") // Prevents slamming the DB for auth checks
    @Transactional(readOnly = true)
    public Long getStoreOwnerId(Long storeId) {
        log.debug("Cache miss for store ID: {}. Fetching owner ID from database.", storeId);

        return storeRepository.findById(storeId)
                .map(Store::getOwnerId)
                .orElseThrow(() -> {
                    log.error("Failed to find store owner. Store ID {} does not exist.", storeId);
                    return new ResourceNotFoundException("Store not found with id: " + storeId);
                });
    }

    // ==========================================
    // Helper Method: Entity to DTO Mapper
    // ==========================================
    private StoreResponse mapToResponse(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .description(store.getDescription())
                .ownerId(store.getOwnerId())
                .supportEmail(store.getSupportEmail())
                .supportPhone(store.getSupportPhone())
                .logoUrl(store.getLogoUrl())
                .status(store.getStatus())
                .createdAt(store.getCreatedAt())
                .build();
    }
}