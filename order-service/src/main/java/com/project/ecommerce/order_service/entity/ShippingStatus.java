package com.project.ecommerce.order_service.entity;

public enum ShippingStatus {

    PENDING,
    PACKED,
    SHIPPED,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FAILED,
    RETURNED,
    CANCELLED,
    REDELIVERY_SCHEDULED

}
