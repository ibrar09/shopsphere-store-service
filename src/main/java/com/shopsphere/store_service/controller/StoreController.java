package com.shopsphere.store_service.controller;

import com.shopsphere.store_service.dto.StoreRequest;
import com.shopsphere.store_service.dto.StoreResponse;
import com.shopsphere.store_service.repository.StoreRepository;
import com.shopsphere.store_service.service.StoreService;
import jakarta.validation.Valid; // Secures your inputs
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j // Injects standard logging
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreResponse> createStore(
            @Valid @RequestBody StoreRequest request, // @Valid ensures required fields aren't null
            Authentication authentication
    ) {
        Long ownerId = (Long) authentication.getPrincipal();
        log.info("REST request to create store received for owner ID: {}", ownerId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storeService.createStore(request, ownerId));
    }

    // --- FIX APPLIED HERE ---
    @GetMapping
    public ResponseEntity<Page<StoreRepository.StoreSummary>> getMyStores(
            Authentication authentication,
            // @PageableDefault protects your server if the frontend forgets to send ?size=10
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        Long ownerId = (Long) authentication.getPrincipal();
        log.info("REST request to fetch paginated stores for owner ID: {}", ownerId);

        // This now perfectly matches your upgraded Service layer!
        return ResponseEntity.ok(storeService.getMyStores(ownerId, pageable));
    }

    // 3. INTERNAL FEIGN ENDPOINT
    @GetMapping("/{id}/owner")
    public ResponseEntity<Long> getStoreOwnerId(@PathVariable Long id) {
        log.debug("REST request to get owner ID for store ID: {}", id);
        Long ownerId = storeService.getStoreOwnerId(id);
        return ResponseEntity.ok(ownerId);
    }
}