package ru.maliutin.diesel.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.maliutin.diesel.domain.MailType;
import ru.maliutin.diesel.service.MailService;

import java.util.concurrent.Executor;


/**
 * Сервис отправки электронных писем.
 * TODO обработать исключения.
 */
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    /**
     * Интерфейс Spring Framework,
     * предназначенный для отправки электронных писем из приложений.
     */
    private final JavaMailSender mailSender;
    /**
     * Поле шаблонизатора Thymeleaf.
     */
    private final TemplateEngine templateEngine;


    private final Executor executor;

    /**
     * Электронный адрес компании.
     */
    @Value("${company.email}")
    private String COMPANY_EMAIL;

    /**
     * Отправка письма.
     * @param to адрес получателя
     * @param type тип письма
     * @param context содержание письма.
     */
    @Override
    @Async("mailExecutor")
    public void sendMail(String to, MailType type, Context context) {
        switch (type){
            case REGISTRATION -> sendRegistrationEmail(to, context);
            case FORGOT_PASSWORD -> sendForgotPasswordEmail(to, context);
            case CONFIRMED_ORDER -> sendConfirmedOrder(to, context);
            case CALL_BACK -> sendCallBackEmail(to, context);
        }
    }

    /**
     * Отправка письма при регистрации.
     * @param context данные для письма.
     */
    @SneakyThrows
    private void sendRegistrationEmail(String to, Context context){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false, "UTF-8");
        helper.setSubject(
                "Благодарим за регистрацию в интернет-магазине Кубань Дизель!");
        helper.setTo(to);
        String emailContent = templateEngine.process(
                "mail/register.html", context);
        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }

    /**
     * Отправка письма при восстановлении пароля.
     * @param to адрес получателя.
     * @param context содержание письма.
     */

    @SneakyThrows
    private void sendForgotPasswordEmail(String to, Context context){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false, "UTF-8");
        helper.setSubject(
                "Восстановление пароля.");
        helper.setTo(to);
        String emailContent = templateEngine.process(
                "mail/forgot_password.html", context);
        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }

    /**
     * Подтверждение заказа.
     * Отправляет письма клиенту и компании о подтверждении заказа.
     * @param to адрес получателя.
     * @param context содержание письма.
     */
    @SneakyThrows
    private void sendConfirmedOrder(String to, Context context){
        sendConfirmedOrderUser(to, context);
        sendConfirmedOrderCompany(context);
    }

    /**
     * Перезвонить пользователю.
     * @param to адрес получателя.
     * @param context содержимое письма.
     */

    @SneakyThrows
    private void sendCallBackEmail(String to, Context context){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false, "UTF-8");
        helper.setSubject(
                "Пришел запрос на связь с клиентом!");
        helper.setTo(COMPANY_EMAIL);
        String emailContent = templateEngine.process(
                "mail/call_back.html", context);
        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }

    /**
     * Отправка письма пользователю при подтверждении заказа.
     * @param to адрес пользователя.
     * @param context содержимое письма.
     */
    @SneakyThrows
    private void sendConfirmedOrderUser(String to, Context context){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false, "UTF-8");
        helper.setSubject(
                "Ваш заказ подтвержден!");
        helper.setTo(to);
        String emailContent = templateEngine.process(
                "mail/confirmed_order_user.html", context);
        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }

    /**
     * Отправка письма в компанию при подтверждении заказа.
     * @param context содержимое письма.
     */
    @SneakyThrows
    private void sendConfirmedOrderCompany(Context context){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false, "UTF-8");
        helper.setSubject("Клиент оформил заказ!");
        helper.setTo(COMPANY_EMAIL);
        String emailContext = templateEngine.process(
                "mail/confirmed_order_company.html", context);
        helper.setText(emailContext, true);
        mailSender.send(mimeMessage);
    }
}
