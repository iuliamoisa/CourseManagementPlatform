package com.ing.hubs.jpa.service;

import com.ing.hubs.jpa.dto.request.UserRequestDto;
import com.ing.hubs.jpa.dto.request.UserUpdateDto;
import com.ing.hubs.jpa.dto.response.UserResponseDto;
import com.ing.hubs.jpa.entity.User;
import com.ing.hubs.jpa.entity.enums.UserRole;
import com.ing.hubs.jpa.exception.StoreException;
import com.ing.hubs.jpa.exception.user.UserAlreadyExistsException;
import com.ing.hubs.jpa.repository.UserRepository;
import com.ing.hubs.jpa.security.webToken.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ing.hubs.jpa.exception.user.UserNotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .toList();
    }

    public List<UserResponseDto> getAllByRole(String field){
        try {
            UserRole role = UserRole.valueOf(field.toUpperCase());
            return userRepository.findAll().stream()
                    .filter(user -> user.getUserRole() == role)
                    .map(user -> modelMapper.map(user, UserResponseDto.class))
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new StoreException(HttpStatus.BAD_REQUEST, "INVALID_USER_ROLE");
        }
    }

    public UserResponseDto getUserById(Long id){
        var user = loadUser(id);
        return modelMapper.map(user, UserResponseDto.class);
    }

    public UserResponseDto createUser(UserRequestDto userCreationDto){
        if (userRepository.findByEmail(userCreationDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        var user = modelMapper.map(userCreationDto, User.class);
        user.setPassword(passwordEncoder.encode(userCreationDto.getPassword()));
        var savedEntity = userRepository.save(user);
        return modelMapper.map(savedEntity, UserResponseDto.class);
    }

    public User loadUser(Long id){
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserResponseDto updateUser(Long id, @Valid UserUpdateDto userUpdateDto, String jwtToken){
        String emailFromJwt = jwtService.extractEmail(jwtToken);
        var user = loadUser(id);
        if(!user.getEmail().equals(emailFromJwt)){
            throw new RuntimeException("You can't edit other users.");
        }
        if(userUpdateDto.getEmail() != null){
            user.setEmail(userUpdateDto.getEmail());
        }
        if(userUpdateDto.getPassword() != null){
            user.setPassword(userUpdateDto.getPassword());
        }
        if(userUpdateDto.getFirstName() != null){
            user.setFirstName(userUpdateDto.getFirstName());
        }
        if(userUpdateDto.getLastName() != null){
            user.setLastName(userUpdateDto.getLastName());
        }
        user.setUpdatedAt(userUpdateDto.getUpdatedAt());
        var updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserResponseDto.class);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
