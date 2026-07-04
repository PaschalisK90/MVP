package org.example.mvp.controler;

import jakarta.validation.Valid;
import org.example.mvp.dto.CertificateDTO;
import org.example.mvp.model.Certificate;
import org.example.mvp.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {
    @Autowired
    CertificateService certificateService;
    @GetMapping("/{referenceNo}")
    public ResponseEntity<CertificateDTO> getCertificateDaten(@PathVariable Long referenceNo){
        CertificateDTO certificateDTO = certificateService.findById(referenceNo);
        return ResponseEntity.ok(certificateDTO);
    }

    @PatchMapping(value = "certificateUpdate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTO> updateCertificateDaten(@RequestBody final CertificateDTO certificateDTO) {
        return ResponseEntity.ok(certificateService.update(certificateDTO));
    }


    @PostMapping(value = "certificateSave", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTO> saveCertificateDaten(@Valid @RequestBody final CertificateDTO certificateDTO){
        return ResponseEntity.ok(certificateService.createRequest(certificateDTO));
    }

    @GetMapping(value = "getFiltered", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CertificateDTO>> getRequests(
            @RequestParam String employeeId,
            @RequestParam(required = false) Long referenceNo, // 1. Changed Long to String to match UUID id field type
            @RequestParam(required = false) String addressTo,
            @RequestParam(required = false) Certificate.RequestStatus status,
            @PageableDefault(size = 10) Pageable pageable) {

        for (Sort.Order order : pageable.getSort()) {
            String sortProperty = order.getProperty();
            if (!"issuedOn".equals(sortProperty) && !"status".equals(sortProperty)) {
                throw new IllegalArgumentException("Sorting by '" + sortProperty + "' is not permitted. Only 'issuedOn' and 'status' can be sorted.");
            }
        }
        return ResponseEntity.ok(certificateService.getAllRequests(employeeId, referenceNo, addressTo, status, pageable));
    }
}
