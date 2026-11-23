package org.example.mvp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mvp.model.Employee;
import org.example.mvp.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public void migrateEmployeeData(Employee employee) {
        employeeRepository.save(employee);
    }

    public Integer increaseEmployNumeberToOne() {
        return Math.toIntExact(employeeRepository.findMaxEmployeeNumber().orElse(0L) + 1);
    }
}
