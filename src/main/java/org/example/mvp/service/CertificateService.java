package org.example.mvp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mvp.dto.CertificateDTO;
import org.example.mvp.mapper.CertificateMapper;
import org.example.mvp.model.Certificate;
import org.example.mvp.repository.CertificateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CertificateService {
    private final CertificateMapper mapper;
    private final CertificateRepository certificateRepository;

public CertificateDTO findById(Long uuid){
    try {
        Certificate certificate = certificateRepository.findById(uuid).orElseGet(Certificate::new);
        return mapper.toDto(certificate);
    }catch (Exception e) {
        throw new RuntimeException("Error finding certificate: " + e.getMessage(), e);
    }
}

    public CertificateDTO update(CertificateDTO certificateDTO){
        try {
            Certificate certificate = certificateRepository.findById(certificateDTO.getReferenceNo())
                    .orElseThrow(() -> new RuntimeException("Certificate request not found with reference number: " + certificateDTO.getReferenceNo()));

            // 2. F06-R01 Rule: Strictly block if status is not PENDING
            if (certificate.getStatus() != Certificate.RequestStatus.PENDING) {
                throw new IllegalStateException("Cannot update request. Only PENDING (unprocessed) requests can be modified.");
            }

            // 3. F06-R01 Rule: Partial update -> modify only the purpose field
            certificate.setPurpose(certificateDTO.getPurpose());

            // 4. Save and map back to output DTO
            Certificate updatedCertificate = certificateRepository.save(certificate);
            return mapper.toDto(updatedCertificate);

        } catch (IllegalStateException e) {
            throw e; // Caught cleanly by your GlobalExceptionHandler
        } catch (Exception e) {
            throw new RuntimeException("Error during the update: " + e.getMessage(), e);
        }
    }

    public CertificateDTO createRequest(CertificateDTO certificateDTO){
        try {

            Certificate certificate = mapper.toEntity(certificateDTO);
            return mapper.toDto(certificateRepository.save(certificate));
        } catch (Exception e) {
            throw new RuntimeException("Error creating certificate request: " + e.getMessage(), e);
        }
    }

    public Page<CertificateDTO> getAllRequests(
            String employeeId, Long referenceNo, String addressTo, Certificate.RequestStatus status, Pageable pageable) {

        try {
            // Enforce F04-R01: Must always filter by the current employeeId
            Specification<Certificate> spec = Specification.where((root, query, cb) ->
                    cb.equal(root.get("employeeId"), employeeId));

            if (referenceNo != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("referenceNo"), referenceNo));
            }

            // F04-R03: Case-insensitive 'Contains' match for Address
            if (addressTo != null && !addressTo.trim().isEmpty()) {
                spec = spec.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("addressTo")), "%" + addressTo.toLowerCase() + "%"));
            }

            // F04-R03: Exact match for Status enum
            if (status != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
            }

            // Fetch paginated database entities
            Page<Certificate> entityPage = certificateRepository.findAll(spec, pageable);

            // Map entities to DTOs so the response uses snake_case keys as requested by the business rules
            return entityPage.map(mapper::toDto);

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving certificate list: " + e.getMessage(), e);
        }
    }

}
