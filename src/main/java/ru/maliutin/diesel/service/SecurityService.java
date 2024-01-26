package ru.maliutin.diesel.service;

import ru.maliutin.diesel.domain.user.User;

public interface SecurityService {
    /**
     * Создание токена для сброса пароля.
     * @param user объект пользователя.
     * @param token секретный токен.
     * @return объект токена.
     */
    void createPasswordResetToken(User user, String token);

    String validatePasswordResetToken(String token);

    User getUserByPasswordResetToken(String token);

    void delPasswordResetTokenByUserId(Long userId);
}
