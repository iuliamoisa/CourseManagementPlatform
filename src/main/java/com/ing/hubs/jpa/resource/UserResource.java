package com.ing.hubs.jpa.resource;

import com.ing.hubs.jpa.dto.request.UserRequestDto;
import com.ing.hubs.jpa.dto.request.UserUpdateDto;
import com.ing.hubs.jpa.dto.response.UserResponseDto;
import com.ing.hubs.jpa.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.web.headers.HeadersSecurityMarker;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserResource {
    UserService userService;

    @GetMapping("/all")
    public List<UserResponseDto> getAllUsers(){
        return userService.getAll();
    }

    @GetMapping("/all/{field}")
    public List<UserResponseDto> getAllByRole(@PathVariable String field){
        return userService.getAllByRole(field);
    }

    @GetMapping("/id/{id}")
    public UserResponseDto getUserById (@PathVariable Long id){
        return userService.getUserById(id);
    }

    @PostMapping("/create")
    public UserResponseDto create(@RequestBody @Valid UserRequestDto userCreationDto){
        return userService.createUser(userCreationDto);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id){
        userService.delete(id);
    }

    @PatchMapping("/{id}")
    public UserResponseDto updateUser(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody @Valid UserUpdateDto userUpdateDto) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return userService.updateUser(id, userUpdateDto, jwtToken);
    }

}
