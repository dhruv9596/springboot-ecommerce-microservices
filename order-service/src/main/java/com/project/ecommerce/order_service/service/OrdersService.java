package com.project.ecommerce.order_service.service;

import com.project.ecommerce.order_service.clients.InventoryOpenFeignClient;
import com.project.ecommerce.order_service.dto.OrderRequestDto;
import com.project.ecommerce.order_service.entity.OrderItem;
import com.project.ecommerce.order_service.entity.OrderStatus;
import com.project.ecommerce.order_service.entity.Orders;
import com.project.ecommerce.order_service.repository.OrdersRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final ModelMapper modelMapper;
    private final InventoryOpenFeignClient inventoryOpenFeignClient;

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
    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto) {

        log.info("Creating order in Order-Service : ");

        Double totalPrice = inventoryOpenFeignClient.reduceStocks(orderRequestDto);

        Orders orders = modelMapper.map(orderRequestDto , Orders.class);

        for(OrderItem orderItem : orders.getItems()){
            orderItem.setOrder(orders);
        }
        orders.setTotalPrice(totalPrice);
        orders.setOrderStatus(OrderStatus.CONFIRMED);

        Orders savedOrder = ordersRepository.save(orders);

        return modelMapper.map(savedOrder , OrderRequestDto.class);

    }

    public OrderRequestDto createOrderFallback(OrderRequestDto orderRequestDto , Throwable throwable) {

        log.error("Fallback occured due to : {} " , throwable.getMessage());
        return new OrderRequestDto();
    }


}
