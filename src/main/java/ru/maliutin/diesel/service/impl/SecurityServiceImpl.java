package ru.maliutin.diesel.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.repository.PasswordTokenRepository;
import ru.maliutin.diesel.security.PasswordResetToken;
import ru.maliutin.diesel.service.SecurityService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Transactional
public class SecurityServiceImpl implements SecurityService {

    private final PasswordTokenRepository passwordTokenRepository;

    /**
     * Создание нового токена восстановления пароля.
     * @param user объект пользователя.
     * @param token секретный токен.
     */
    @Override
    @Transactional
    public void createPasswordResetToken(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(passwordResetToken);
    }

    /**
     * Валидация токена для восстановления пароля.
     * @param token объект токена
     * @return сообщение с ошибкой или null при успешной валидации.
     */
    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passToken = passwordTokenRepository
                .findByToken(token).orElse(null);

        return !isTokenFound(passToken) ? "Не корректный токен!":
                isTokenExpired(passToken) ? "Время жизни токена истекло!":
                        null;
    }

    /**
     * Проверка, что токен не пуст.
     * @param token объект токена.
     * @return true если не пуст, иначе false.
     */
    private boolean isTokenFound(PasswordResetToken token){
        return token != null;
    }

    /**
     * Проверка времени жизни токена.
     * @param token объект токена.
     * @return true если время жизни не истекло, иначе false.
     */
    private boolean isTokenExpired(PasswordResetToken token){
        LocalDateTime dateTime = LocalDateTime.now();
        return token.getExpiryDate().isBefore(dateTime);
    }

    @Override
    public User getUserByPasswordResetToken(String token) {
        return passwordTokenRepository.findByToken(token).get().getUser();
    }

    @Override
    @Transactional
    public void delPasswordResetTokenByUserId(Long userId) {
        passwordTokenRepository.deleteAllByUserId(userId);
    }
}
