package com.company.store_bff.products.application.adapters;

import com.company.store_bff.products.application.ports.in.GetSimilarProductsUseCase;
import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.domain.ports.out.ExternalProductServicePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Service
@AllArgsConstructor
public class GetSimilarProductsAdapter implements GetSimilarProductsUseCase {

    private final ExternalProductServicePort externalProductServicePort;

    @Override
    public Set<Product> getSimilarProducts(String productId) {

        List<String> productIds = externalProductServicePort.getSimilarProductsIds(productId);
        List<Product> productsDetail = externalProductServicePort.getProductsDetail(productIds);

        return isNull(productsDetail) || productsDetail.isEmpty()
                ? Set.of()
                : Set.copyOf(productsDetail);
    }
}
