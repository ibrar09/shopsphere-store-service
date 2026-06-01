package com.shopsphere.store_service.repository;

import com.shopsphere.store_service.model.Store;
import com.shopsphere.store_service.model.StoreStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    // 1. PAGINATION: Shield the JVM from OutOfMemory errors
    Page<Store> findByOwnerId(Long ownerId, Pageable pageable);

    // 2. SAFE LOOKUPS: Always use Optional for single records to avoid NullPointerExceptions
    Optional<Store> findByName(String name);

    // 3. EXISTENCE CHECKS: The absolute fastest way to validate uniqueness
    boolean existsByNameIgnoreCase(String name);

    // 4. PROJECTIONS: Fetch only the strictly required columns for list/dashboard views
    @Query("SELECT s.id AS id, s.name AS name, s.logoUrl AS logoUrl, s.status AS status FROM Store s WHERE s.ownerId = :ownerId")
    Page<StoreSummary> findStoreSummariesByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    /**
     * Interface-based Projection: Spring automatically maps the specific
     * columns from the query above into this lightweight interface.
     */
    interface StoreSummary {
        Long getId();
        String getName();
        String getLogoUrl();
        StoreStatus getStatus();
    }
}