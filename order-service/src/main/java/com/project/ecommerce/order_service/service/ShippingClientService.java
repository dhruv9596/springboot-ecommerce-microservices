package com.project.ecommerce.order_service.service;


import com.project.ecommerce.order_service.clients.ShippingOpenFeignClient;
import com.project.ecommerce.order_service.dto.ShippingRequestDto;
import com.project.ecommerce.order_service.dto.ShippingResponseDto;
import com.project.ecommerce.order_service.entity.ShippingStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingClientService {

    private final ShippingOpenFeignClient shippingOpenFeignClient;

    @CircuitBreaker(name = "shippingCircuitBreaker" , fallbackMethod = "createShipmentFallback")
    public ShippingResponseDto createShipment(ShippingRequestDto shippingRequestDto){
        return shippingOpenFeignClient.createShipment(shippingRequestDto);
    }

    public ShippingResponseDto createShipmentFallback( ShippingRequestDto shippingRequestDto , Throwable throwable){
        log.error("Fallback occurred due to: {}", throwable.getMessage());
        return ShippingResponseDto.builder()
                .trackingId("N/A")
                .expectedDeliveryDate(LocalDate.now().plusDays(14)) // or null
                .shippingStatus(ShippingStatus.FAILED)
                .build();

    }



}
