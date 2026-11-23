package org.example.mvp.repository;

import org.example.mvp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    @Query("SELECT COALESCE(MAX(e.employeeNumber), 0) FROM Employee e")
    Optional<Long> findMaxEmployeeNumber();
}

