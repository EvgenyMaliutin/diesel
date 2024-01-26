package ru.maliutin.diesel.domain.order;

import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Сущность анонимного заказа (пока пользователь не аутентифицировался).
 */

@Entity
@Table(name = "anonymous_order")
@Data
public class AnonymousOrder {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date")
    private LocalDateTime date;

    @Transient
    private BigDecimal sum;

    @Column(name = "id_user")
    private String owner;

    @OneToMany(mappedBy = "anonymousOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnonymousOrderProduct> products;

    // Метод для расчета суммы заказа
    public BigDecimal getSum() {
        if (products == null || products.isEmpty()) {
            return BigDecimal.ZERO; // или null, в зависимости от вашего выбора
        }

        return products.stream()
                .map(orderProduct -> orderProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(orderProduct.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.UP);
    }

    // Метод получения времени создания заказа в отформатированном виде
    public String getDateFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return this.date.format(formatter);
    }

    @Override
    public String toString() {
        return "AnonymousOrder{" +
                "id=" + id +
                ", date=" + getDateFormat() +
                ", owner='" + owner + '\'' +
                ", products=" + getProductsAsString() +
                '}';
    }

    private String getProductsAsString() {
        if (products == null || products.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (AnonymousOrderProduct product : products) {
            sb.append(product.getProduct().getName())
                    .append(" - ")
                    .append(product.getAmount())
                    .append(", ");
        }
        sb.delete(sb.length() - 2, sb.length()); // удалить последнюю запятую и пробел
        sb.append("]");

        return sb.toString();
    }
}
