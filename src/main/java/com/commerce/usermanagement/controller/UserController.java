package com.commerce.usermanagement.controller;

import com.commerce.usermanagement.model.dto.UserDto;
import com.commerce.usermanagement.model.entity.User;
import com.commerce.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @GetMapping("/{email}")
    @SecurityRequirement(name = "Bearer Authentication")
    public Mono<EntityModel<User>> getUser(@PathVariable String email) {
        logger.info("Getting user with email: {}", email);
        return prepareHateoas(userService.getUser(email),
                List.of(linkTo(methodOn(UserController.class).getUser(email)).withSelfRel().toMono()));
    }

    @GetMapping("/all")
    @SecurityRequirement(name = "Bearer Authentication")
    public Flux<EntityModel<User>> getAllUsers() {
        logger.info("Getting all users");
        return prepareHateoas(userService.getAllUsers(),
                List.of(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel().toMono()));

    }

    @PostMapping("/register")
    public Mono<EntityModel<User>> createUser(@RequestBody UserDto userDto) {
        logger.info("Creating user: {}", userDto);
        return prepareHateoas(userService.createUser(userDto),
                List.of(linkTo(methodOn(UserController.class).createUser(userDto)).withSelfRel().toMono()));
    }

    @PutMapping("/{email}")
    @SecurityRequirement(name = "Bearer Authentication")
    public Mono<EntityModel<User>> updateUser(@RequestBody UserDto userDto, @PathVariable String email) {
        logger.info("Updating user: {}", userDto);
        return prepareHateoas(userService.updateUser(email, userDto),
                List.of(linkTo(methodOn(UserController.class).updateUser(userDto, email)).withSelfRel().toMono()));
    }

    @DeleteMapping("/{email}")
    @SecurityRequirement(name = "Bearer Authentication")
    public Mono<Link> deleteUser(@PathVariable String email) {
        logger.info("Deleting user with email: {}", email);
        return userService.deleteUser(email).then(linkTo(methodOn(UserController.class).deleteUser(email)).withSelfRel().toMono());
    }

    private Mono<EntityModel<User>> prepareHateoas(Mono<User> monoUser, List<Mono<Link>> monoLinks) {
        return monoUser.flatMap(user -> Flux.concat(monoLinks)
                .collectList()
                .map(link -> EntityModel.of(user, link)));
    }

    private Flux<EntityModel<User>> prepareHateoas(Flux<User> monoUser, List<Mono<Link>> monoLinks) {
        return monoUser.flatMap(user -> Flux.concat(monoLinks)
                .collectList()
                .map(link -> EntityModel.of(user, link)));
    }

}
