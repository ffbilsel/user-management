package com.commerce.usermanagement.service.impl;

import com.commerce.usermanagement.model.dto.UserDto;
import com.commerce.usermanagement.model.entity.User;
import com.commerce.usermanagement.repository.UserRepository;
import com.commerce.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> getUser(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> createUser(UserDto user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        User userToSave = new User(user, encodedPassword);
        return userRepository.save(userToSave);
    }

    @Override
    public Mono<User> updateUser(String existingEmail, UserDto user) {
        return userRepository.findByEmail(existingEmail)
                .map(u -> {
                    u.setEmail(user.getEmail());
                    u.setPassword(passwordEncoder.encode(user.getPassword()));
                    u.setName(user.getName());
                    u.setRole(user.getRole());
                    return u;
                })
                .flatMap(userRepository::save);
    }

    @Override
    public Mono<Void> deleteUser(String email) {
        return userRepository.deleteByEmail(email);
    }
}
