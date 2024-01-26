package ru.maliutin.diesel.service;

import org.thymeleaf.context.Context;
import ru.maliutin.diesel.domain.MailType;

/**
 * Интерфейс сервиса отправки электронных писем.
 */
public interface MailService {
    /**
     * Отправка письма.
     * @param to адрес получателя
     * @param type тип письма
     * @param context содержание письма.
     */
    void sendMail(String to, MailType type, Context context);

}
