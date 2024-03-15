package ru.maliutin.diesel.domain.order;

import jakarta.persistence.*;
import lombok.Data;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.domain.user.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Сущность заказа.
 */

@Entity
@Table(name = "orders")
@Data
public class Orders {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date")
    private LocalDateTime date;

    @Transient
    private BigDecimal sum;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User owner;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderProduct> products;

    @Column(name = "order_status")
    @Enumerated(value = EnumType.STRING)
    private Status orderStatus;

    /**
     * Метод для расчета суммы заказа
     * @return сумма заказа.
     */
    public BigDecimal getSum() {
        if (products == null || products.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return products.stream()
                .map(orderProduct -> orderProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(orderProduct.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.UP);
    }

    public String getDateFormat(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return this.date.format(formatter);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date=" + getDateFormat() +
                ", owner='" + owner.getUsername() + '\'' +
                ", products=" + getProductsAsString() +
                ", status= " + orderStatus +
                '}';
    }

    /**
     * Служебный метод для вывода списка заказанных товаров и их кол-ва.
     * @return строку со списком товаров и количеством.
     */
    private String getProductsAsString() {
        if (products == null || products.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (OrderProduct product : products) {
            sb.append(product.getProduct().getName())
                    .append(" - ")
                    .append(product.getAmount())
                    .append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("]");
        return sb.toString();
    }
}
