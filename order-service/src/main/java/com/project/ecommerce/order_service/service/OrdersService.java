package com.project.ecommerce.order_service.service;

import com.project.ecommerce.order_service.clients.InventoryOpenFeignClient;
import com.project.ecommerce.order_service.clients.ShippingOpenFeignClient;
import com.project.ecommerce.order_service.dto.*;
import com.project.ecommerce.order_service.entity.OrderItem;
import com.project.ecommerce.order_service.entity.OrderStatus;
import com.project.ecommerce.order_service.entity.Orders;
import com.project.ecommerce.order_service.entity.ShippingStatus;
import com.project.ecommerce.order_service.repository.OrdersRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final ModelMapper modelMapper;
    private final InventoryOpenFeignClient inventoryOpenFeignClient;
    private final ShippingOpenFeignClient shippingOpenFeignClient;

    @Autowired
    private ShippingClientService shippingClientService;

    public List<OrderRequestDto> getAllOrder(){
        log.info("Fetching All Orders : ");
        List<Orders> orders = ordersRepository.findAll();
        return orders.stream().map(order -> modelMapper.map( order , OrderRequestDto.class)).toList();
    }

    public OrderRequestDto getOrderById( Long id ){
        log.info("Fetching Order with ID : {} " , id );
        Orders order = ordersRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Order Not Found")
        );
        return modelMapper.map(order , OrderRequestDto.class);
    }

    //@Retry( name = "inventoryRetry" , fallbackMethod = "createOrderFallback" )
    //@RateLimiter(name = "inventoryRateLimiter" , fallbackMethod = "createOrderFallback")

    @CircuitBreaker(name = "inventoryCircuitBreaker" , fallbackMethod = "createOrderFallback")
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {

        log.info("Creating order in Order-Service : ");

        Double totalPrice = inventoryOpenFeignClient.reduceStocks(orderRequestDto);

        Orders orders = modelMapper.map(orderRequestDto , Orders.class);

        for(OrderItem orderItem : orders.getItems()){
            orderItem.setOrder(orders);
        }
        orders.setTotalPrice(totalPrice);
        orders.setShippingAddress(orderRequestDto.getShippingAddress());
        orders.setOrderStatus(OrderStatus.CONFIRMED);

        Orders savedOrder = ordersRepository.save(orders);

        ShippingRequestDto shippingRequestDto = new ShippingRequestDto();
        shippingRequestDto.setOrderId(savedOrder.getId());
        shippingRequestDto.setShippingAddress(savedOrder.getShippingAddress());


        //ShippingResponseDto shippingResponseDto = createShipment(shippingRequestDto);

        ShippingResponseDto shippingResponseDto = shippingClientService.createShipment(shippingRequestDto);

        log.info("ShippingResponseDto : {}", shippingResponseDto.toString());

        return OrderResponseDto.builder()
                .orderId(savedOrder.getId())
                .orderStatus(savedOrder.getOrderStatus())
                .shippingAddress(savedOrder.getShippingAddress())
                .totalPrice(BigDecimal.valueOf(savedOrder.getTotalPrice()))
                .items(savedOrder.getItems().stream()
                        .map(orderItem -> OrderRequestItemDto.builder()
                                .id(orderItem.getId())
                                .productId(orderItem.getProductId())
                                .quantity(orderItem.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .trackingId(String.valueOf(shippingResponseDto.getTrackingId()))
                .expectedDeliveryDate(LocalDate.from(shippingResponseDto.getExpectedDeliveryDate()))
                .build();

    }

    public OrderResponseDto createOrderFallback(OrderRequestDto orderRequestDto, Throwable throwable) {
        log.error("Fallback occurred due to: {}", throwable.getMessage());

        return OrderResponseDto.builder()
                .orderStatus(OrderStatus.FAILED)
                .totalPrice(BigDecimal.valueOf(0.0))
                .items(orderRequestDto.getItems())
                .trackingId("N/A")
                .expectedDeliveryDate(null)
                .build();
    }

    public void cancelOrder(Long orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("Order Not Found.")
        );
        if( order.getOrderStatus().equals("CANCELLED") || order.getOrderStatus().equals("DELIVERED")){
            throw new IllegalStateException("Cannot Cancel Order in this State.");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        ordersRepository.save(order);

        for( OrderItem orderItem : order.getItems() ){
                inventoryOpenFeignClient.restockProduct( orderItem.getProductId() , orderItem.getQuantity() );
        }
    }

    public ShippingStatusResponseDto orderShipmentStatus(Long orderId) {
        log.info("Fetching order status with ID : {} via order-service ",orderId);
        Orders order = ordersRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("Order Not Found.")
        );
        ShippingStatusResponseDto shippingStatusResponseDto = shippingOpenFeignClient.getShippingStatus(orderId);
        return shippingStatusResponseDto;

    }
}
