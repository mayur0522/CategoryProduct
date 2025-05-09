package com.Rest.CategoryProduct.Controller;

import com.Rest.CategoryProduct.Model.JwtRequest;
import com.Rest.CategoryProduct.Model.JwtResponse;
import com.Rest.CategoryProduct.Security.JwtHelper;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final MeterRegistry meterRegistry;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;


    @Autowired
    private JwtHelper helper;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        meterRegistry.counter("auth_requests_total", "endpoint", "/auth/login", "method", "POST").increment();
        log.info("User is trying to login...");
        log.info("Validating credentials of user");
        this.doAuthenticate(request.getUsername(), request.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = this.helper.generateToken(userDetails);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername()).build();
        log.info("Valid User");
        log.info("Logged in successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);

        } catch (BadCredentialsException e) {
            log.warn("Invalid Username or Password  !!");
            throw new BadCredentialsException("Invalid Username or Password  !!");
        }

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> exceptionHandler() {
        return new ResponseEntity<>("Credentials Invalid !!", HttpStatus.UNAUTHORIZED);
    }
}
