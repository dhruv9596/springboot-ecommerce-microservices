package com.project.ecommerce.order_service.controller;

import com.project.ecommerce.order_service.dto.OrderRequestDto;
import com.project.ecommerce.order_service.dto.OrderResponseDto;
import com.project.ecommerce.order_service.dto.ShippingStatusResponseDto;
import com.project.ecommerce.order_service.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
@Slf4j
public class OrdersController {
    private final OrdersService ordersService;


    @GetMapping("/helloOrders")
    public String helloOrders(@RequestHeader("X-User-Id") Long userId ){
        log.info("Order Service Controller : /core/helloOrders : ");
        return "Hello from Order Service"+ userId;
    }


    @GetMapping
    public ResponseEntity<List<OrderRequestDto>> getAllOrders(HttpServletRequest httpServletRequest) {
        log.info("Fetching All orders via controller ");
        List<OrderRequestDto> orders = ordersService.getAllOrder();
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderRequestDto> getOrderById(@PathVariable Long id){
        log.info("Fetching order with ID : {} via controller ",id);
        OrderRequestDto order = ordersService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/create-order")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto){
        //Double totalPrice = inventoryOpenFeignClient.reduceStocks(orderRequestDto);
        OrderResponseDto orderResponseDto = ordersService.createOrder(orderRequestDto);
        return ResponseEntity.ok(orderResponseDto);

    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable(name = "orderId") Long orderId){

        log.info("Cancelling order with ID : {} via controller ",orderId);
        ordersService.cancelOrder(orderId);
        return ResponseEntity.ok("Order Cancelled and Order Items Restocked.");

    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<ShippingStatusResponseDto> shippingStatus( @PathVariable(name = "orderId") Long orderId){

        log.info("Fetching order status with ID : {} via controller ", orderId);
        ShippingStatusResponseDto shippingStatusResponseDto = ordersService.orderShipmentStatus(orderId);
        return ResponseEntity.ok(shippingStatusResponseDto);
    }

}



