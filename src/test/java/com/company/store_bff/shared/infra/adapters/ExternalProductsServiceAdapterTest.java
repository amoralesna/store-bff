package com.company.store_bff.shared.infra.adapters;

import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.shared.infra.config.AppConfigEnvironment;
import com.company.store_bff.shared.infra.dtos.ExternalProductDetail;
import com.company.store_bff.shared.infra.mappers.ExternalProductDetailMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalProductsServiceAdapterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClientMock;

    @Mock
    private AppConfigEnvironment appConfigEnvironmentMock;

    @Mock
    private ExternalProductDetailMapper externalProductDetailMapperMock;

    @InjectMocks
    private ExternalProductsServiceAdapter externalProductsServiceAdapter;

    @Test
    void should_getSimilarProductsIds_return_list() {

        when(appConfigEnvironmentMock.getExternalProductsServiceUriProductSimilarIds())
                .thenReturn("/product/{id}/similarids");

        when(webClientMock.get().uri(anyString(), anyString()).retrieve().bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(List.of("1", "2", "3")));

        Mono<List<String>> result = externalProductsServiceAdapter.getSimilarProductsIds("1");

        StepVerifier.create(result)
                .expectNextMatches(ids -> ids.size() == 3 && ids.equals(List.of("1", "2", "3")))
                .verifyComplete();
    }

    @Test
    void should_getProductsDetail_return_flux() {
        when(appConfigEnvironmentMock.getExternalProductsServiceUriProductDetail())
                .thenReturn("/product/{id}");

        when(appConfigEnvironmentMock.getMaxConcurrentRequestsProductDetail())
                .thenReturn(2);

        ExternalProductDetail detail1 = ExternalProductDetail.builder().id("1").name("Product 1").build();
        ExternalProductDetail detail2 = ExternalProductDetail.builder().id("2").name("Product 2").build();

        when(webClientMock.get().uri(anyString(), eq("1")).retrieve().bodyToMono(eq(ExternalProductDetail.class)))
                .thenReturn(Mono.just(detail1));

        when(webClientMock.get().uri(anyString(), eq("2")).retrieve().bodyToMono(eq(ExternalProductDetail.class)))
                .thenReturn(Mono.just(detail2));

        Product product1 = new Product("1", "Product 1", null, false);
        Product product2 = new Product("2", "Product 2", null, false);

        when(externalProductDetailMapperMock.toDomain(detail1)).thenReturn(product1);
        when(externalProductDetailMapperMock.toDomain(detail2)).thenReturn(product2);

        Flux<Product> result = externalProductsServiceAdapter.getProductsDetail(List.of("1", "2"));

        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();

        verify(externalProductDetailMapperMock, times(2)).toDomain(any());
    }

    @Test
    void should_getProductsDetail_return_empty_when_list_is_null() {
        Flux<Product> result = externalProductsServiceAdapter.getProductsDetail(null);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void should_getProductsDetail_return_empty_when_list_is_empty() {
        Flux<Product> result = externalProductsServiceAdapter.getProductsDetail(List.of());

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }
}