package ru.maliutin.diesel.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.maliutin.diesel.config.TestConfig;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.repository.UserRepository;

import java.util.Optional;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
@Import(TestConfig.class)
public class UsersDetailsServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UsersDetailsService usersDetailsService;

    @Test
    public void loadByUsername() {
        String username = "some name";
        User user = new User();
        Mockito.when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        UserDetails userDetails = usersDetailsService.loadUserByUsername(username);
        Mockito.verify(userRepository).findByUsername(username);
        Assertions.assertNotNull(userDetails);
    }

    @Test
    public void loadByUsernameUserNotFound() {
        String username = "some name";
        Mockito.when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> usersDetailsService.loadUserByUsername(username));
        Mockito.verify(userRepository).findByUsername(username);
    }
}
