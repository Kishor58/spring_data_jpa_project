package com.wcs.spring_data_jpa_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String userName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String address;

    @NotBlank
    private String contact;

    @NotBlank
    private String password;
}
