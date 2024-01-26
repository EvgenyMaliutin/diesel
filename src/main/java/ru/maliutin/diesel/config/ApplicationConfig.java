package ru.maliutin.diesel.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.maliutin.diesel.service.impl.UsersDetailsService;

/**
 * Класс конфигурации Spring Security.
 */

@Configuration  // Аннотация Spring - отмечающая класс, как конфигурационный.
@EnableWebSecurity  // Аннотация Spring Security - отмечающая класс, как конфигурационный для Spring Security.
@RequiredArgsConstructor(onConstructor = @__(@Lazy))  // Аннотация lombok - предоставляющая классу конструкторы.
/*
    Благодаря @Lazy, эти аргументы будут созданы только при первом обращении к ним.
    Это может быть полезно, если создание аргументов потребует затратных ресурсов
    или если вы хотите отложить их инициализацию.
    В данном случае используется для предотвращения циклической зависимости между компонентами Spring.
 */
@EnableMethodSecurity  // Аннотация Spring включающая на уровне приложения проверку прав доступа в контроллерах.
public class ApplicationConfig {


    // Сразу используем класс имплементирующий UserDetailsService и реализующий loadUserByUsername
    private final UsersDetailsService detailsService;


    /*   1 вариант:
         Создаем бин DaoAuthenticationProvider данный класс настроен на работу с UserDetailsService
         и производит запрос в БД сравнивая логин и пароль пользователя.
         Создаем бин PasswordEncoder данный класс определяем как буде шифроваться пароль пользователя.
         Данный бин следует передать провайдеру, что бы он понимал, как работать с паролями.
         Создаем бин AuthenticationManager который отправляет запрос на аутентификацию доступным провайдерам.
     */
    // Настройка аутентификации
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(detailsService);
        daoAuthenticationProvider.setPasswordEncoder(getPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Фильтр защиты доступа к страницам приложения с перенаправлением на страницу аутентификации
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                // Правило собственной страницы аутентификации
                .formLogin(login -> login
                        .loginPage("/auth/login")  // Перенаправление на собственную страницу аутентификации (обрати внимание вначале ставиться "/")
                        .permitAll()  // обязательно вызвать метод permitAll() для доступа к странице не аутентифицированных пользователей
                        .loginProcessingUrl("/process_login") // // URL для обработки входа
                        .defaultSuccessUrl("/orders/merge", true)  // URL после успешного входа (второй аргумент указывает, что необходимо всегда перенаправлять пользователя на данную страницу после аутентификации)
                        .failureUrl("/auth/login?error"))  // URL в случае неудачной аутентификации
                // Правило аутентификации при входе на сайт
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/**").authenticated()  // запрещает доступ к личным данным пользователей
                        .anyRequest().permitAll()) // Разрешает доступ ко всем оставшимся страницам сайта
                // Обработка исключений (например при попытке обращения не аутентифицированного пользователя)
                .exceptionHandling(except -> except
                        .authenticationEntryPoint((request, response, authException) -> {  // Точка входа
                            response.sendRedirect("/auth/login"); // Перенаправление на страницу аутентификации
                        }))
                // Выход пользователя из аутентификации (при переходе по данному адресу пользователь разлогинится)
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL запрос приводящий к выходу пользователя из аутентификации
                        .logoutSuccessUrl("/home")); // URL адрес на который будет перенаправлен пользователь после выхода из аутентификации
        return http.build();
    }
}
