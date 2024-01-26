package ru.maliutin.diesel.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maliutin.diesel.domain.order.AnonymousOrder;
import ru.maliutin.diesel.domain.order.AnonymousOrderProduct;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.repository.AnonymousOrderRepository;
import ru.maliutin.diesel.service.AnonymousOrderService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class AnonymousOrderServiceImpl implements AnonymousOrderService {

    private final AnonymousOrderRepository anonymousOrderRepository;

    /**
     * Получение заказа анонимного пользователя по id.
     * @param userId id пользователя.
     * @return заказ пользователя или null.
     */
    @Override
    @Transactional
    public AnonymousOrder getOrderByUserId(String userId) {
        AnonymousOrder order = anonymousOrderRepository.findByOwner(userId).stream().findFirst().orElse(null);
        // Если заказа не существует
        if (order == null) {
            order = new AnonymousOrder();
            order.setDate(LocalDateTime.now());
            order.setOwner(userId);
            order.setProducts(new ArrayList<>());
            anonymousOrderRepository.save(order);
        }
        return order;
    }

    /**
     * Добавление товара в корзину не аутентифицированного пользователя,
     * в случае наличия товара в заказе будет произведено его увеличение на
     * количество переданное в аргументе amount.
     * @param user_id id пользователя.
     * @param product объект товара.
     * @param amount количество товара.
     */
    @Override
    @Transactional
    public void addProductInOrder(String user_id, Product product, Integer amount) {
        AnonymousOrder order = getOrderByUserId(user_id);
        AnonymousOrderProduct anonymousOrderProduct = new AnonymousOrderProduct();
        anonymousOrderProduct.setId(product.getId());
        anonymousOrderProduct.setProduct(product);
        anonymousOrderProduct.setAmount(checkAmountInOrder(product, amount));
        anonymousOrderProduct.setAnonymousOrder(order);
        // Проверяем есть ли уже продукт в заказе клиента
        // TODO возможно исправить сравнение товаров по объекту, а не по id
        Optional<AnonymousOrderProduct> productInOrderOptional = order.getProducts()
                .stream()
                .filter(prod -> prod.getProduct().getId() == anonymousOrderProduct.getProduct().getId())
                .findFirst();
        // Если есть увеличиваем количество товара
        if (productInOrderOptional.isPresent()) {
            AnonymousOrderProduct productInOrder = productInOrderOptional.get();
            productInOrder.setAmount(
                    checkAmountInOrder(product, productInOrder.getAmount() + amount));
        }
        // Иначе просто добавляем продукт к заказу
        else {
            order.getProducts().add(anonymousOrderProduct);
        }
        anonymousOrderRepository.save(order);
    }

    /**
     * Изменение количества заказанного товара.
     * @param user_id идентификатор пользователя.
     * @param product объект товара.
     * @param amount количество товара устанавливаемое в заказе.
     */
    @Override
    @Transactional
    public void updateProductToOrder(String user_id, Product product, Integer amount) {
        // Проверяем что-бы кол-во товара было положительным
        if (amount > 0) {
            AnonymousOrder order = getOrderByUserId(user_id);
            // Проверяем есть ли продукт в заказе клиента
            Optional<AnonymousOrderProduct> productInOrderOptional = order.getProducts()
                    .stream()
                    .filter(prod -> prod.getProduct().getId() == product.getId())
                    .findFirst();
            // Если продукт есть в заказе, изменяем его количество проверяя, что бы заказа не превысил остаток
            if (productInOrderOptional.isPresent()) {
                AnonymousOrderProduct productInOrder = productInOrderOptional.get();
                productInOrder.setAmount(
                        checkAmountInOrder(product, amount));
            }
            anonymousOrderRepository.save(order);
        }
    }

    /**
     * Удаление товара из анонимного заказа.
     * @param user_id идентификатор пользователя.
     * @param product объект товара.
     */
    @Override
    @Transactional
    public void removeProductFromOrder(String user_id, Product product) {
        AnonymousOrder order = getOrderByUserId(user_id);
        // Проверяем есть ли продукт в заказе клиента
        Optional<AnonymousOrderProduct> productInOrderOptional = order.getProducts()
                .stream()
                .filter(prod -> prod.getProduct().getId() == product.getId())
                .findFirst();
        // Если продукт найден, удаляем его из заказа
        productInOrderOptional.ifPresent(anonymousOrderProduct -> order.getProducts().remove(anonymousOrderProduct));
        anonymousOrderRepository.save(order);
    }

    /**
     * Получение анонимного заказа по его идентификатору.
     * @param anonymousOrderId идентификатор анонимного заказа.
     * @return объект анонимного заказа.
     */
    @Override
    public AnonymousOrder getOrderById(Long anonymousOrderId) {
        return anonymousOrderRepository.getReferenceById(anonymousOrderId);
    }

    @Override
    @Transactional
    public void removeOrderById(long anonymousOrderId) {
        anonymousOrderRepository.deleteById(anonymousOrderId);
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
}
