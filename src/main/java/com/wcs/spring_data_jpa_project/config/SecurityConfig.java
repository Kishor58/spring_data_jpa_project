package com.wcs.spring_data_jpa_project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "User Management API",
                version = "1.0",
                description = "API for managing users, registration, login, department assignment, filtering, and more."
        )
)
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/users/register",
                                "/users/search/**",
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));  // your front-end
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Management API")
                        .version("3.0")
                        .description("API documentation for the User Management System"));
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

