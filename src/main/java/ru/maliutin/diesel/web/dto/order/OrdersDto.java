package ru.maliutin.diesel.web.dto.order;

import lombok.Data;
import ru.maliutin.diesel.domain.order.Status;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.domain.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdersDto {

    private long id;

    private LocalDateTime date;

    private BigDecimal sum;

    private User owner;

    private List<Product> products;

    private Status orderStatus;

}
