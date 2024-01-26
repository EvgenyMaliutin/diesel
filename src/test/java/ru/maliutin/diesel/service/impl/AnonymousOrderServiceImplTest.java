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
import ru.maliutin.diesel.domain.order.AnonymousOrder;
import ru.maliutin.diesel.domain.order.AnonymousOrderProduct;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.repository.AnonymousOrderRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
@Import(TestConfig.class)
public class AnonymousOrderServiceImplTest {

    @MockBean
    private AnonymousOrderRepository anonymousOrderRepository;

    @Autowired
    private AnonymousOrderServiceImpl anonymousOrderService;

    @Test
    public void getOrderByUserId(){
        String userId = UUID.randomUUID().toString();
        AnonymousOrder anonymousOrder = new AnonymousOrder();
        Mockito.when(anonymousOrderRepository.findByOwner(userId))
                .thenReturn(Optional.of(anonymousOrder));
        AnonymousOrder testAnonymousOrder =
                anonymousOrderService.getOrderByUserId(userId);
        Mockito.verify(anonymousOrderRepository).findByOwner(userId);
        Assertions.assertEquals(testAnonymousOrder, anonymousOrder);
    }

    @Test
    public void getOrderByUserIdNotExistingId(){
        String userId = UUID.randomUUID().toString();
        Mockito.when(anonymousOrderRepository.findByOwner(userId))
                .thenReturn(Optional.empty());
        AnonymousOrder testAnonymousOrder =
                anonymousOrderService.getOrderByUserId(userId);
        Mockito.verify(anonymousOrderRepository).findByOwner(userId);
        Mockito.verify(anonymousOrderRepository).save(testAnonymousOrder);
        Assertions.assertNotNull(testAnonymousOrder.getDate());
        Assertions.assertNotNull(testAnonymousOrder.getOwner());
        Assertions.assertNotNull(testAnonymousOrder.getProducts());
    }

    @Test
    public void addProductInOrderWhenProductNotPresent(){
        String userId = UUID.randomUUID().toString();
        Product product = new Product();
        product.setId(1L);  // Задаем id продукта
        product.setAmount(5); // Задаем остаток на складе
        Integer amountInOrder = 1; // Количество в заказе
        AnonymousOrder anonymousOrder = new AnonymousOrder();
        anonymousOrder.setProducts(new ArrayList<>());
        Mockito.when(anonymousOrderRepository.findByOwner(userId))
                .thenReturn(Optional.of(anonymousOrder));
        anonymousOrderService.addProductInOrder(userId, product, amountInOrder);
        Mockito.verify(anonymousOrderRepository).save(anonymousOrder);
        AnonymousOrderProduct testProduct = anonymousOrder
                .getProducts().stream().findFirst().orElse(null);
        Assertions.assertEquals(testProduct.getProduct().getId(), product.getId());
        Assertions.assertEquals(testProduct.getAmount(), amountInOrder);
    }

    @Test
    public void addProductInOrderWhenProductPresent(){
        String userId = UUID.randomUUID().toString();
        Product product = new Product();
        product.setId(1L);  // Задаем id продукта
        product.setAmount(5); // Задаем остаток на складе
        Integer amountInOrder = 1; // Количество в заказе
        AnonymousOrder anonymousOrder = new AnonymousOrder();
        // Добавляем в анонимный заказ товар
        anonymousOrder.setProducts(new ArrayList<>());
        AnonymousOrderProduct anonymousOrderProduct = new AnonymousOrderProduct();
        anonymousOrderProduct.setProduct(product);
        anonymousOrderProduct.setAnonymousOrder(anonymousOrder);
        anonymousOrderProduct.setAmount(amountInOrder);
        anonymousOrder.getProducts().add(anonymousOrderProduct);
        // Проверяем как меняется его значение
        Mockito.when(anonymousOrderRepository.findByOwner(userId))
                .thenReturn(Optional.of(anonymousOrder));
        anonymousOrderService.addProductInOrder(userId, product, amountInOrder);
        Mockito.verify(anonymousOrderRepository).save(anonymousOrder);
        AnonymousOrderProduct testProduct = anonymousOrder
                .getProducts().stream().findFirst().orElse(null);
        Assertions.assertEquals(testProduct.getProduct().getId(), product.getId());
        Assertions.assertEquals(testProduct.getAmount(), amountInOrder * 2);
    }

    @Test
    public void updateProductToOrder(){
        String userId = UUID.randomUUID().toString();
        Product product = new Product();
        product.setId(1L);  // Задаем id продукта
        product.setAmount(5); // Задаем остаток на складе
        Integer amountInOrder = 4; // Количество ранее заказанного товара
        Integer amount = 2; // Измененное количество заказанного товара
        AnonymousOrder anonymousOrder = new AnonymousOrder();
        // Добавляем в анонимный заказ товар
        anonymousOrder.setProducts(new ArrayList<>());
        AnonymousOrderProduct anonymousOrderProduct = new AnonymousOrderProduct();
        anonymousOrderProduct.setProduct(product);
        anonymousOrderProduct.setAnonymousOrder(anonymousOrder);
        anonymousOrderProduct.setAmount(amountInOrder);
        anonymousOrder.getProducts().add(anonymousOrderProduct);
        // Проверяем как меняется его значение
        Mockito.when(anonymousOrderRepository.findByOwner(userId))
                .thenReturn(Optional.of(anonymousOrder));

        anonymousOrderService.updateProductToOrder(userId, product, amount);

        Mockito.verify(anonymousOrderRepository).save(anonymousOrder);
        AnonymousOrderProduct testProduct = anonymousOrder
                .getProducts().stream().findFirst().orElse(null);
        Assertions.assertEquals(testProduct.getProduct().getId(), product.getId());
        Assertions.assertEquals(amount, testProduct.getAmount());
    }

    /**
     * Тестирование при изменении заказного товара на количество
     * превышающее остаток на складе.
     * Тестирование служебного метода checkAmountInOrder().
     */
    @Test
    public void updateProductToOrderMoreThanStock(){
        String userId = UUID.randomUUID().toString();
        Product product = new Product();
        Integer stock = 5;
        product.setId(1L);  // Задаем id продукта
        product.setAmount(stock); // Задаем остаток на складе
        Integer amountInOrder = 2; // Количество ранее заказанного товара
        Integer amount = 6; // Измененное количество заказанного товара
        AnonymousOrder anonymousOrder = new AnonymousOrder();
        // Добавляем в анонимный заказ товар
        anonymousOrder.setProducts(new ArrayList<>());
        AnonymousOrderProduct anonymousOrderProduct = new AnonymousOrderProduct();
        anonymousOrderProduct.setProduct(product);
        anonymousOrderProduct.setAnonymousOrder(anonymousOrder);
        anonymousOrderProduct.setAmount(amountInOrder);
        anonymousOrder.getProducts().add(anonymousOrderProduct);
        // Проверяем как меняется его значение
        Mockito.when(anonymousOrderRepository.findByOwner(userId))
                .thenReturn(Optional.of(anonymousOrder));

        anonymousOrderService.updateProductToOrder(userId, product, amount);

        Mockito.verify(anonymousOrderRepository).save(anonymousOrder);
        AnonymousOrderProduct testProduct = anonymousOrder
                .getProducts().stream().findFirst().orElse(null);
        Assertions.assertEquals(testProduct.getProduct().getId(), product.getId());
        Assertions.assertEquals(stock, testProduct.getAmount());
    }

    @Test
    public void removeProductFromOrder(){
        String userId = UUID.randomUUID().toString();
        Product product = new Product();
        product.setId(1L);  // Задаем id продукта
        product.setAmount(5); // Задаем остаток на складе
        Integer amountInOrder = 2; // Количество ранее заказанного товара
        AnonymousOrder anonymousOrder = new AnonymousOrder();
        // Добавляем в анонимный заказ товар
        anonymousOrder.setProducts(new ArrayList<>());
        AnonymousOrderProduct anonymousOrderProduct = new AnonymousOrderProduct();
        anonymousOrderProduct.setProduct(product);
        anonymousOrderProduct.setAnonymousOrder(anonymousOrder);
        anonymousOrderProduct.setAmount(amountInOrder);
        anonymousOrder.getProducts().add(anonymousOrderProduct);
        // Проверяем удаление товара из заказа
        Mockito.when(anonymousOrderRepository.findByOwner(userId))
                .thenReturn(Optional.of(anonymousOrder));

        anonymousOrderService.removeProductFromOrder(userId, product);

        Mockito.verify(anonymousOrderRepository).save(anonymousOrder);
        AnonymousOrderProduct testProduct = anonymousOrder
                .getProducts().stream().findFirst().orElse(null);
        Assertions.assertNull(testProduct);
    }

    @Test
    public void getOrderById(){
        Long anonymousOrderId = 1L;
        AnonymousOrder anonymousOrder = new AnonymousOrder();
        anonymousOrder.setId(anonymousOrderId);

        Mockito.when(anonymousOrderRepository.getReferenceById(anonymousOrderId))
                .thenReturn(anonymousOrder);

        AnonymousOrder testOrder =
                anonymousOrderService.getOrderById(anonymousOrderId);

        Mockito.verify(anonymousOrderRepository).getReferenceById(anonymousOrderId);
        Assertions.assertEquals(testOrder, anonymousOrder);
    }

    @Test
    public void removeOrderById(){
        Long anonymousOrderId = 1L;
        anonymousOrderService.removeOrderById(anonymousOrderId);
        Mockito.verify(anonymousOrderRepository).deleteById(anonymousOrderId);
    }


}
