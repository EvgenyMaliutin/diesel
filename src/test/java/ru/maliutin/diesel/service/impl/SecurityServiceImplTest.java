package ru.maliutin.diesel.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.maliutin.diesel.config.TestConfig;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.repository.PasswordTokenRepository;
import ru.maliutin.diesel.security.PasswordResetToken;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
@Import(TestConfig.class)
public class SecurityServiceImplTest {

    @MockBean
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    private SecurityServiceImpl securityService;

    @Test
    public void createPasswordResetToken(){
        User user = new User();
        String token = "token";

        securityService.createPasswordResetToken(user, token);

        Mockito.verify(passwordTokenRepository)
                .save(any(PasswordResetToken.class));
    }

    @Test
    public void validatePasswordResetTokenCorrect(){
        String token = "token";
        User user = new User();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        Mockito.when(passwordTokenRepository.findByToken(token))
                .thenReturn(Optional.of(resetToken));

        String actualResponse = securityService.validatePasswordResetToken(token);

        Mockito.verify(passwordTokenRepository).findByToken(token);
        Assertions.assertNull(actualResponse);
    }

    /**
     * Проверка работы служебного метода isTokenFound().
     */
    @Test
    public void validatePasswordResetTokenIncorrectToken(){
        String token = "token";
        Mockito.when(passwordTokenRepository.findByToken(token))
                .thenReturn(Optional.empty());
        String expectResponse = "Не корректный токен!";
        String actualResponse = securityService.validatePasswordResetToken(token);

        Mockito.verify(passwordTokenRepository).findByToken(token);
        Assertions.assertEquals(expectResponse, actualResponse);
    }

    /**
     * Проверка работы служебного метода isTokenExpired().
     */
    @Test
    public void validatePasswordResetTokenExpiredToken(){
        String token = "token";
        User user = new User();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        resetToken.setExpiryDate(LocalDateTime.now().minusDays(2L));
        Mockito.when(passwordTokenRepository.findByToken(token))
                .thenReturn(Optional.of(resetToken));
        String expectResponse = "Время жизни токена истекло!";
        String actualResponse = securityService.validatePasswordResetToken(token);

        Mockito.verify(passwordTokenRepository).findByToken(token);
        Assertions.assertEquals(expectResponse, actualResponse);
    }

    @Test
    public void getUserByPasswordResetToken(){
        String token = "token";
        User user = new User();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        Mockito.when(passwordTokenRepository.findByToken(token))
                .thenReturn(Optional.of(resetToken));

        User testUser = securityService.getUserByPasswordResetToken(token);

        Mockito.verify(passwordTokenRepository).findByToken(token);
        Assertions.assertEquals(user, testUser);
    }

    @Test
    public void delPasswordResetTokenByUserId(){
        Long userId = 1L;

        securityService.delPasswordResetTokenByUserId(userId);

        Mockito.verify(passwordTokenRepository).deleteAllByUserId(userId);
    }
}
