package com.project.ecommerce.shipping_service.dto;

import com.project.ecommerce.shipping_service.entity.ShippingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingResponseDto {
    private String trackingId;
    private String shippingId;
    private ShippingStatus shippingStatus;
    private LocalDate expectedDeliveryDate;
}
