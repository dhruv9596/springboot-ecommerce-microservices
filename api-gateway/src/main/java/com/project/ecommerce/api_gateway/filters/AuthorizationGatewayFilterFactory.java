package com.project.ecommerce.api_gateway.filters;

import com.project.ecommerce.api_gateway.service.JwtService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthorizationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthorizationGatewayFilterFactory.Config> {

    private final JwtService jwtService;

    @Data
    public static class Config{
        private String allowedRoles;

        public String getAllowedRoles() {
            return allowedRoles;
        }

        public void setAllowedRoles(String allowedRoles) {
            this.allowedRoles = allowedRoles;
        }
    }

    public AuthorizationGatewayFilterFactory(JwtService jwtService){
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config){

        return (exchange, chain) -> {

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if( authorizationHeader == null ){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            log.info("Authorization Header : {}", exchange.getRequest().getHeaders().toString());

            //String token = authorizationHeader.split("Bearer ")[1];
            String token = authorizationHeader.substring(7).trim();
            List<String> inputRoles = jwtService.getUserRoleFromToken(token);

            List<String> allowedRoles = Arrays.stream(config.getAllowedRoles().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            log.info("Token : {} " , token);
            log.info("Token Input Roles : {}" , inputRoles);
            if (inputRoles == null || inputRoles.isEmpty()) {
                log.warn("No roles found in token.");
                return onError(exchange, "Access Denied: No roles in token", HttpStatus.FORBIDDEN);
            }


            boolean isAuthorized = inputRoles.stream().anyMatch(allowedRoles::contains);

            if(!isAuthorized){
                return unauthorized(exchange , "Forbidden : Insufficient Roles");
            }
            return chain.filter(exchange);
        };
    }
    public Mono<Void> unauthorized(ServerWebExchange exchange , String message){
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(
                message.getBytes(StandardCharsets.UTF_8)
        );
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        String body = String.format("{\"error\": \"%s\"}", errorMessage);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }


}
