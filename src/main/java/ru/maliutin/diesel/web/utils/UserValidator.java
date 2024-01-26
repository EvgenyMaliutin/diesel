package ru.maliutin.diesel.web.utils;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.service.UserService;

@Component
@AllArgsConstructor
public class UserValidator implements Validator {

    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    /**
     * Метод валидации нового пользователя. Проверка на существования пользователя с таким логином,
     * проверка, что введенные пароли при регистрации совпадают.
     * @param target the object that is to be validated
     * @param errors contextual state about the validation process
     */
    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (userService.getByUsername(user.getUsername()) != null){
            errors.rejectValue("username", "", "Пользователь с таким логином уще существует!");
        }
        if (!user.getPassword().equals(user.getPasswordConfirmation())){
            errors.rejectValue("password", "", "Пароли не совпадают!");
            errors.rejectValue("passwordConfirmation", "", "Пароли не совпадают!");
        }
    }
}
