package ru.maliutin.diesel.domain.order;

/**
 * Статусы заказа: Создан, Подтвержден, Оплачен, Выполнен.
 */
public enum Status {
    CREATE, CONFIRMED, PAID, COMPLETED
}
