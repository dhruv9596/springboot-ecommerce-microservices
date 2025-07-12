package com.project.ecommerce.order_service.dto;

import com.project.ecommerce.order_service.entity.ShippingStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingResponseDto {
    private String trackingId;
    private String shippingId;
    private ShippingStatus shippingStatus;
    private LocalDate expectedDeliveryDate;
}
