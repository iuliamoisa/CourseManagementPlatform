package com.ing.hubs.jpa.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Email(message = "Email must be a valid email address")
    private String email;

    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    private LocalDateTime updatedAt = LocalDateTime.now();
}
