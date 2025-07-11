package com.project.ecommerce.shipping_service.controller;

import com.project.ecommerce.shipping_service.dto.ShippingRequestDto;
import com.project.ecommerce.shipping_service.dto.ShippingResponseDto;
import com.project.ecommerce.shipping_service.dto.ShippingStatusResponseDto;
import com.project.ecommerce.shipping_service.entity.Shipping;
import com.project.ecommerce.shipping_service.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping( path = "/core")
public class ShippingController {

    private final ShippingService shippingService;

    @PostMapping("/create")
    public ResponseEntity<ShippingResponseDto> createShipment(@RequestBody ShippingRequestDto shippingRequestDto){
        ShippingResponseDto shippingResponseDto = shippingService.createShipment(shippingRequestDto);
        return ResponseEntity.ok(shippingResponseDto);
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<ShippingStatusResponseDto> getShippingStatus(@PathVariable Long orderId) {
        log.info("Fetching Shipping with Order ID : {} via controller ",orderId);
        ShippingStatusResponseDto response = shippingService.getShippingStatus(orderId);
        return ResponseEntity.ok( response);
    }

}
