package com.project.ecommerce.order_service.dto;

import com.project.ecommerce.order_service.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OrderRequestDto {
    //private Long orderId;
    private OrderStatus orderStatus;
    private Double totalPrice;

    private List<OrderRequestItemDto> items;
    // Shipping details
    private String trackingId;
    private LocalDate expectedDeliveryDate;
    private String shippingAddress;
}
