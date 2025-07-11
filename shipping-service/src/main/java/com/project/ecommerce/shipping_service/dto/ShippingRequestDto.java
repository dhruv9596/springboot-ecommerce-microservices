package com.project.ecommerce.shipping_service.dto;

import lombok.Data;

@Data
public class ShippingRequestDto {

    private Long orderId;
    private String shippingAddress;

}
