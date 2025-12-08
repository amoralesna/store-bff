package com.company.store_bff.products.infra.controllers;

import com.company.store_bff.products.domain.ports.in.GetSimilarProductsUseCase;
import com.company.store_bff.shared.infra.api.model.ProductDetail;
import com.company.store_bff.shared.infra.mappers.ProductMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class GetSimilarProductsController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;
    private final ProductMapper productMapper;

    @GetMapping("/product/{productId}/similar")
    public Mono<ResponseEntity<List<ProductDetail>>> getProductSimilar(
            @PathVariable String productId) {

        log.debug("GetSimilarProductsController - getProductSimilar - Fetching similar products for product {}", productId);
        return getSimilarProductsUseCase.execute(productId)
                .map(productMapper::toDto)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
