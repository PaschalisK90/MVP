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

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CertificateService {
    private final CertificateMapper mapper;
    private final CertificateRepository certificateRepository;

public CertificateDTO findById(String uuid){
    Certificate certificate = certificateRepository.findById(UUID.fromString(uuid)).orElseGet(Certificate::new);
    return mapper.toDto(certificate);
}

    public CertificateDTO update(CertificateDTO certificateDTO){
        Certificate certificate = mapper.toEntity(certificateDTO);
        return mapper.toDto(certificateRepository.save(certificate));
    }

    public CertificateDTO createRequest(CertificateDTO certificateDTO){
        certificateRepository.save(mapper.toEntity(certificateDTO));

        return certificateDTO;
    }

    public Page<Certificate> getAllRequests(
            String employeeId, Long referenceNo, String addressTo, Certificate.RequestStatus status, Pageable pageable) {

        Specification<Certificate> spec = Specification.where((root, query, cb) ->
                cb.equal(root.get("employeeId"), employeeId));

        if (referenceNo != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("id"), referenceNo));
        }
        if (addressTo != null && !addressTo.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("addressTo")), "%" + addressTo.toLowerCase() + "%"));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        return certificateRepository.findAll(spec, pageable);
    }

}
