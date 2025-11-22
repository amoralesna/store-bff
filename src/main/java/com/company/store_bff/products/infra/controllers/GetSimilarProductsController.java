package com.company.store_bff.products.infra.controllers;

import com.company.store_bff.products.application.ports.in.GetSimilarProductsUseCase;
import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.shared.infra.api.ProductApi;
import com.company.store_bff.shared.infra.api.model.ProductDetail;
import com.company.store_bff.shared.infra.mappers.ProductMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@AllArgsConstructor
public class GetSimilarProductsController implements ProductApi {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;
    private final ProductMapper productMapper;

    @Override
    public ResponseEntity<Set<ProductDetail>> getProductSimilar(String productId) {

        Set<Product> similarProducts = getSimilarProductsUseCase.getSimilarProducts(productId);

        return ResponseEntity
                .ok(productMapper.toResponse(similarProducts));
    }
}
