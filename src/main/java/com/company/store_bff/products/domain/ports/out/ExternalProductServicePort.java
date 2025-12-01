package com.company.store_bff.products.domain.ports.out;

import com.company.store_bff.products.domain.models.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ExternalProductServicePort {

    Mono<List<String>> getSimilarProductsIds(String productId);
    Flux<Product> getProductsDetails(List<String> productIds);
}
