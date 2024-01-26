package ru.maliutin.diesel.service;

import ru.maliutin.diesel.domain.product.Product;

import java.util.List;
import java.util.Set;

public interface ProductService {
    /**
     * Поиск всех товаров.
     * @return список товаров.
     */
    List<Product> findAll();

    /**
     * Поиск товара по id
     * @param product_id идентификатор товара.
     * @return объект товара.
     */
    Product findById(long product_id);

    /**
     * Поиск товара по введенному тексту.
     * @param findText текст для поиска.
     * @return список товаров.
     */
    List<Product> find(String findText);

    /**
     * Поиск товара по модели применения.
     * @param modelCar модель техники
     * @return список товаров.
     */
    List<Product> findByModelCar(String modelCar);

    /**
     * Поиск товара по внутреннему номеру.
     * @param programNumber внутренний номер товара.
     * @return найденный товар.
     */
    Product findByProgramNumber(int programNumber);

}
