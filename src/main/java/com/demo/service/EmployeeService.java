package com.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dto.PageRequestDto;
import com.demo.dto.PageResponseDto;
import com.demo.entity.Employee;
import com.demo.util.QueryUtil;

import jakarta.persistence.EntityManager;

@Service
public class EmployeeService {
	
	private final QueryUtil<Employee> queryUtil;
	
	@Autowired
    public EmployeeService(EntityManager entityManager) {
        this.queryUtil = new QueryUtil<>(entityManager);
    }

	public PageResponseDto<Employee> getEmployees(PageRequestDto pageRequestDto){
		return queryUtil.getPaginatedData(pageRequestDto, Employee.class);
	}
}
