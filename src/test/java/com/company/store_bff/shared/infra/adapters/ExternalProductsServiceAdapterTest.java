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
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

        List<String> result = externalProductsServiceAdapter.getSimilarProductsIds("1");

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(List.of("1", "2", "3"), result);
    }

    @Test
    void should_getSimilarProductsIds_return_empty_on_error() {
        when(appConfigEnvironmentMock.getExternalProductsServiceUriProductSimilarIds())
                .thenReturn("/product/{id}/similarids");

        when(webClientMock.get().uri(anyString(), anyString()).retrieve().bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(new RuntimeException("remote error")));

        List<String> result = externalProductsServiceAdapter.getSimilarProductsIds("1");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void should_getProductsDetail_map_success_and_skip_failures() {
        when(appConfigEnvironmentMock.getExternalProductsServiceUriProductDetail())
                .thenReturn("/product/{id}");

        ExternalProductDetail detail1 = ExternalProductDetail.builder().id("1").name("Product 1").build();

        when(webClientMock.get().uri(anyString(), anyString()).retrieve().bodyToMono(eq(ExternalProductDetail.class)))
                .thenReturn(Mono.just(detail1))
                .thenReturn(Mono.error(new RuntimeException("remote error")));

        Product product = new Product("1", "Product 1", null, false);
        when(externalProductDetailMapperMock.toDomain(detail1)).thenReturn(product);

        List<Product> result = externalProductsServiceAdapter.getProductsDetail(List.of("1", "2"));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        verify(externalProductDetailMapperMock, times(1)).toDomain(detail1);
    }
}