package ru.maliutin.diesel.service;

import ru.maliutin.diesel.domain.order.Orders;
import ru.maliutin.diesel.domain.order.Status;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.domain.user.User;

import java.util.List;

public interface OrderService {


    /**
     * Получение коллекции заказа с товарами в состоянии СОЗДАНА
     * по объекту пользователя.
     * @param user объект пользователя.
     * @return коллекция товаров.
     */
    Orders getCreatedOrderByUser(User user);

    void addProductToOrder(User user, Product product, Integer amount);
    void updateProductToOrder(User user, Product product, Integer amount);
    void removeProductFromOrder(User user, Product product);

    void mergeOrders(User user, Long anonymousOrderId);

    void changeStatusOrderById(long orderId, Status status);

    void removeOrderById(long idOrder);

}
