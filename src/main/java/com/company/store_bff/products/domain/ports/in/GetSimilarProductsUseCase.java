package com.company.store_bff.products.domain.ports.in;

import com.company.store_bff.products.domain.models.Product;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface GetSimilarProductsUseCase {

    Mono<Set<Product>> execute(String productId);
}
