package com.ing.hubs.jpa.service;

import com.ing.hubs.jpa.entity.User;
import com.ing.hubs.jpa.exception.user.UserNotFoundException;
import com.ing.hubs.jpa.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        var userObj = user.orElseThrow(UserNotFoundException::new);
        return org.springframework.security.core.userdetails.User.builder()
                .username(userObj.getEmail())
                .password(userObj.getPassword())
                .roles(userObj.getUserRole().name())
                .build();
    }
}
