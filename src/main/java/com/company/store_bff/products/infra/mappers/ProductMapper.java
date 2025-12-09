package com.company.store_bff.products.infra.mappers;

import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.infra.api.model.ProductDetail;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProductMapper {
    ProductDetail toDto(Product product);
}
