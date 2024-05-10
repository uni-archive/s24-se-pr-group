package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import static at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier.anAdminUser;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.UserValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {


    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenizer jwtTokenizer;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<ApplicationUserDto> userDto;

    @Test
    void createUserShouldCallValidateAndSetSalt() throws ValidationException {
        ApplicationUserDto user = anAdminUser();
        Mockito.doNothing().when(userValidator).validateForCreate(user);
        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");

        userService.createUser(user);

        verify(userValidator).validateForCreate(user);
        verify(userDao).create(userDto.capture());
        Assertions.assertNotNull(userDto.getValue().getSalt());
        Assertions.assertNotNull(userDto.getValue().getPassword());
    }


}