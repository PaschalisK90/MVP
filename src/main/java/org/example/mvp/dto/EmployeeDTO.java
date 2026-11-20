package org.example.mvp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @JsonProperty("employee_name")
    @NotBlank(message = "Employee Name field is required")
    String employeeName;

}
