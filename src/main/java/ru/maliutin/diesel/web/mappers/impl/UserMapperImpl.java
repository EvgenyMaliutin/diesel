package ru.maliutin.diesel.web.mappers.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.web.dto.user.UserDto;
import ru.maliutin.diesel.web.mappers.OrderMapper;
import ru.maliutin.diesel.web.mappers.UserMapper;

@Service
@AllArgsConstructor
public class UserMapperImpl implements UserMapper {

    private final OrderMapper orderMapper;

    @Override
    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setMobileNumber(user.getMobileNumber());
        userDto.setOrders(user.getOrders());
        return userDto;
    }

    @Override
    public User toEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setPasswordConfirmation(userDto.getPasswordConfirmation());
        user.setMobileNumber(userDto.getMobileNumber());
        return user;
    }
}
