package com.commerce.usermanagement.repository;

import com.commerce.usermanagement.model.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {

    Mono<User> findByEmail(String email);
    Mono<Void> deleteByEmail(String email);

}
