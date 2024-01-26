package ru.maliutin.diesel.web.mappers;

import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.web.dto.user.UserDto;

public interface UserMapper {
    /**
     * Преобразование модели объекта User в объект данных UserDto.
     * @param user объект модели.
     * @return объект данных.
     */
    UserDto toDto(User user);

    /**
     * Преобразование объекта данных UserDto в объект модели User.
     * @param userDto объект данных.
     * @return объект модели.
     */
    User toEntity(UserDto userDto);
}
