package com.company.store_bff.shared.infra.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExternalProductDetail {
    private String id;
    private String name;
    private Double price;
    private boolean availability;
}
