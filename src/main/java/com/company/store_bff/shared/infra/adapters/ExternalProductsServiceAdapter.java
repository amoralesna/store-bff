package com.company.store_bff.shared.infra.adapters;

import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.domain.ports.out.ExternalProductServicePort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExternalProductsServiceAdapter implements ExternalProductServicePort {

    @Override
    public List<String> getSimilarProductsIds(String productId) {
        return List.of();
    }

    @Override
    public List<Product> getProductsDetail(List<String> productIds) {
        return List.of();
    }
}
