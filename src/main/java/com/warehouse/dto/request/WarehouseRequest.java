package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;
}
