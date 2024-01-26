package ru.maliutin.diesel.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.context.Context;
import ru.maliutin.diesel.config.TestConfig;
import ru.maliutin.diesel.domain.MailType;
import ru.maliutin.diesel.domain.user.Role;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.service.MailService;
import ru.maliutin.diesel.service.UserService;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
@Import(TestConfig.class)
public class AuthServiceImplTest {

    @MockBean
    private UserService userService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private MailService mailService;

    @Autowired
    private AuthServiceImpl authService;

    @Test
    public void registration(){
        User user = new User();
        String password = "password";
        String encodePassword = "encodePassword";
        user.setUsername("username@mail.ru");
        user.setPassword(password);
        user.setPasswordConfirmation(password);

        Mockito.when(passwordEncoder.encode(password))
                .thenReturn(encodePassword);
        User testUser = authService.registration(user);

        Mockito.verify(passwordEncoder).encode(password);
        Mockito.verify(userService).create(user);
        Mockito.verify(mailService).sendMail(eq(user.getUsername()),
                eq(MailType.REGISTRATION), any(Context.class));

        Assertions.assertEquals(encodePassword, testUser.getPassword());
        Assertions.assertEquals(Set.of(Role.ROLE_USER), testUser.getRoles());
    }
}
