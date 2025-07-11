package com.project.ecommerce.shipping_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingStatusResponseDto {
    private String trackingId;
    private String shippingStatus;
    private LocalDate expectedDeliveryDate;
}
