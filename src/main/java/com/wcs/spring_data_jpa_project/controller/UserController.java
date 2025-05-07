package com.wcs.spring_data_jpa_project.controller;

import com.wcs.spring_data_jpa_project.customeResponse.ApiResponse;
import com.wcs.spring_data_jpa_project.dto.UserDeptDTO;
import com.wcs.spring_data_jpa_project.dto.UserSummaryDTO;
import com.wcs.spring_data_jpa_project.model.User;
import com.wcs.spring_data_jpa_project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
        userService.saveUser(user);
        ApiResponse<User> response = new ApiResponse<>("User created successfully", user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            ApiResponse<User> response = new ApiResponse<>("User fetched successfully", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<User> response = new ApiResponse<>("User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User exitingUser = userService.getUserById(id);
        if (exitingUser != null) {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            ApiResponse<User> response = new ApiResponse<>("User updated successfully", updatedUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<User> response = new ApiResponse<>("User not found. Update failed", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUser(id);
            ApiResponse<String> response = new ApiResponse<>("User deleted successfully", "Deleted user ID: " + id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<String> response = new ApiResponse<>("User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/assignUserToDepartment")
    public ResponseEntity<ApiResponse<User>> assignUserToDepartment(@RequestParam Long userId, @RequestParam Long departmentId) {

        User updatedUser = userService.assignUserToDepartment(userId, departmentId);

        ApiResponse<User> response = new ApiResponse<>();
        response.setMessage("User assigned to department successfully.");
        response.setData(updatedUser);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        ApiResponse<List<User>> response = new ApiResponse<>("All users fetched", users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByCity(@PathVariable String city) {
        List<User> users = userService.getUsersByCity(city);
        if (!users.isEmpty()) {
            ApiResponse<List<User>> response = new ApiResponse<>("Users fetched by city", users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<List<User>> response = new ApiResponse<>("No users found in city: " + city, null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/countByEmailDomain/{domain}")
    public ResponseEntity<ApiResponse<Long>> countUsersByEmailDomain(@PathVariable String domain) {
        Long count = userService.countUsersByEmailDomain(domain);
        ApiResponse<Long> response = new ApiResponse<>("Users counted by email domain", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/native")
    public ResponseEntity<ApiResponse<List<User>>> getUsersNative() {
        List<User> users = userService.getUsersNative();
        ApiResponse<List<User>> response = new ApiResponse<>("Users fetched using native SQL", users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getUserByCity/{city}")
    public ResponseEntity<ApiResponse<List<User>>> getUserByCitySorted(@PathVariable String city) {
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
        int updatedCount = userService.updateEmailsByCity(city, newEmail);
        if (updatedCount > 0) {
            return ResponseEntity.ok(updatedCount + " users' emails updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found in city: " + city);
        }
    }

    @DeleteMapping("/deleteByCity")
    public ResponseEntity<String> deleteUsersByCity(@RequestParam String city) {
        int deletedCount = userService.deleteUsersByCity(city);
        if (deletedCount > 0) {
            return ResponseEntity.ok(deletedCount + " users deleted from city: " + city);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found in city: " + city);
        }
    }

    @GetMapping("/filterByCityAndContact")
    public ResponseEntity<ApiResponse<List<User>>> getFilteredUsers(@RequestParam String city, @RequestParam String contact) {
        List<User> users = userService.getFilteredUsers(city, contact);
        if (!users.isEmpty()) {
            ApiResponse<List<User>> response = new ApiResponse<>("User list to given filter contidion " + city, users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<List<User>> response = new ApiResponse<>("Using this filter condition there is not any user found ", users);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getUserSortedByNameAsc")
    public ResponseEntity<ApiResponse<List<User>>> getUsersSortedByNameAsc() {
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
        List<User> users = userService.getUsersSortedByEmailDesc();
        if (!users.isEmpty()) {
            ApiResponse<List<User>> response = new ApiResponse<>("List of the users", users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<List<User>> response = new ApiResponse<>("There is not any users ", users);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<List<User>> getSortedPaginatedUsers(
            @RequestParam int page,
            @RequestParam int size) {

        List<User> users = userService.getUsersPaginated(page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/with-department")
    public ResponseEntity<List<User>> getUsersWithDepartments() {
        return new ResponseEntity<>(userService.getUsersWithDepartments(), HttpStatus.OK);
    }

    @GetMapping("/by-department/{name}")
    public ResponseEntity<List<User>> getUsersByDepartmentName(@PathVariable String name) {
        return new ResponseEntity<>(userService.getUsersByDepartmentName(name), HttpStatus.OK);
    }

    @GetMapping("/sorted-by-department")
    public ResponseEntity<List<User>> getUsersSortedByDepartmentName() {
        return new ResponseEntity<>(userService.getUsersSortedByDepartmentName(), HttpStatus.OK);
    }

    //  DTO projection
    @GetMapping("/summary")
    public ResponseEntity<List<UserSummaryDTO>> getUserSummary() {
        List<UserSummaryDTO> summary = userService.getUserSummary();
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

// Class based Projection

    @GetMapping("/user-summary/city/{city}")
    public ResponseEntity<List<UserSummaryDTO>> getUserSummaryByCity(@PathVariable String city) {
        List<UserSummaryDTO> summaryList = userService.getUserSummaryByCity(city);
        return new ResponseEntity<>(summaryList, HttpStatus.OK);
    }

    // DTO projection with joins
    @GetMapping("/user-with-department")
    public ResponseEntity<List<UserDeptDTO>> getUsersWithDepartment() {
        List<UserDeptDTO> dtoList = userService.getUserDepartmentDetails();
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }


}
