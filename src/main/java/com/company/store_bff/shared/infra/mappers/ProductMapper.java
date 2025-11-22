package com.company.store_bff.shared.infra.mappers;

import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.shared.infra.api.model.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProductMapper {
    Set<ProductDetail> toResponse(Set<Product> product);
}
