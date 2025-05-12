package com.wcs.spring_data_jpa_project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/users/register",
                                "/users/login",
                                "/users/save",
                                "/users/getUser/**",
                                "/users/updateUser/**",
                                "/users/deleteUser/**",
                                "/users/assignUserToDepartment",
                                "/users/getAllUsers",
                                "/users/city/**",
                                "/users/countByEmailDomain/**",
                                "/users/native",
                                "/users/getUserByCity/**",
                                "/users/updateEmailByCity",
                                "/users/deleteByCity",
                                "/users/filterByCityAndContact",
                                "/users/getUserSortedByNameAsc",
                                "/users/getUsersSortedByEmailDesc",
                                "/users/paginated",
                                "/users/with-department",
                                "/users/by-department/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // Use Customizer to avoid deprecation

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

