package com.commerce.usermanagement.model.entity;

import com.commerce.usermanagement.model.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "t_user")
@Data
@NoArgsConstructor
public class User {

    @Id
    private Long id;

    private String name;
    private String email;
    private String password;
    private String role;

    public User(UserDto user, String encodedPassword) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = encodedPassword;
        this.role = user.getRole();
    }

}
