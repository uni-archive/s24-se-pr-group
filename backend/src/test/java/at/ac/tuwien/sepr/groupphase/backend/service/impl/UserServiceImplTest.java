package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.persistence.dao.UserDao;
import at.ac.tuwien.sepr.groupphase.backend.persistence.exception.EntityNotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.UserValidator;
import at.ac.tuwien.sepr.groupphase.backend.supplier.ApplicationUserSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        ApplicationUserDto user = ApplicationUserSupplier.anAdminUser();
        doNothing().when(userValidator).validateForCreate(user);
        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");

        userService.createUser(user);

        verify(userValidator).validateForCreate(user);
        verify(userDao).create(userDto.capture());
        Assertions.assertNotNull(userDto.getValue().getSalt());
        Assertions.assertNotNull(userDto.getValue().getPassword());
    }

    @Test
    void searchShouldReturnCorrectResults() {
        ApplicationUserSearchDto searchParams = new ApplicationUserSearchDto("Berta", "Muster", "admin@email.com",
            false);
        List<ApplicationUserDto> mockUsers = List.of(ApplicationUserSupplier.anAdminUser(), ApplicationUserSupplier.anAdminUser());

        when(userDao.search(searchParams)).thenReturn(mockUsers.stream());

        Stream<ApplicationUserDto> result = userService.search(searchParams);

        verify(userDao).search(searchParams);
        Assertions.assertEquals(mockUsers, result.collect(Collectors.toList()));
    }

    @Test
    void updateUserStatusByEmailShouldUpdateUser() throws NotFoundException, ValidationException, EntityNotFoundException {
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setEmail("update@email.com");
        String adminEmail = "admin@email.com";

        doNothing().when(userValidator).validateForUpdateStatus(userToUpdate, adminEmail);
        when(userDao.findByEmail(userToUpdate.getEmail())).thenReturn(userToUpdate);
        when(userDao.update(userToUpdate)).thenReturn(userToUpdate);

        ApplicationUserDto updatedUser = userService.updateUserStatusByEmail(userToUpdate, adminEmail);
    }

    @Test
    void updateUserStatusByEmailShouldThrowNotFoundExceptionIfUserDoesNotExist() throws ValidationException {
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setEmail("nonexistent@email.com");
        String adminEmail = "admin@email.com";

        doNothing().when(userValidator).validateForUpdateStatus(userToUpdate, adminEmail);
        when(userDao.findByEmail(userToUpdate.getEmail())).thenReturn(null);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
            userService.updateUserStatusByEmail(userToUpdate, adminEmail);
        });

        verify(userValidator).validateForUpdateStatus(userToUpdate, adminEmail);
        verify(userDao).findByEmail(userToUpdate.getEmail());
        Assertions.assertEquals("Could not update the user with the email address nonexistent@email.com because it does not exist", exception.getMessage());
    }

    @Test
    void updateUserStatusByEmailShouldThrowNotFoundExceptionOnEntityNotFoundException() throws NotFoundException, ValidationException, EntityNotFoundException {
        ApplicationUserDto userToUpdate = ApplicationUserSupplier.anAdminUser();
        userToUpdate.setEmail("update@email.com");
        String adminEmail = "admin@email.com";

        doNothing().when(userValidator).validateForUpdateStatus(userToUpdate, adminEmail);
        when(userDao.findByEmail(userToUpdate.getEmail())).thenReturn(userToUpdate);
        when(userDao.update(userToUpdate)).thenThrow(new EntityNotFoundException(1L));

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
            userService.updateUserStatusByEmail(userToUpdate, adminEmail);
        });

        verify(userValidator).validateForUpdateStatus(userToUpdate, adminEmail);
        verify(userDao).findByEmail(userToUpdate.getEmail());
        verify(userDao).update(userToUpdate);
        Assertions.assertTrue(exception.getCause() instanceof EntityNotFoundException);
    }
}