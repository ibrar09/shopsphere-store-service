package com.shopsphere.store_service.dto;

import lombok.Data;

@Data
public class StoreRequest {
    private String name;
    private String description;
    private String supportEmail;
    private String supportPhone;
    private String commercialRegistrationNumber;
    private String taxId;
    private String logoUrl;
    // Notice: NO ownerId or status here. We control those securely on the backend.
}