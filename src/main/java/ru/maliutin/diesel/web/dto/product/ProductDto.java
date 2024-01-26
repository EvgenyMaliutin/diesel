package ru.maliutin.diesel.web.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private int id;
    private String number;
    private String name;
    private BigDecimal price;
    private int amount;

}
