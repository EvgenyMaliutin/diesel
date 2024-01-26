package ru.maliutin.diesel.web.dto.user;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.maliutin.diesel.domain.order.Orders;

import java.util.List;

@Data
@Component
public class UserDto {

    // Идентификатор пользователя
    private long id;
    // Имя пользователя
    @NotEmpty(message = "Имя пользователя не может быть пустым!")
    @Size(max = 255, message = "Имя должно быть менее 255 символов!")
    private String name;
    // Логин пользователя
    @NotEmpty(message = "Электронная почта не может быть пустой!")
    @Pattern(regexp = "^[a-zA-Z0-9]+@[a-z]+\\.[a-z]+$",
            message = "Убедитесь в корректности ввода email")
    @Size(max = 255, message = "Электронная почта должна быть менее 255 символов!")
    private String username;
    // Пароль пользователя
    @NotEmpty(message = "Пароль пользователя не может быть пустым!")
    @Size(min = 4, message = "Пароль не может быть короче 4 символов!")
    private String password;
    // Подтверждение пароля пользователя
    @NotEmpty(message = "Подтверждение пароля не может быть пустым!")
    @Size(min = 4, message = "Пароль не может быть короче 4 символов!")
    private String passwordConfirmation;

    @NotEmpty(message = "Номер телефона не может быть пустым")
    @Pattern(regexp = "^\\+7\\-\\(\\d{3}\\)\\-\\d{3}\\-\\d{2}\\-\\d{2}$",
            message = "Номер телефона должен быть в формате +7-(111)-111-11-11")
    private String mobileNumber;

    private List<Orders> orders;

}
