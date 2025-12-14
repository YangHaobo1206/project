package com.example.fooddelivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shops")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String address;
    private boolean online;

    /**
     * 店铺状态：PENDING（待审核）、APPROVED（已通过）、REJECTED（拒绝）
     */
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User owner;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = owner == null ? "APPROVED" : "PENDING";
        }
    }
}
