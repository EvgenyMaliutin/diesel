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
import org.thymeleaf.context.Context;
import ru.maliutin.diesel.config.TestConfig;
import ru.maliutin.diesel.domain.MailType;
import ru.maliutin.diesel.domain.order.*;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.repository.OrderRepository;
import ru.maliutin.diesel.service.AnonymousOrderService;
import ru.maliutin.diesel.service.MailService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
@Import(TestConfig.class)
public class OrderServiceImplTest {

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private MailService mailService;

    @Autowired
    private AnonymousOrderService anonymousOrderService;

    @Autowired
    private OrderServiceImpl orderService;

    @Test
    public void getCreatedOrderByUserWhenNotCreateOrder(){
        User user = new User();
        user.setOrders(new ArrayList<>());
        Orders order = orderService.getCreatedOrderByUser(user);
        Mockito.verify(orderRepository).save(order);
        Assertions.assertNotNull(order);
        Assertions.assertEquals(Status.CREATE, order.getOrderStatus());
    }

    @Test
    public void getCreatedOrderByUserWhenCreateOrderExpect(){
        User user = new User();
        List<Orders> orders = new ArrayList<>();
        Orders order = new Orders();
        order.setOrderStatus(Status.CREATE);
        orders.add(order);
        user.setOrders(orders);
        Orders testOrder = orderService.getCreatedOrderByUser(user);
        Mockito.verify(orderRepository,
                Mockito.times(0)).save(order);
        Assertions.assertNotNull(order);
        Assertions.assertEquals(testOrder, order);
    }

    // TODO не произведена проверка вызова save в orderRepository, приводит к ошибке
    @Test
    public void addProductToOrderWhenProductNotInOrder(){
        // Инициализация пользователя
        User user = new User();
        user.setOrders(new ArrayList<>());
        // Инициализация товара
        Long productId = 1L;
        Product product = new Product();
        Integer stock = 5;
        product.setId(productId);
        product.setAmount(stock);
        // Количество в заказе
        Integer amount = 1;

        orderService.addProductToOrder(user, product, amount);
        Orders order = user.getOrders().get(0);
        OrderProduct productInOrder = order.getProducts().get(0);
        System.out.println("Статус заказа " + order.getOrderStatus());

        Assertions.assertNotNull(productInOrder);
        Assertions.assertEquals(productInOrder.getAmount(), amount);

    }

    @Test
    public void addProductToOrderWhenProductInOrderExpect(){
        // Инициализация пользователя
        User user = new User();
        OrderProduct orderProduct = new OrderProduct();
        // Инициализация товара
        Long productId = 1L;
        Product product = new Product();
        Integer stock = 5;
        product.setId(productId);
        product.setAmount(stock);

        Integer amountInOrder = 2;
        orderProduct.setProduct(product);
        orderProduct.setAmount(amountInOrder);
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        Orders order = new Orders();
        order.setOrderStatus(Status.CREATE);
        order.setProducts(orderProducts);
        List<Orders> orders = new ArrayList<>();
        orders.add(order);
        user.setOrders(orders);

        // Количество в заказе
        Integer amount = 2;

        orderService.addProductToOrder(user, product, amount);
        Orders testOrder = user.getOrders().get(0);
        OrderProduct productInOrder = testOrder.getProducts().get(0);

        Assertions.assertNotNull(productInOrder);
        Assertions.assertEquals(productInOrder.getAmount(),
                amount + amountInOrder);

    }

    @Test
    public void updateProductToOrder(){
        // Инициализация пользователя
        User user = new User();
        OrderProduct orderProduct = new OrderProduct();
        // Инициализация товара
        Long productId = 1L;
        Product product = new Product();
        Integer stock = 5;
        product.setId(productId);
        product.setAmount(stock);

        Integer amountInOrder = 2;
        orderProduct.setProduct(product);
        orderProduct.setAmount(amountInOrder);
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        Orders order = new Orders();
        order.setOrderStatus(Status.CREATE);
        order.setProducts(orderProducts);
        List<Orders> orders = new ArrayList<>();
        orders.add(order);
        user.setOrders(orders);

        // Количество в заказе
        Integer amount = 2;

        orderService.updateProductToOrder(user, product, amount);

        Orders testOrder = user.getOrders().get(0);
        OrderProduct productInOrder = testOrder.getProducts().get(0);

        Assertions.assertEquals(productInOrder.getAmount(), amount);
    }

    /**
     * Проверка работы служебного метода checkAmountInOrder,
     * когда количество товара в заказе превышает остаток на складе.
     */
    @Test
    public void updateProductToOrderWhenAmountMoreStock(){
        // Инициализация пользователя
        User user = new User();
        OrderProduct orderProduct = new OrderProduct();
        // Инициализация товара
        Long productId = 1L;
        Product product = new Product();
        Integer stock = 5;
        product.setId(productId);
        product.setAmount(stock);

        Integer amountInOrder = 2;
        orderProduct.setProduct(product);
        orderProduct.setAmount(amountInOrder);
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        Orders order = new Orders();
        order.setOrderStatus(Status.CREATE);
        order.setProducts(orderProducts);
        List<Orders> orders = new ArrayList<>();
        orders.add(order);
        user.setOrders(orders);

        // Количество в заказе
        Integer amount = 10;

        orderService.updateProductToOrder(user, product, amount);

        Orders testOrder = user.getOrders().get(0);
        OrderProduct productInOrder = testOrder.getProducts().get(0);

        Assertions.assertEquals(productInOrder.getAmount(), stock);
    }

    @Test
    public void removeProductFromOrder(){
        // Инициализация пользователя
        User user = new User();
        OrderProduct orderProduct = new OrderProduct();
        // Инициализация товара
        Long productId = 1L;
        Product product = new Product();
        Integer stock = 5;
        product.setId(productId);
        product.setAmount(stock);

        Integer amountInOrder = 2;
        orderProduct.setProduct(product);
        orderProduct.setAmount(amountInOrder);
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);
        Orders order = new Orders();
        order.setOrderStatus(Status.CREATE);
        order.setProducts(orderProducts);
        List<Orders> orders = new ArrayList<>();
        orders.add(order);
        user.setOrders(orders);

        orderService.removeProductFromOrder(user, product);

        Mockito.verify(orderRepository).save(order);
        // Проверяем что товар с количеством удален из заказа
        Assertions.assertTrue(user.getOrders().get(0).getProducts().isEmpty());
    }

    @Test
    public void changeStatusOrderById(){
        Long orderId = 1L;
        Status orderStatus = Status.CONFIRMED;
        User user = new User();
        user.setUsername("test");
        Orders order = new Orders();
        order.setOrderStatus(Status.CREATE);
        order.setOwner(user);

        Mockito.when(orderRepository.getReferenceById(orderId)).thenReturn(order);

        orderService.changeStatusOrderById(orderId, orderStatus);
        Mockito.verify(orderRepository).getReferenceById(orderId);
        Mockito.verify(orderRepository).save(order);
        Mockito.verify(mailService)
                .sendMail(eq(user.getUsername()),
                        eq(MailType.CONFIRMED_ORDER),
                        any(Context.class));
        Assertions.assertEquals(order.getOrderStatus(), orderStatus);
    }

    @Test
    public void mergeOrders(){
        Product product = new Product();
        product.setAmount(5);
        // Подготавливаем анонимный заказ
        Long anonymOrderId = 1L;
        AnonymousOrder anonymousOrder = new AnonymousOrder();
        AnonymousOrderProduct anonymousOrderProduct = new AnonymousOrderProduct();
        anonymousOrderProduct.setProduct(product);
        anonymousOrderProduct.setAmount(1);
        anonymousOrder.setProducts(
                new ArrayList<>(Arrays.asList(anonymousOrderProduct)));
        // Подготавливаем заказ аутентифицированного пользователя
        User user = new User();
        Orders order = new Orders();
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setAmount(1);
        order.setProducts(new ArrayList<>(Arrays.asList(orderProduct)));
        order.setOrderStatus(Status.CREATE);
        user.setOrders(new ArrayList<>(Arrays.asList(order)));

        Mockito.when(anonymousOrderService.getOrderById(anonymOrderId))
                .thenReturn(anonymousOrder);
        orderService.mergeOrders(user, anonymOrderId);
        // Проверяемые значения
        Integer expectAmount = 2;
        Integer testAmount = order.getProducts().get(0).getAmount();

        Mockito.verify(orderRepository).save(order);
        Assertions.assertEquals(testAmount, expectAmount);
    }

    @Test
    public void mergeOrdersWhenProductNotOnOrder(){
        Product product = new Product();
        product.setAmount(5);
        // Подготавливаем анонимный заказ
        Long anonymOrderId = 1L;
        AnonymousOrder anonymousOrder = new AnonymousOrder();
        AnonymousOrderProduct anonymousOrderProduct = new AnonymousOrderProduct();
        anonymousOrderProduct.setProduct(product);
        anonymousOrderProduct.setAmount(1);
        anonymousOrder.setProducts(
                new ArrayList<>(Arrays.asList(anonymousOrderProduct)));
        // Подготавливаем заказ аутентифицированного пользователя
        User user = new User();
        Orders order = new Orders();
        order.setProducts(new ArrayList<>());
        order.setOrderStatus(Status.CREATE);
        user.setOrders(new ArrayList<>(Arrays.asList(order)));

        Mockito.when(anonymousOrderService.getOrderById(anonymOrderId))
                .thenReturn(anonymousOrder);
        orderService.mergeOrders(user, anonymOrderId);
        // Проверяемые значения
        Integer expectAmount = 1;
        Integer testAmount = order.getProducts().get(0).getAmount();

        Assertions.assertEquals(testAmount, expectAmount);
    }

    @Test
    public void removeOrderById(){
        Long orderId = 1L;
        orderService.removeOrderById(orderId);
        Mockito.verify(orderRepository).deleteById(orderId);
    }
}
