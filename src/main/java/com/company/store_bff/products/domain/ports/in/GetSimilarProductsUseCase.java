package com.company.store_bff.products.domain.ports.in;

import com.company.store_bff.products.domain.models.Product;
import reactor.core.publisher.Flux;

public interface GetSimilarProductsUseCase {

    Flux<Product> execute(String productId);
}
