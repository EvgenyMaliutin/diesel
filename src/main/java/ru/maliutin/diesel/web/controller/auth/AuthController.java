package ru.maliutin.diesel.web.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import ru.maliutin.diesel.domain.MailType;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.service.AuthService;
import ru.maliutin.diesel.service.MailService;
import ru.maliutin.diesel.service.SecurityService;
import ru.maliutin.diesel.service.UserService;
import ru.maliutin.diesel.web.dto.user.UserDto;
import ru.maliutin.diesel.web.mappers.UserMapper;
import ru.maliutin.diesel.web.utils.UserValidator;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final SecurityService securityService;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final UserService userService;
    private final MailService mailService;

    @GetMapping("/login")
    public String authenticate(Model model, HttpSession session){
        model.addAttribute("user", new UserDto());
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new UserDto());
        return "auth/register";
    }

    /**
     * Регистрация пользователя.
     * @param userDto объект передачи данных User
     * @return объект пользователя.
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid UserDto userDto,
                               BindingResult bindingResult){
        User user = userMapper.toEntity(userDto);
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()){
            log.warn("Ошибка при регистрации пользователя");
            log.warn(bindingResult.getFieldErrors().toString());
            return "auth/register";
        }

        authService.registration(user);
        log.warn("Создан новый пользователь: " + user.getName());
        return "redirect:/users/personal";
    }

    @GetMapping("/forgot_password")
    public String forgotPassword(){
        return "auth/forgot_password";
    }

    @PostMapping("/forgot_password")
    public String resetPassword(@RequestParam("username")String username,
                               HttpServletRequest request,
                               Model model){
        User user = userService.getByUsername(username.toLowerCase());
        if(user != null){
            // логика отправки письма
            String token = UUID.randomUUID().toString();
            securityService.createPasswordResetToken(user, token);
            String baseURL = request.getRequestURL().toString();
            int index = baseURL.lastIndexOf('/');
            baseURL = baseURL.substring(0, index + 1);
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("token", token);
            context.setVariable("url", baseURL);
            mailService.sendMail(username, MailType.FORGOT_PASSWORD, context);
            model.addAttribute("confirm",
                    true);
        }else{
            // Если пользователь не найден
            model.addAttribute("error",
                    "Пользователь с таким email не найден!");
        }
        return "auth/forgot_password";
    }

    @GetMapping("/changePassword")
    public String changePassword(@RequestParam("token") String token,
                                 Model model){
        String result = securityService.validatePasswordResetToken(token);
        if (result != null){
            // Отправляем ошибки
            model.addAttribute("error", result);
            return "errors/error_change_password";
        }else{
            // Отправляем на страницу изменения пароля
            User user = securityService.getUserByPasswordResetToken(token);
            model.addAttribute("id_user", user.getId());
            return "auth/update_password";
        }
    }

    @PostMapping("/changePassword")
    public String changedPassword(@RequestParam("password") String password,
                                  @RequestParam("password_confirmation") String passwordConfirmation,
                                  @RequestParam("id_user") Long idUser,
                                  Model model){
        if (!password.equals(passwordConfirmation)){
            model.addAttribute("error", "Пароли не совпадают");
            model.addAttribute("id_user", idUser);
            return "auth/update_password";
        }else{
            userService.updatePassword(idUser, password);
            return "redirect:/auth/login";
        }
    }
}
