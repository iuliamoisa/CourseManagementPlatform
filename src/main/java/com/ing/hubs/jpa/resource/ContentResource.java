package com.ing.hubs.jpa.resource;

import com.ing.hubs.jpa.security.webToken.JwtService;
import com.ing.hubs.jpa.security.webToken.LoginForm;
import com.ing.hubs.jpa.service.SecurityUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ContentResource {
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private SecurityUserDetailsService userDetailsService;

    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody LoginForm loginForm){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginForm.username(), loginForm.password()
        ));
        if(authentication.isAuthenticated()){
            return jwtService.generateToken(userDetailsService.loadUserByUsername(loginForm.username()));
        } else {
            throw new UsernameNotFoundException("Invalid Credentials");
        }
    }
}
