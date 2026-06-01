package com.shopsphere.store_service.dto;

import com.shopsphere.store_service.model.StoreStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class StoreResponse {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String supportEmail;
    private String supportPhone;
    private String logoUrl;
    private StoreStatus status;
    private Instant createdAt;
}