package com.example.jwt.service;

import com.example.jwt.entity.User;
import com.example.jwt.exception.NoDataFoundException;
import com.example.jwt.exception.UserAlreadyExistException;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;


    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<Object, Object> createToken(User u) {
        String token = jwtTokenProvider.createToken(u.getEmail(), u.getRole().name(), u.getId());

        Map<Object, Object> tokens = new HashMap<>();
        tokens.put("userId", u.getId());
        tokens.put("email", u.getEmail());
        tokens.put("role", u.getRole().name());
        tokens.put("token", token);
        return tokens;
    }

    public Map<Object, Object> authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new NoDataFoundException("User doesn't exists"));
        return createToken(user);
    }

    public Map<Object, Object> register(User user) {
        boolean isUserExist = userRepository.findUserByEmail(user.getEmail()).isPresent();
        if (!isUserExist) {
            String password = user.getPassword();
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            return authenticate(savedUser.getEmail(), password);
        } else {
            throw new UserAlreadyExistException("User already exist");
        }
    }

}
