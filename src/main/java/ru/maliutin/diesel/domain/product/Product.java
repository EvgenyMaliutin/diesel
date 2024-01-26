package ru.maliutin.diesel.domain.product;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сущность товара.
 */

@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "program_number")
    private int programNumber;

    @Column(name = "catalog_number")
    private String catalogNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "model_car")
    private String modelCar;

    @Column(name = "amount")
    private int amount;
}
