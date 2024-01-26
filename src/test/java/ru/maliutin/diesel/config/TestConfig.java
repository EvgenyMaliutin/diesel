package ru.maliutin.diesel.config;

import lombok.RequiredArgsConstructor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;
import ru.maliutin.diesel.repository.*;
import ru.maliutin.diesel.service.*;
import ru.maliutin.diesel.service.impl.*;

import java.util.concurrent.Executor;

/**
 * Конфигурация для тестирования.
 */

@TestConfiguration
@RequiredArgsConstructor
public class TestConfig {

    @Bean
    @Primary
    public AnonymousOrderRepository anonymousOrderRepository(){
        return Mockito.mock(AnonymousOrderRepository.class);
    }

    @Bean
    @Primary
    public OrderRepository orderRepository(){
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    @Primary
    public PasswordTokenRepository passwordTokenRepository(){
        return Mockito.mock(PasswordTokenRepository.class);
    }

    @Bean
    @Primary
    public ProductRepository productRepository(){
        return Mockito.mock(ProductRepository.class);
    }

    @Bean
    @Primary
    public UserRepository userRepository(){
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager(){
        return Mockito.mock(AuthenticationManager.class);
    }

    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService(){
        return new UsersDetailsService(userRepository());
    }

    @Bean
    @Primary
    public UserService userService(){
        return new UserServiceImpl(userRepository(),
                testPasswordEncoder(), securityService());
    }

    @Bean
    public JavaMailSender mailSender(){
        return Mockito.mock(JavaMailSender.class);
    }

    @Bean
    public TemplateEngine templateEngine(){
        return Mockito.mock(TemplateEngine.class);
    }

    @Bean
    public Executor executor(){
        return Mockito.mock(Executor.class);
    }

    @Bean
    @Primary
    public MailService mailService(){
        return new MailServiceImpl(mailSender(), templateEngine(), executor());
    }

    @Bean
    @Primary
    public AnonymousOrderService anonymousOrderServer(){
        return new AnonymousOrderServiceImpl(anonymousOrderRepository());
    }

    @Bean
    @Primary
    public OrderService orderService(){
        return new OrderServiceImpl(orderRepository(), anonymousOrderServer(), mailService());
    }

    @Bean
    @Primary
    public ProductService productService(){
        return new ProductServiceImpl(productRepository());
    }

    @Bean
    @Primary
    public SecurityService securityService(){
        return new SecurityServiceImpl(passwordTokenRepository());
    }

    @Bean
    @Primary
    public AuthService authService(){
        return new AuthServiceImpl(
                userService(), testPasswordEncoder(), mailService()
        );
    }
}
