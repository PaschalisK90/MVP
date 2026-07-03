package org.example.mvp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CertificateDTO {

    @NotBlank(message = "Address to field is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\s,.-]+$", message = "Address to must be alphanumeric")
    private String address_to;

    @NotBlank(message = "Purpose field is required")
    @Size(min = 50, message = "Purpose must be a minimum of 50 characters")
    private String purpose;

    @Future(message = "Issued on date must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d/M/yyyy")
    private LocalDate issued_on;

    @NotBlank(message = "Employee ID is required")
    @Pattern(regexp = "^[0-9]+$", message = "Employee ID must contain numeric digits only")
    private String employee_id;
}
