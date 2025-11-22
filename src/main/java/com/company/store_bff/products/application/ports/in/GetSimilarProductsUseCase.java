package com.company.store_bff.products.application.ports.in;

import com.company.store_bff.products.domain.models.Product;

import java.util.Set;

public interface GetSimilarProductsUseCase {

    Set<Product> getSimilarProducts(String productId);
}
