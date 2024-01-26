package ru.maliutin.diesel.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.maliutin.diesel.config.TestConfig;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.repository.ProductRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
@Import(TestConfig.class)
public class ProductServiceImplTest {

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductServiceImpl productService;

    @Test
    public void findAll(){
        List<Product> products = new ArrayList<>(List.of(new Product()));
        Mockito.when(productRepository.findAll()).thenReturn(products);

        List<Product> testProducts = productService.findAll();
        Mockito.verify(productRepository).findAll();
        Assertions.assertEquals(products.size(), testProducts.size());
    }

    @Test
    public void findById(){
        long productId = 1L;
        Product product = new Product();
        Mockito.when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        Product testProduct = productService.findById(productId);

        Mockito.verify(productRepository).findById(productId);
        Assertions.assertEquals(product, testProduct);
    }

    @Test
    public void findByIdNotExists(){
        long productId = 1L;
        Mockito.when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        Product testProduct = productService.findById(productId);

        Mockito.verify(productRepository).findById(productId);
        Assertions.assertNull(testProduct);
    }

    @Test
    public void find(){
        String findText = "text";
        Product product = new Product();
        List<Product> products = new ArrayList<>(Arrays.asList(product, product));
        Mockito.when(
                productRepository.findDistinctByNameContainingIgnoreCase(findText))
                .thenReturn(products);
        Mockito.when(
                productRepository.findDistinctByCatalogNumberContainingIgnoreCase(findText))
                .thenReturn(products);
        int expectSize = 1;
        List<Product> testProducts = productService.find(findText);

        Mockito.verify(productRepository)
                .findDistinctByNameContainingIgnoreCase(findText);
        Mockito.verify(productRepository)
                .findDistinctByCatalogNumberContainingIgnoreCase(findText);
        Assertions.assertEquals(expectSize, testProducts.size());
    }

    @Test
    public void findByModelCar(){
        String modelCar = "test";
        Product product = new Product();
        List<Product> products = new ArrayList<>(List.of(product));

        Mockito.when(
                productRepository.findDistinctByModelCarStartingWithIgnoreCaseOrderByName(modelCar))
                .thenReturn(products);
        List<Product> testProducts = productService.findByModelCar(modelCar);

        Mockito.verify(productRepository)
                .findDistinctByModelCarStartingWithIgnoreCaseOrderByName(modelCar);
        Assertions.assertEquals(products.size(), testProducts.size());
    }

    @Test
    public void findByProgramNumber(){
        int programNumber = 1111;
        Product product = new Product();
        Mockito.when(productRepository.findByProgramNumber(programNumber))
                .thenReturn(product);

        Product testProduct = productService.findByProgramNumber(programNumber);

        Mockito.verify(productRepository).findByProgramNumber(programNumber);
        Assertions.assertEquals(product, testProduct);
    }

    @Test
    public void findByProgramNumberNotExists(){
        int programNumber = 1111;
        Mockito.when(productRepository.findByProgramNumber(programNumber))
                .thenReturn(null);

        Product testProduct = productService.findByProgramNumber(programNumber);

        Mockito.verify(productRepository).findByProgramNumber(programNumber);
        Assertions.assertNull(testProduct);
    }
}
