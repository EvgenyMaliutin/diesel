package ru.maliutin.diesel.service;

import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.security.PasswordResetToken;

/**
 * Интерфейс сервиса аутентификации.
 */
public interface AuthService {

    User registration(User user);

}
