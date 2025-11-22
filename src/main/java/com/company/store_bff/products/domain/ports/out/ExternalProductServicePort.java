package com.company.store_bff.products.domain.ports.out;

import com.company.store_bff.products.domain.models.Product;

import java.util.List;

public interface ExternalProductServicePort {

    List<String> getSimilarProductsIds(String productId);
    List<Product> getProductsDetail(List<String> productIds);
}
