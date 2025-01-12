package com.ing.hubs.jpa.service;

import com.ing.hubs.jpa.dto.request.UserRequestDto;
import com.ing.hubs.jpa.dto.request.UserUpdateDto;
import com.ing.hubs.jpa.dto.response.UserResponseDto;
import com.ing.hubs.jpa.entity.User;
import com.ing.hubs.jpa.entity.enums.UserRole;
import com.ing.hubs.jpa.exception.StoreException;
import com.ing.hubs.jpa.exception.user.UserAlreadyExistsException;
import com.ing.hubs.jpa.exception.user.UserNotFoundException;
import com.ing.hubs.jpa.repository.UserRepository;
import com.ing.hubs.jpa.security.webToken.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private UserRequestDto userRequestDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setUserRole(UserRole.PROFESSOR);
        userRequestDto.setEmail("gigiduru@email.com");
        userRequestDto.setPassword("gigidurumate");
        userRequestDto.setLastName("Ion");
        userRequestDto.setFirstName("Matei");

        user = new User();
        user.setId(1L);
        user.setUserRole(UserRole.PROFESSOR);
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(userRequestDto.getPassword());
        user.setLastName(userRequestDto.getLastName());
        user.setFirstName(userRequestDto.getFirstName());
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.empty());
        when(modelMapper.map(userRequestDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class))).thenReturn(new UserResponseDto());

        var response = userService.createUser(userRequestDto);

        assertNotNull(response, "Response should not be null");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserThatAlreadyExists() {
        when(userRepository.findByEmail(userRequestDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userRequestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoadUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User loadedUser = userService.loadUser(1L);
        assertEquals(user, loadedUser, "Loaded user should match the expected user");
    }

    @Test
    void shouldThrowExceptionWhenLoadingNonexistentUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.loadUser(1L));
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        UserResponseDto response = userService.getUserById(1L);

        assertNotNull(response, "Response should not be null");
        verify(modelMapper).map(user, UserResponseDto.class);
    }

    @Test
    void shouldGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        List<UserResponseDto> users = userService.getAll();

        assertEquals(1, users.size(), "List should not be null");
        verify(modelMapper).map(user, UserResponseDto.class);
    }

    @Test
    void shouldGetAllUsersByRole() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        List<UserResponseDto> users = userService.getAllByRole("PROFESSOR");

        assertEquals(1, users.size(), "List should not be null");
        verify(modelMapper).map(user, UserResponseDto.class);
    }

    @Test
    void shouldThrowExceptionForInvalidRole() {
        assertThrows(StoreException.class, () -> userService.getAllByRole("ceva"));
    }

    @Test
    void shouldUpdateUser() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFirstName("UpdatedName");
        updateDto.setEmail("updated@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtService.extractEmail(anyString())).thenReturn(user.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        UserResponseDto updatedUser = userService.updateUser(1L, updateDto, "validJwt");

        assertNotNull(updatedUser, "Updated user should not be null");
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingOtherUser() {
        UserUpdateDto updateDto = new UserUpdateDto();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtService.extractEmail(anyString())).thenReturn("other@email.com");

        assertThrows(RuntimeException.class, () -> userService.updateUser(1L, updateDto, "invalidJwt"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }
}
