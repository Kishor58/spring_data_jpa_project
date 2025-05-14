package com.wcs.spring_data_jpa_project.controller;

import com.wcs.spring_data_jpa_project.customeResponse.ApiResponse;
import com.wcs.spring_data_jpa_project.dto.LoginRequest;
import com.wcs.spring_data_jpa_project.dto.RegisterRequest;
import com.wcs.spring_data_jpa_project.dto.UserDeptDTO;
import com.wcs.spring_data_jpa_project.dto.UserSummaryDTO;
import com.wcs.spring_data_jpa_project.exception.InvalidCredentialsException;
import com.wcs.spring_data_jpa_project.exception.InvalidInputException;
import com.wcs.spring_data_jpa_project.model.User;
import com.wcs.spring_data_jpa_project.service.core.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")  // allow your Vite app
@Slf4j
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "User Registration", description = "Register a new user and send a welcome email.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request);  // Call the registerUser method from UserService
            ApiResponse<User> response = new ApiResponse<>("User registered successfully", user);
            return ResponseEntity.ok(response);
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @Operation(summary = "User Login", description = "Authenticate user using email and password.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginUser(@RequestBody LoginRequest request) {
        try {
            String token = userService.loginUser(request);  // Call the loginUser method from UserService
            return ResponseEntity.ok(new ApiResponse<>("Login successful", token));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));
        }
    }


    @GetMapping("/search")
    public ResponseEntity<List<User>> dynamicSearch(@RequestParam String keyword) {
        try {
            // Call service to perform dynamic search based on the keyword
            List<User> users = userService.dynamicSearch(keyword);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // You can customize the exception handling as per your project needs
        }
    }
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
        log.info("Creating new user: {}", user.getUserName());
        userService.saveUser(user);
        ApiResponse<User> response = new ApiResponse<>("User created successfully", user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getUser/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userService.getUserById(id);
        if (user != null) {
            ApiResponse<User> response = new ApiResponse<>("User fetched successfully", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            log.warn("User with ID {} not found", id);
            ApiResponse<User> response = new ApiResponse<>("User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        log.info("Updating user with ID: {}", id);
        User existingUser = userService.getUserById(id);
        if (existingUser != null) {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            ApiResponse<User> response = new ApiResponse<>("User updated successfully", updatedUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            log.warn("Cannot update. User with ID {} not found", id);
            ApiResponse<User> response = new ApiResponse<>("User not found. Update failed", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        User user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUser(id);
            ApiResponse<String> response = new ApiResponse<>("User deleted successfully", "Deleted user ID: " + id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            log.warn("User with ID {} not found for deletion", id);
            ApiResponse<String> response = new ApiResponse<>("User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/assignUserToDepartment")
    public ResponseEntity<ApiResponse<User>> assignUserToDepartment(@RequestParam Long userId, @RequestParam Long departmentId) {
        log.info("Assigning user {} to department {}", userId, departmentId);
        User updatedUser = userService.assignUserToDepartment(userId, departmentId);
        ApiResponse<User> response = new ApiResponse<>("User assigned to department successfully.", updatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        ApiResponse<List<User>> response = new ApiResponse<>("All users fetched", users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByCity(@PathVariable String city) {
        log.info("Fetching users from city: {}", city);
        List<User> users = userService.getUsersByCity(city);
        if (!users.isEmpty()) {
            ApiResponse<List<User>> response = new ApiResponse<>("Users fetched by city", users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            log.warn("No users found in city: {}", city);
            ApiResponse<List<User>> response = new ApiResponse<>("No users found in city: " + city, null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/countByEmailDomain/{domain}")
    public ResponseEntity<ApiResponse<Long>> countUsersByEmailDomain(@PathVariable String domain) {
        log.info("Counting users with email domain: {}", domain);
        Long count = userService.countUsersByEmailDomain(domain);
        ApiResponse<Long> response = new ApiResponse<>("Users counted by email domain", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/native")
    public ResponseEntity<ApiResponse<List<User>>> getUsersNative() {
        log.info("Fetching users using native SQL");
        List<User> users = userService.getUsersNative();
        ApiResponse<List<User>> response = new ApiResponse<>("Users fetched using native SQL", users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getUserByCity/{city}")
    public ResponseEntity<ApiResponse<List<User>>> getUserByCitySorted(@PathVariable String city) {
        log.info("Fetching sorted users from city: {}", city);
        List<User> listUser = userService.getUsersByCitySorted(city);
        if (!listUser.isEmpty()) {
            ApiResponse<List<User>> response = new ApiResponse<>("The list of the user :" + city, listUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<List<User>> response = new ApiResponse<>("There is no any user from :" + city, listUser);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/updateEmailByCity")
    public ResponseEntity<String> updateEmailsByCity(@RequestParam String city, @RequestParam String newEmail) {
        log.info("Updating emails to {} for users in city: {}", newEmail, city);
        int updatedCount = userService.updateEmailsByCity(city, newEmail);
        if (updatedCount > 0) {
            return ResponseEntity.ok(updatedCount + " users' emails updated successfully.");
        } else {
            log.warn("No users found in city: {}", city);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found in city: " + city);
        }
    }

    @DeleteMapping("/deleteByCity")
    public ResponseEntity<String> deleteUsersByCity(@RequestParam String city) {
        log.info("Deleting users from city: {}", city);
        int deletedCount = userService.deleteUsersByCity(city);
        if (deletedCount > 0) {
            return ResponseEntity.ok(deletedCount + " users deleted from city: " + city);
        } else {
            log.warn("No users found in city: {} for deletion", city);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found in city: " + city);
        }
    }

    @GetMapping("/filterByCityAndContact")
    public ResponseEntity<ApiResponse<List<User>>> getFilteredUsers(@RequestParam String city, @RequestParam String contact) {
        log.info("Filtering users by city: {} and contact: {}", city, contact);
        List<User> users = userService.getFilteredUsers(city, contact);
        if (!users.isEmpty()) {
            ApiResponse<List<User>> response = new ApiResponse<>("User list to given filter condition " + city, users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<List<User>> response = new ApiResponse<>("Using this filter condition there is not any user found", users);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getUserSortedByNameAsc")
    public ResponseEntity<ApiResponse<List<User>>> getUsersSortedByNameAsc() {
        log.info("Fetching users sorted by name ascending");
        List<User> users = userService.getUsersSortedByNameAsc();
        if (!users.isEmpty()) {
            ApiResponse<List<User>> response = new ApiResponse<>("The list of the user", users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<List<User>> response = new ApiResponse<>("There is not any user", users);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getUsersSortedByEmailDesc")
    public ResponseEntity<ApiResponse<List<User>>> getUsersSortedByEmailDesc() {
        log.info("Fetching users sorted by email descending");
        List<User> users = userService.getUsersSortedByEmailDesc();
        if (!users.isEmpty()) {
            ApiResponse<List<User>> response = new ApiResponse<>("List of the users", users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<List<User>> response = new ApiResponse<>("There is not any users", users);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<List<User>> getSortedPaginatedUsers(
            @RequestParam int page,
            @RequestParam int size) {
        log.info("Fetching paginated users - page: {}, size: {}", page, size);
        List<User> users = userService.getUsersPaginated(page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/with-department")
    public ResponseEntity<List<User>> getUsersWithDepartments() {
        log.info("Fetching users with departments");
        return new ResponseEntity<>(userService.getUsersWithDepartments(), HttpStatus.OK);
    }

    @GetMapping("/by-department/{name}")
    public ResponseEntity<List<User>> getUsersByDepartmentName(@PathVariable String name) {
        log.info("Fetching users from department: {}", name);
        return new ResponseEntity<>(userService.getUsersByDepartmentName(name), HttpStatus.OK);
    }

    @GetMapping("/sorted-by-department")
    public ResponseEntity<List<User>> getUsersSortedByDepartmentName() {
        log.info("Fetching users sorted by department name");
        return new ResponseEntity<>(userService.getUsersSortedByDepartmentName(), HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<UserSummaryDTO>> getUserSummary() {
        log.info("Fetching user summary");
        List<UserSummaryDTO> summary = userService.getUserSummary();
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

    @GetMapping("/user-summary/city/{city}")
    public ResponseEntity<List<UserSummaryDTO>> getUserSummaryByCity(@PathVariable String city) {
        log.info("Fetching user summary for city: {}", city);
        List<UserSummaryDTO> summaryList = userService.getUserSummaryByCity(city);
        return new ResponseEntity<>(summaryList, HttpStatus.OK);
    }

    @GetMapping("/user-with-department")
    public ResponseEntity<List<UserDeptDTO>> getUsersWithDepartment() {
        log.info("Fetching user details with department (DTO projection)");
        List<UserDeptDTO> dtoList = userService.getUserDepartmentDetails();
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }
}
