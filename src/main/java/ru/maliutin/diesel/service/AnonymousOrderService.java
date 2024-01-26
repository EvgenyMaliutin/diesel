package ru.maliutin.diesel.service;

import ru.maliutin.diesel.domain.order.AnonymousOrder;
import ru.maliutin.diesel.domain.product.Product;

public interface AnonymousOrderService {

    AnonymousOrder getOrderByUserId(String userId);

    AnonymousOrder getOrderById(Long anonymousOrderId);
    void addProductInOrder(String user_id, Product product, Integer amount);
    void updateProductToOrder(String user_id, Product product, Integer amount);
    void removeProductFromOrder(String user_id, Product product);

    void removeOrderById(long anonymousOrderId);

}
