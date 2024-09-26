package com.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.dto.PageRequestDto;
import com.demo.dto.PageResponseDto;
import com.demo.entity.Employee;
import com.demo.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@PostMapping
	public ResponseEntity<PageResponseDto<Employee>> getAllEmployees(@RequestBody PageRequestDto pageRequestDto) {
		PageResponseDto<Employee> employees = employeeService.getEmployees(pageRequestDto);
		return ResponseEntity.ok(employees);
	}
}
