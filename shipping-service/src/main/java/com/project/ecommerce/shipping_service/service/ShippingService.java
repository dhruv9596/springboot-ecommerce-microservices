package com.project.ecommerce.shipping_service.service;


import com.project.ecommerce.shipping_service.dto.ShippingRequestDto;
import com.project.ecommerce.shipping_service.dto.ShippingResponseDto;
import com.project.ecommerce.shipping_service.dto.ShippingStatusResponseDto;
import com.project.ecommerce.shipping_service.entity.Shipping;
import com.project.ecommerce.shipping_service.entity.ShippingStatus;
import com.project.ecommerce.shipping_service.repository.ShippingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShippingService {

    private final ModelMapper modelMapper;
    private final ShippingRepository shippingRepository;

    public ShippingResponseDto createShipment(ShippingRequestDto shippingRequestDto) {
        Shipping shipment = Shipping.builder()
                .orderId(shippingRequestDto.getOrderId())
                .shippingAddress(shippingRequestDto.getShippingAddress())
                .shippingStatus(ShippingStatus.PENDING)
                .expectedDeliveryDate(LocalDate.now().plusDays(7))
                .trackingId(generateTrackingId())
                .build();
        shipment = shippingRepository.save(shipment);
        ShippingResponseDto shippingResponseDto = modelMapper.map(shipment , ShippingResponseDto.class);
        return  shippingResponseDto;
    }

    private String generateTrackingId() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public ShippingStatusResponseDto getShippingStatus(Long orderId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId);
//                .orElseThrow(() -> new RuntimeException("Shipping not found"));

        ShippingStatusResponseDto response = ShippingStatusResponseDto.builder()
                .trackingId(shipping.getTrackingId())
                .shippingStatus(shipping.getShippingStatus().name())
                .expectedDeliveryDate(shipping.getExpectedDeliveryDate())
                .build();
        return response;
    }
}
