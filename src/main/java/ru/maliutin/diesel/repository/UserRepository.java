package ru.maliutin.diesel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.maliutin.diesel.domain.user.User;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Получение пользователя по логину
     * @param username логин пользователя (email)
     * @return объект Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверка принадлежности заказа пользователю.
     * @param user_id идентификатор пользователя.
     * @param order_id идентификатор заказа.
     * @return булево значение true - если принадлежит, иначе false.
     */
    @Query(value = """
            SELECT exists(SELECT 1
                  FROM orders
                  WHERE id_user = :userId
                    AND id = :orderId)""", nativeQuery = true)
    boolean isOrderOwner(@Param("userId")Long user_id, @Param("orderId")Long order_id);
}
