package org.example.mvp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeDTO {

    @JsonProperty("employee_id") // Sent out to the frontend, ignored on incoming POST requests
    private Long employeeId;

    @JsonProperty("employee_number")
    private Integer  employeeNumber;

    @JsonProperty("address_to")
    private String addressTo;

    @JsonProperty("position")
    private String position;

    @JsonProperty("employee_name")
    @NotBlank(message = "Employee Name field is required")
    String employeeName;

}
