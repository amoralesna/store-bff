package com.company.store_bff.products.infra.mappers;

import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.infra.dtos.ExternalProductDetail;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ExternalProductDetailMapper {

    Product toDomain(ExternalProductDetail externalProductDetail);
}
