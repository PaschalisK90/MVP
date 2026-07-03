package org.example.mvp.controler;

import org.example.mvp.dto.CertificateDTO;
import org.example.mvp.model.Certificate;
import org.example.mvp.service.CertificateService;
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
    CertificateService certificateService;
    @GetMapping("/{uuid}")
    public ResponseEntity<CertificateDTO> getCertificateDaten(@PathVariable String uuid){
        CertificateDTO certificateDTO = certificateService.findById(uuid);
        return ResponseEntity.ok(certificateDTO);
    }

    @PutMapping(value = "certificateUpdate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTO> updateCertificateDaten(@RequestBody final CertificateDTO certificateDTO){
        return ResponseEntity.ok(certificateService.update(certificateDTO));
    }


    @PostMapping(value = "certificateSave", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTO> saveCertificateDaten(@RequestBody final CertificateDTO certificateDTO){
        return ResponseEntity.ok(certificateService.createRequest(certificateDTO));
    }

    @GetMapping
    public ResponseEntity<Page<Certificate>> getRequests(
            @RequestParam String employeeId,
            @RequestParam(required = false) Long referenceNo,
            @RequestParam(required = false) String addressTo,
            @RequestParam(required = false) Certificate.RequestStatus status,
            @PageableDefault(size = 10, sort = "issuedOn", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(certificateService.getAllRequests(employeeId, referenceNo, addressTo, status, pageable));
    }
}
