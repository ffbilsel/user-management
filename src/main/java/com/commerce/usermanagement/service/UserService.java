package com.commerce.usermanagement.service;

import com.commerce.usermanagement.model.dto.UserDto;
import com.commerce.usermanagement.model.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> getUser(String email);
    Flux<User> getAllUsers();
    Mono<User> createUser(UserDto user);
    Mono<User> updateUser(String existingEmail, UserDto user);

    Mono<Void> deleteUser(String email);

}
