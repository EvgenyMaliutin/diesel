package ru.maliutin.diesel.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.maliutin.diesel.config.TestConfig;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.repository.UserRepository;
import ru.maliutin.diesel.service.SecurityService;

import java.util.Optional;


@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
@Import(TestConfig.class)
public class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private SecurityService securityService;

    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, securityService);
    }

    @Test
    public void getById(){
        Long id = 1L;
        User user = new User();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        User testUser = userService.getById(id);
        Mockito.verify(userRepository).findById(id);
        Assertions.assertEquals(testUser, user);
    }

    @Test
    public void getByIdNotExistingId(){
        Long id = 1L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());
        User testUser = userService.getById(id);
        Mockito.verify(userRepository).findById(id);
        Assertions.assertNull(testUser);
    }

    @Test
    public void getByUsername(){
        String username = "test@mai.ru";
        User user = new User();
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        User testUser = userService.getByUsername(username);
        Mockito.verify(userRepository).findByUsername(username);
        Assertions.assertEquals(testUser, user);
    }

    @Test
    public void getByUsernameNotExistingId(){
        String username = "test@mai.ru";
        Mockito.when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());
        User testUser = userService.getByUsername(username);
        Mockito.verify(userRepository).findByUsername(username);
        Assertions.assertNull(testUser);
    }

    @Test
    public void update(){
        User referenceUser = new User();
        userService.update(referenceUser);
        Mockito.verify(userRepository).save(referenceUser);
    }

    @Test
    public void updatePassword(){
        Long id = 1L;
        String password = "testPassword";
        String referencePassword = "encodingPassword";
        User user = new User();
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.encode(password))
                .thenReturn(referencePassword);
        User testUser = userService.updatePassword(id, password);
        Mockito.verify(userRepository).findById(id);
        Mockito.verify(passwordEncoder).encode(password);
        Mockito.verify(userRepository).save(user);
        Mockito.verify(securityService).delPasswordResetTokenByUserId(id);
        Assertions.assertEquals(referencePassword, testUser.getPassword());
    }

    @Test
    public void create(){
        User user = new User();
        userService.create(user);
        Mockito.verify(userRepository).save(user);
    }

    @Test
    public void IsOrderOwner(){
        Long userId = 1L;
        Long orderId = 1L;
        Mockito.when(userRepository.isOrderOwner(userId, orderId))
                .thenReturn(true);
        boolean testValue = userService.isOrderOwner(userId, orderId);
        Mockito.verify(userRepository).isOrderOwner(userId, orderId);
        Assertions.assertTrue(testValue);
    }

    @Test
    public void delete(){
        Long id = 1L;
        userService.delete(id);
        Mockito.verify(userRepository).deleteById(id);
    }
}
