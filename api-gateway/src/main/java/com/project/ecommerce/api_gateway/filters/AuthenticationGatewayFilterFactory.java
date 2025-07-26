package com.project.ecommerce.api_gateway.filters;

import com.project.ecommerce.api_gateway.service.JwtService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
@Slf4j
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private final JwtService jwtService;

    @Data
    public static class Config {
        private boolean isEnabled;

    }

    public AuthenticationGatewayFilterFactory(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            //if( !config.isEnabled) return chain.filter(exchange);

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if( authorizationHeader == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            try {
                log.info("Try Block : ----- " );
                /*String token = authorizationHeader.substring(7).trim();
                Long userId = jwtService.getUserIdFromToken(token);
                log.info("User Id from Token : " + userId );
                exchange.getRequest().mutate()
                        .header("X-User-Id",userId.toString())
                        .build();
                log.info("Set Headers : "+exchange.getRequest().mutate().header("X-User-Id").toString());
                return chain.filter(exchange);*/
                String token = authorizationHeader.substring(7).trim();
                Long userId = jwtService.getUserIdFromToken(token);
                log.info("User Id from Token : " + userId);
                // Mutate request and set header
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().header("X-User-Id", userId.toString()).build();
                // Create a new exchange with the mutated request
                ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                return chain.filter(mutatedExchange);
            } catch (Exception e) {
                log.error("JWT parsing or header injection failed", e);
                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                return exchange.getResponse().setComplete();
            }
        };
    }
}
