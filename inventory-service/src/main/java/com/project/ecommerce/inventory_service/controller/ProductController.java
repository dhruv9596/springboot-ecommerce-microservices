package com.project.ecommerce.inventory_service.controller;

import com.project.ecommerce.inventory_service.clients.OrdersFeignClient;
import com.project.ecommerce.inventory_service.dto.OrderRequestDto;
import com.project.ecommerce.inventory_service.dto.ProductDto;
import com.project.ecommerce.inventory_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/products")
public class ProductController {

    private final OrdersFeignClient ordersFeignClient;

    private final ProductService productService;

    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    @GetMapping("/fetchOrders")
    public String fetchFromOrdersService() {
        log.info("Fetch Orders API Call:");
        //Rest Client
        /*List<ServiceInstance> instances = discoveryClient.getInstances("order-service");
        ServiceInstance instance = instances.get(0);
        log.info("Discovered Order Service at URI: {}", instance.getUri());
        String uri = instance.getUri().toString() + "/orders/core/helloOrders";
        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);*/

        //Open Feign Client
        return ordersFeignClient.helloOrders();
    }


    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllInventory(){
        List<ProductDto> inventories = productService.getAllInventory();
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getInventoryById(@PathVariable Long id){
        ProductDto inventory = productService.getProductById(id);
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("reduce-stocks")
    public ResponseEntity<Double> reduceStocks(@RequestBody OrderRequestDto orderRequestDto){

        Double totalPrice = productService.reduceStocks(orderRequestDto);
        return ResponseEntity.ok(totalPrice);
    }

}
