package com.medical.authservice.dto.request;

import com.medical.authservice.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String password;
    private Role role;
}

