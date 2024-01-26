package ru.maliutin.diesel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.maliutin.diesel.domain.order.Orders;
import ru.maliutin.diesel.domain.user.User;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

}
