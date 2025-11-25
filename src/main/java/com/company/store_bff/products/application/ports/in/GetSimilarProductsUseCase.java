package com.company.store_bff.products.application.ports.in;

import com.company.store_bff.products.domain.models.Product;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface GetSimilarProductsUseCase {

    Mono<Set<Product>> getSimilarProducts(String productId);
}
