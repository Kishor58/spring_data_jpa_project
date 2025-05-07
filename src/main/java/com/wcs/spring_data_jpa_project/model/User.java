package com.wcs.spring_data_jpa_project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username can not be blank")
    private String userName;
    @Email(message = "Invalid email please fill correct email !")
    private String email;
    @NotBlank(message = "Address can not be blank")
    private String address;
    @Size(min = 5,max = 10)
    @NotBlank(message = "Contact number should be required ")
    private String contact;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

}
