package ru.maliutin.diesel.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import ru.maliutin.diesel.domain.MailType;
import ru.maliutin.diesel.domain.user.Role;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.repository.PasswordTokenRepository;
import ru.maliutin.diesel.security.PasswordResetToken;
import ru.maliutin.diesel.service.AuthService;
import ru.maliutin.diesel.service.MailService;
import ru.maliutin.diesel.service.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;


    /**
     * Регистрация пользователя.
     * @param user объект пользователя.
     * @return зарегистрированный объект пользователя.
     */

    @Override
    @Transactional
    public User registration(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        user.setRoles(roles);
        userService.create(user);
        Context context = new Context();
        context.setVariable("user", user);
        mailService.sendMail(user.getUsername(), MailType.REGISTRATION, context);
        return user;
    }
}
