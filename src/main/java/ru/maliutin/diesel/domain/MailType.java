package ru.maliutin.diesel.domain;

/**
 * Типы отправляемой почты.
 * (регистрация, забыли пароль, перезвоните мне, подтвержденный заказ)
 */
public enum MailType {
    REGISTRATION, FORGOT_PASSWORD, CALL_BACK, CONFIRMED_ORDER
}
