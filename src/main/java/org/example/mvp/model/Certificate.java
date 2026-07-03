package org.example.mvp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.Pattern;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "certificate_requests")
public class Certificate {

    public enum RequestStatus {
        PENDING, PROCESSED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address_to", nullable = false)
    private String addressTo;

    @Column(name = "purpose", nullable = false, columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "issued_on", nullable = false)
    private LocalDate issuedOn;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

}
