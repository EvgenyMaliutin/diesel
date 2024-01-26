package ru.maliutin.diesel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.maliutin.diesel.domain.order.AnonymousOrder;
import ru.maliutin.diesel.domain.product.Product;

import java.util.List;
import java.util.Optional;

public interface AnonymousOrderRepository extends JpaRepository<AnonymousOrder, Long> {

    /**
     * Получение анонимного заказа по id пользователя.
     * @param userId идентификатор пользователя.
     * @return объект Optional.
     */
    Optional<AnonymousOrder> findByOwner(String userId);
}
