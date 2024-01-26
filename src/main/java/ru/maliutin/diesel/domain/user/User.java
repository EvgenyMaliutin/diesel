package ru.maliutin.diesel.domain.user;

import jakarta.persistence.*;
import lombok.Data;
import ru.maliutin.diesel.domain.order.Orders;

import java.util.List;
import java.util.Set;

/**
 * Сущность пользователя.
 */
@Data  // Автоматически создает геттеры, сеттеры, конструкторы, hashcode, equals;
@Entity
@Table(name = "users")
public class User {
    /**
     * Id пользователя.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * Имя пользователя.
     */
    @Column(name = "name")
    private String name;
    /**
     * Логин пользователя (email).
     */
    @Column(name = "username")
    private String username;
    /**
     * Пароль пользователя.
     */
    @Column(name = "password")
    private String password;
    /**
     * Подтверждение пароля пользователя.
     */
    @Transient
    private String passwordConfirmation;
    /**
     * Поле с номером телефона пользователя.
     */
    @Column(name = "mobile_number")
    private String mobileNumber;
    /**
     * Коллекция ролей пользователя.
     */
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles")
    @Enumerated(value = EnumType.STRING)
    private Set<Role> roles;
    /**
     * Заказы пользователя.
     */
    @OneToMany(mappedBy = "owner")
    private List<Orders> orders;

}
