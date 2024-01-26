package ru.maliutin.diesel.domain.order;

import jakarta.persistence.*;
import lombok.Data;
import ru.maliutin.diesel.domain.product.Product;

/**
 * Сущность связывающая заказанные продукты с анонимным заказам.
 */
@Entity
@Table(name = "anonymous_order_products")
@Data
public class AnonymousOrderProduct {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_order", referencedColumnName = "id")
    private AnonymousOrder anonymousOrder;

    @ManyToOne
    @JoinColumn(name = "id_product", referencedColumnName = "id")
    private Product product;

    @Column(name = "amount")
    private Integer amount;

    @Override
    public String toString() {
        return "AnonymousOrderProduct{" +
                "id=" + id +
                ", product=" + product +
                ", amount=" + amount +
                '}';
    }
}
