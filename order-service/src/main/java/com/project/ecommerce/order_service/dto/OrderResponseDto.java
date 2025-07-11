package com.project.ecommerce.order_service.dto;

import com.project.ecommerce.order_service.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OrderResponseDto {

    private Long orderId;
    private List<OrderRequestItemDto> items;

    private OrderStatus orderStatus;
    private String trackingId;
    private BigDecimal totalPrice;
    private String shippingAddress;
    private LocalDate expectedDeliveryDate;

}
