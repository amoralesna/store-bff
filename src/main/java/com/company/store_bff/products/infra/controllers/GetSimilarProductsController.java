package com.company.store_bff.products.infra.controllers;

import com.company.store_bff.products.domain.ports.in.GetSimilarProductsUseCase;
import com.company.store_bff.products.infra.api.ProductApi;
import com.company.store_bff.products.infra.api.model.ProductDetail;
import com.company.store_bff.products.infra.mappers.ProductMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@Slf4j
public class GetSimilarProductsController implements ProductApi {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;
    private final ProductMapper productMapper;

    public Mono<ResponseEntity<Flux<ProductDetail>>> getProductSimilar(
            String productId,
            ServerWebExchange exchange) {

        log.debug("GetSimilarProductsController - getProductSimilar - Fetching similar products for product {}", productId);

        return Mono.just(ResponseEntity
                .ok(getSimilarProductsUseCase.execute(productId)
                .map(productMapper::toDto)));
    }
}
