package com.project.ecommerce.order_service.dto;

import com.project.ecommerce.order_service.entity.ShippingStatus;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class ShippingResponseDto {
    private String trackingId;
    private String shippingId;
    private ShippingStatus shippingStatus;
    private LocalDate expectedDeliveryDate;
}
