package org.example.mvp.model;
import jakarta.persistence.*;
import lombok.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <-- Changed to numeric Identity strategy
    @Column(name = "reference_no", nullable = false)
    private Long  referenceNo;

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
    private RequestStatus status;

}
