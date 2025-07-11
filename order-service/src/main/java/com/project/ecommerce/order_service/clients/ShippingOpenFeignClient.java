package com.project.ecommerce.order_service.clients;


import com.project.ecommerce.order_service.dto.ShippingRequestDto;
import com.project.ecommerce.order_service.dto.ShippingResponseDto;
import com.project.ecommerce.order_service.dto.ShippingStatusResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient( name = "shipping-service" , path = "/shipping")
public interface ShippingOpenFeignClient {

    @PostMapping("/core/create")
    ShippingResponseDto createShipment(@RequestBody ShippingRequestDto shippingRequestDto);

    @GetMapping("/core/status/{orderId}")
    ShippingStatusResponseDto getShippingStatus(@PathVariable("orderId") Long orderId);

}
