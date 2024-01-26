package ru.maliutin.diesel.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.repository.UserRepository;
import ru.maliutin.diesel.service.SecurityService;
import ru.maliutin.diesel.service.UserService;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    /**
     * Поле с репозиторием объекта User.
     */
    private final UserRepository userRepository;
    /**
     * Поле с объектом кодирования паролей.
     */
    private final PasswordEncoder passwordEncoder;

    private final SecurityService securityService;

    /**
     * Поиск пользователя id.
     * @param id идентификатор пользователя.
     * @return объект пользователя.
     */
    @Override
    public User getById(Long id) {
        return userRepository
                .findById(id).orElse(null);
    }

    /**
     * Получение пользователя по логину.
     * @param username логин пользователя.
     * @return объект пользователя.
     */
    @Override
    public User getByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElse(null);
    }

    /**
     * Обновление пользователя.
     * @param user объект пользователя.
     * @return объект пользователя.
     */
    @Override
    public User update(User user) {
//         Кодируем сырой пароль пользователя при сохранении в БД
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    /**
     * Обновление пароля пользователя.
     * @param userId идентификатор пользователя.
     * @param password новый пароль
     * @return объект пользователя или null если пользователь не найден.
     */
    @Override
    @Transactional
    public User updatePassword(Long userId, String password) {
        User user = userRepository.findById(userId).orElse(null);
        if(user != null){
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            securityService.delPasswordResetTokenByUserId(userId);
        }
        return user;
    }

    /**
     * Сохранение нового пользователя для БД.
     * @param user объект пользователя.
     */
    @Override
    public void create(User user) {
        userRepository.save(user);
    }

    /**
     * Проверка относится ли заказ к конкретному пользователю.
     * @param userId идентификатор пользователя.
     * @param orderId идентификатор задачи.
     * @return true - если заказ относится, иначе false.
     */
    @Override
    public boolean isOrderOwner(Long userId, Long orderId) {
        return userRepository.isOrderOwner(userId, orderId);
    }

    /**
     * Удаление пользователя.
     * @param id идентификатор пользователя.
     */
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
