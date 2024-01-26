package ru.maliutin.diesel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.maliutin.diesel.domain.product.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Поиск товаров по наименованию игнорируя регистр.
     * @param findText строка для поиска
     * @return список уникальных товаров.
     */
    List<Product> findDistinctByNameContainingIgnoreCase(String findText);

    /**
     * Поиск товара по каталожному номеру игнорируя регистр.
     * @param findText строка для поиска.
     * @return список уникальных товаров.
     */
    List<Product> findDistinctByCatalogNumberContainingIgnoreCase(String findText);

    /**
     * Поиск товара по марке техники, игнорируя регистр.
     * @param applying техника применения.
     * @return список товаров.
     */
    List<Product> findDistinctByModelCarStartingWithIgnoreCaseOrderByName(String applying);

    /**
     * Поиск товара по внутреннему номеру.
     * @param programNumber внутренний номер.
     * @return найденный товар или null.
     */
    Product findByProgramNumber(int programNumber);
}
