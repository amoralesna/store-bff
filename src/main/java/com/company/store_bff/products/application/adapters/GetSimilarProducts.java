package com.company.store_bff.products.application.adapters;

import com.company.store_bff.products.domain.ports.in.GetSimilarProductsUseCase;
import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.domain.ports.out.ExternalProductServicePort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class GetSimilarProducts implements GetSimilarProductsUseCase {

    private final ExternalProductServicePort externalProductServicePort;

    @Override
    public Mono<Set<Product>> execute(String productId) {

        log.debug("getSimilarProducts - Fetched similar products for product {}", productId);
        return externalProductServicePort.getSimilarProductsIds(productId)
                .flatMapMany(externalProductServicePort::getProductsDetails)
                .collectList()
                .map(productsDetail -> productsDetail.isEmpty()
                        ? Set.of()
                        : Set.copyOf(productsDetail));
    }
}
