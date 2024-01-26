package ru.maliutin.diesel.service;

import ru.maliutin.diesel.domain.user.User;

/**
 * Класс содержащий бизнес-логику программы.
 * Осуществляет запросы к репозиторию и взаимодействующий с моделью User.
 */
public interface UserService {
    /**
     * Получение пользователя по его id.
     * @param id идентификатор пользователя.
     * @return объект пользователя или null в случае его отсутствия.
     */
    User getById(Long id);

    /**
     * Получение пользователя по username (логину).
     * @param username логин пользователя.
     * @return объект пользователя или null в случае его отсутствия.
     */
    User getByUsername(String username);

    /**
     * Обновление информации о пользователе.
     * @param user объект пользователя.
     * @return обновленный объект пользователя.
     */
    User update(User user);

    User updatePassword(Long userId, String password);

    /**
     * Создание нового пользователя.
     * @param user объект пользователя.
     */
    void create(User user);

    /**
     * Проверка принадлежит ли задача пользователю.
     * @param userId идентификатор пользователя.
     * @param taskId идентификатор задачи.
     * @return true - если задача принадлежит пользователю, иначе - false.
     */
    boolean isOrderOwner(Long userId, Long taskId);

    /**
     * Удаление пользователя.
     * @param id идентификатор пользователя.
     */
    void delete(Long id);

}
