package org.example.mvp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id", nullable = false)
    private Long  employeeId;

    @Column(name = "employee_number", nullable = false)
    private Integer  employeeNumber;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "address_to", nullable = false)
    private String addressTo;

    @Column(name = "position", nullable = false)
    private String position;

    @OneToMany(mappedBy = "employee")
    private List<Certificate> certificates;
}
