package ru.maliutin.diesel.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.repository.ProductRepository;
import ru.maliutin.diesel.service.ProductService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Поиск всех товаров.
     * @return список найденных товаров.
     */
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Поиск товара по id.
     * @param productId идентификатор товара.
     * @return
     */
    @Override
    public Product findById(long productId) {
        return productRepository.findById(productId).orElse(null);
    }

    /**
     * Поиск товаров по наименованиям или каталожным номерам.
     * @param findText текст для поиска.
     * @return список найденных товаров.
     */
    @Override
    public List<Product> find(String findText) {
        String [] words = findText.split(" ");
        List<Product> products = new ArrayList<>();
        for (String word : words){
            products.addAll(
                    productRepository
                            .findDistinctByNameContainingIgnoreCase(word));
            products.addAll(
                    productRepository
                            .findDistinctByCatalogNumberContainingIgnoreCase(
                                    word
                            )
            );
        }
        products = products.stream().distinct().collect(Collectors.toList());
        return products;
    }

    /**
     * Поиск товаров по технике применения.
     * @param modelCar модель техники
     * @return список найденных товаров.
     */
    @Override
    public List<Product> findByModelCar(String modelCar) {
        return productRepository
                .findDistinctByModelCarStartingWithIgnoreCaseOrderByName(
                        modelCar);
    }

    /**
     * Поиск товара по коду программы.
     * @param programNumber код программы.
     * @return найденный товар.
     */
    @Override
    public Product findByProgramNumber(int programNumber) {
        return productRepository.findByProgramNumber(programNumber);
    }
}
