package ru.maliutin.diesel.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.maliutin.diesel.config.TestConfig;
import ru.maliutin.diesel.domain.MailType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
@Import(TestConfig.class)
public class MailServiceImplTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private TemplateEngine templateEngine;

    private String COMPANY_EMAIL = "test";

    @Autowired
    private MailServiceImpl mailService;

    @Test
    public void sendRegistrationEmail(){
        String to = "mailTo";
        MailType type = MailType.REGISTRATION;
        Context context = new Context();
        String textEmail = "some text";
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(mimeMessage);
        Mockito.when(templateEngine.process("mail/register.html", context))
                .thenReturn(textEmail);

        mailService.sendMail(to, type, context);

        Mockito.verify(javaMailSender).createMimeMessage();
        Mockito.verify(templateEngine).process(any(String.class), eq(context));
        Mockito.verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    public void sendForgotPasswordEmail(){
        String to = "mailTo";
        MailType type = MailType.FORGOT_PASSWORD;
        Context context = new Context();
        String textEmail = "some text";
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(mimeMessage);
        Mockito.when(templateEngine.process(
                "mail/forgot_password.html", context))
                .thenReturn(textEmail);

        mailService.sendMail(to, type, context);

        Mockito.verify(javaMailSender).createMimeMessage();
        Mockito.verify(templateEngine).process(any(String.class), eq(context));
        Mockito.verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    public void sendConfirmedOrder(){
        String to = "mailTo";
        MailType type = MailType.CONFIRMED_ORDER;
        Context context = new Context();
        String textEmail = "some text";
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(mimeMessage);
        Mockito.when(templateEngine.process(
                        "mail/confirmed_order_user.html", context))
                .thenReturn(textEmail);
        Mockito.when(templateEngine.process(
                        "mail/confirmed_order_company.html", context))
                .thenReturn(textEmail);

        mailService.sendMail(to, type, context);

        Mockito.verify(javaMailSender, Mockito.times(2))
                .createMimeMessage();
        Mockito.verify(templateEngine, Mockito.times(2))
                .process(any(String.class), eq(context));
        Mockito.verify(javaMailSender, Mockito.times(2))
                .send(any(MimeMessage.class));
    }

    @Test
    public void sendCallBackEmail(){
        String to = "mailTo";
        MailType type = MailType.CALL_BACK;
        Context context = new Context();
        String textEmail = "some text";
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(mimeMessage);
        Mockito.when(templateEngine.process(
                        "mail/call_back.html", context))
                .thenReturn(textEmail);

        mailService.sendMail(to, type, context);

        Mockito.verify(javaMailSender).createMimeMessage();
        Mockito.verify(templateEngine).process(any(String.class), eq(context));
        Mockito.verify(javaMailSender).send(any(MimeMessage.class));
    }
}
