package com.project.ecommerce.shipping_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String trackingId;

    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    private ShippingStatus shippingStatus;

    private LocalDate expectedDeliveryDate;

}
