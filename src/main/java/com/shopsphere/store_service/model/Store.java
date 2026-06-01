package com.shopsphere.store_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(
        name = "stores",
        indexes = {
                @Index(name = "idx_store_owner", columnList = "owner_id"),
                @Index(name = "idx_store_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // Spring Data JPA Auditing
@SQLDelete(sql = "UPDATE stores SET status = 'DELETED' WHERE id = ?") // Soft Delete
@SQLRestriction("status <> 'DELETED'") // Hibernate 6.3+ (use @Where for older versions)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    // --- PROFESSIONAL FIELDS ---

    @Column(name = "support_email")
    private String supportEmail;

    @Column(name = "support_phone", length = 20)
    private String supportPhone;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "commercial_registration_number", length = 50)
    private String commercialRegistrationNumber;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StoreStatus status;

    // --- AUDITING FIELDS ---

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    // --- METHODS ---

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = StoreStatus.PENDING;
        }
    }

    // Safe equals and hashCode relying only on the DB ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Store)) return false;
        Store store = (Store) o;
        return id != null && id.equals(store.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}