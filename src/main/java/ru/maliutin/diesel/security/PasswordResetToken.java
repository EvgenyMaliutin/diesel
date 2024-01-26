package ru.maliutin.diesel.security;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.maliutin.diesel.domain.user.User;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Сущность токена для сброса пароля.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    /**
     * Время жизни токена
     */
    private static final int EXPIRATION_DAY = 1;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name="id_user")
    private User user;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public PasswordResetToken(String token, User user){
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusDays(EXPIRATION_DAY);
    }
}
