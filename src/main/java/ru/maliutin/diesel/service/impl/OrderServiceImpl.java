package ru.maliutin.diesel.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import ru.maliutin.diesel.domain.MailType;
import ru.maliutin.diesel.domain.order.*;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.repository.OrderRepository;
import ru.maliutin.diesel.service.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AnonymousOrderService anonymousOrderService;
    private final MailService mailService;


    /**
     * Получение коллекции заказа с товарами в состоянии СОЗДАНА
     * по объекту пользователя.
     *
     * @param user объект пользователя.
     * @return
     */
    @Override
    @Transactional
    public Orders getCreatedOrderByUser(User user) {
        List<Orders> userOrders = user.getOrders();
        // Проверяем есть ли у пользователя заказ со статусом CREATE
        for (Orders order : userOrders) {
            if (order.getOrderStatus().equals(Status.CREATE)) {
                return order;
            }
        }
        // Если заказа не было создаем новый заказ
        Orders newOrder = new Orders();
        newOrder.setDate(LocalDateTime.now());
        newOrder.setOwner(user);
        newOrder.setProducts(new ArrayList<>());
        newOrder.setOrderStatus(Status.CREATE);
        user.getOrders().add(newOrder);
        orderRepository.save(newOrder);
        return newOrder;
    }

    /**
     * Добавление нового товара в корзину.
     *
     * @param user    объект пользователя.
     * @param product объект товара.
     * @param amount  количество товара.
     */
    @Override
    @Transactional
    public void addProductToOrder(User user, Product product, Integer amount) {
        Orders order = getCreatedOrderByUser(user);
        OrderProduct addProduct = new OrderProduct();
        addProduct.setOrder(order);
        addProduct.setProduct(product);
        addProduct.setAmount(checkAmountInOrder(product, amount));

        // Проверяем есть ли продукт в заказе клиента
        Optional<OrderProduct> productInOrderOptional = order.getProducts()
                .stream()
                .filter(prod -> addProduct.getProduct().getId() == prod.getProduct().getId())
                .findFirst();
        // Если продукт есть в заказе, увеличиваем его количество
        if(productInOrderOptional.isPresent()){
            OrderProduct productInOrder = productInOrderOptional.get();
            productInOrder.setAmount(
                    checkAmountInOrder(product, productInOrder.getAmount() + amount));
        }
        // Если продукта нет, просто добавляем его
        else{
            order.getProducts().add(addProduct);
        }
        orderRepository.save(order);
    }


    @Override
    public void updateProductToOrder(User user, Product product, Integer amount) {
        // Проверяем что-бы кол-во товара было положительным
        if (amount > 0) {
            Orders order = getCreatedOrderByUser(user);
            // Проверяем есть ли продукт в заказе клиента
            Optional<OrderProduct> productInOrderOptional = order.getProducts()
                    .stream()
                    .filter(prod -> product.getId() == prod.getProduct().getId())
                    .findFirst();
            // Если продукт есть в заказе, изменяем его количество проверяя, что бы заказа не превысил остаток
            if (productInOrderOptional.isPresent()) {
                OrderProduct productInOrder = productInOrderOptional.get();
                productInOrder.setAmount(
                        checkAmountInOrder(product, amount));
            }
            orderRepository.save(order);
        }
    }

    @Override
    @Transactional
    public void removeProductFromOrder(User user, Product product) {
        Orders order = getCreatedOrderByUser(user);
        // Проверяем есть ли продукт в заказе клиента
        Optional<OrderProduct> productInOrderOptional = order.getProducts()
                .stream()
                .filter(prod -> product.getId() == prod.getProduct().getId())
                .findFirst();
        // Если продукт есть в заказе, удаляем его
        productInOrderOptional.ifPresent(orderProduct -> order.getProducts().remove(orderProduct));
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void changeStatusOrderById(long orderId, Status status) {
        Orders order = orderRepository.getReferenceById(orderId);
        order.setOrderStatus(status);
        orderRepository.save(order);
        Context context = new Context();
        context.setVariable("user", order.getOwner());
        context.setVariable("order", order);
        mailService.sendMail(order.getOwner().getUsername(),
                MailType.CONFIRMED_ORDER, context);
    }

    /**
     * Метод слияния анонимного и аут. заказов в статусе CREATE.
     * @param user объект пользователя.
     * @param anonymousOrderId id анонимного заказа.
     */
    @Override
    @Transactional
    public void mergeOrders(User user, Long anonymousOrderId) {
        Orders authOrder = getCreatedOrderByUser(user);
        AnonymousOrder anonymousOrder = anonymousOrderService.getOrderById(anonymousOrderId);

        // Перебираем анонимный заказ
        for (AnonymousOrderProduct anonymProduct : anonymousOrder.getProducts()){
            boolean flag = true;
            // Перебираем аутентифицированный заказ
            for (OrderProduct authProduct : authOrder.getProducts()){
                // Если в аутент. заказе содержится продукт из анонимного заказа
                if(authProduct.getProduct().getProgramNumber() == anonymProduct.getProduct().getProgramNumber()){
                    // Получаем сумму кол-ва товаров из обоих заказов
                    Integer amount = anonymProduct.getAmount() + authProduct.getAmount();

                    // Производим увеличение кол-ва товара в аут. заказе, с проверкой превышения остатка на складе
                    authProduct.setAmount(checkAmountInOrder(authProduct.getProduct(), amount));
                    // Прерываем проверку аутентифицированного заказа
                    flag = false;
                    break;
                }
            }
            // Если товара из анонимного заказа не было в аут. заказе, добавляем его
            if (flag) {
                addProductToOrder(user, anonymProduct.getProduct(), anonymProduct.getAmount());
            }
        }
        orderRepository.save(authOrder);
        anonymousOrderService.removeOrderById(anonymousOrderId);
    }

    /**
     *  Проверка остатка товара на складе по отношению к заказанному товару.
     * @param product объект товара.
     * @param amount количество товара в заказе.
     * @return остаток товара на складе, при превышении в заказе. Или количество в заказе.
     */
    private Integer checkAmountInOrder(Product product, Integer amount){
        if (product.getAmount() < amount)
            return product.getAmount();
        return amount;
    }

    /**
     * Удаление заказа по id
     * @param idOrder идентификатор заказа.
     */
    @Override
    @Transactional
    public void removeOrderById(long idOrder) {
        orderRepository.deleteById(idOrder);
    }
}
