package com.project.ecommerce.shipping_service.repository;

import com.project.ecommerce.shipping_service.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ShippingRepository extends JpaRepository<Shipping , Long> {
    <Optional> Shipping  findByOrderId(Long orderId);
}
