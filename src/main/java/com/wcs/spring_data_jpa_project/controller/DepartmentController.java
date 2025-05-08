package com.wcs.spring_data_jpa_project.controller;

import com.wcs.spring_data_jpa_project.customeResponse.ApiResponse;
import com.wcs.spring_data_jpa_project.model.Department;
import com.wcs.spring_data_jpa_project.service.core.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> saveDepartment(@RequestBody Department department) {
        departmentService.saveDepartment(department);
        ApiResponse<String> response = new ApiResponse<>("Department saved successfully", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        ApiResponse<Department> response = new ApiResponse<>("Department fetched successfully", department);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Department>> updateDepartment(@RequestBody Department department) {
        Department updated = departmentService.updateDepartment(department);
        ApiResponse<Department> response = new ApiResponse<>("Department updated successfully", updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        ApiResponse<String> response = new ApiResponse<>("Department deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Department>>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        ApiResponse<List<Department>> response = new ApiResponse<>("Departments fetched successfully", departments);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<Department>> getDepartmentByName(@PathVariable String name) {
        Department department = departmentService.getDepartmentByName(name);
        ApiResponse<Department> response = new ApiResponse<>("Department fetched successfully", department);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/native")
    public ResponseEntity<ApiResponse<List<Department>>> getDepartmentsNative() {
        List<Department> departments = departmentService.getDepartmentsNative();
        ApiResponse<List<Department>> response = new ApiResponse<>("Departments fetched successfully", departments);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countByName(@RequestParam String name) {
        Long count = departmentService.countByName(name);
        ApiResponse<Long> response = new ApiResponse<>("Department count fetched successfully", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<Department>>> getDepartmentsByNameCriteria(@RequestParam String name) {
        List<Department> departments = departmentService.getDepartmentsByNameCriteria(name);
        ApiResponse<List<Department>> response = new ApiResponse<>("Departments filtered successfully", departments);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sort")
    public ResponseEntity<ApiResponse<List<Department>>> getDepartmentsSortedByNameAsc() {
        List<Department> departments = departmentService.getDepartmentsSortedByNameAsc();
        ApiResponse<List<Department>> response = new ApiResponse<>("Departments sorted by name", departments);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pagination")
    public ResponseEntity<ApiResponse<List<Department>>> getDepartmentsPaginated(
            @RequestParam int pageNo,
            @RequestParam int pageSize) {
        List<Department> departments = departmentService.getDepartmentsPaginated(pageNo, pageSize);
        ApiResponse<List<Department>> response = new ApiResponse<>("Departments fetched successfully", departments);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-name")
    public ResponseEntity<ApiResponse<String>> updateDepartmentName(
            @RequestParam Long id,
            @RequestParam String newName) {
        int updated = departmentService.updateDepartmentName(id, newName);
        ApiResponse<String> response = new ApiResponse<>("Updated " + updated + " record(s)", null);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-by-name")
    public ResponseEntity<ApiResponse<String>> deleteDepartmentByName(@RequestParam String name) {
        int deleted = departmentService.deleteDepartmentByName(name);
        ApiResponse<String> response = new ApiResponse<>("Deleted " + deleted + " record(s)", null);
        return ResponseEntity.ok(response);
    }
}
