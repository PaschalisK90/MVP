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
import org.springframework.web.client.RestTemplate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    public CertificateDTO update(CertificateDTO certificateDTO) {
        try {
            Certificate certificate = certificateRepository.findById(certificateDTO.getReferenceNo())
                    .orElseThrow(() -> new RuntimeException("Certificate not found"));

            if (certificate.getStatus() != Certificate.RequestStatus.PENDING) {
                throw new IllegalStateException("Only PENDING can be updated");
            }
            certificateDTO = mapper.toDto(certificate);
            RestTemplate restTemplate = new RestTemplate();
             certificateDTO = restTemplate.postForObject(
                    "http://localhost:8081/api/approval/approve",
                    certificateDTO,
                    CertificateDTO.class
            );

            assert certificateDTO != null;
            if (!certificateDTO.getStatus().equals(Certificate.RequestStatus.PROCESSED.toString())) {
                throw new IllegalStateException("Approval rejected by external service");
            }

            this.createPDFCertificate(certificateDTO, "certificate_" + certificateDTO.getReferenceNo() + ".pdf");

            certificate = mapper.toEntity(certificateDTO);
            certificate.setPurpose(certificateDTO.getPurpose());
            Certificate updated = certificateRepository.save(certificate);
            return mapper.toDto(updated);

        } catch (Exception e) {
            throw new RuntimeException("Error during update: " + e.getMessage(), e);
        }
    }

    public CertificateDTO createRequest(CertificateDTO certificateDTO){
        try {
            RestTemplate restTemplate = new RestTemplate();
            certificateDTO = restTemplate.postForObject(
                    "http://localhost:8081/api/approval/approve",
                    certificateDTO,
                    CertificateDTO.class
            );
            assert certificateDTO != null;
            this.createPDFCertificate(certificateDTO, "certificate_" + certificateDTO.getReferenceNo() + ".pdf");
            Certificate certificate = mapper.toEntity(certificateDTO);
            return mapper.toDto(certificateRepository.save(certificate));
        } catch (Exception e) {
            throw new RuntimeException("Error creating certificate request: " + e.getMessage(), e);
        }
    }




    public void createPDFCertificate(CertificateDTO certificateDTO, String outputPath) throws java.io.IOException {
        try (PDDocument document = new PDDocument()) {
            // Create a landscape A4 page
            PDRectangle landscapeA4 = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
            PDPage page = new PDPage(landscapeA4);
            document.addPage(page);

            float pageWidth = landscapeA4.getWidth();
            float pageHeight = landscapeA4.getHeight();

            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                // 1. Draw outer decorative border
                stream.setStrokingColor(new Color(41, 128, 185));
                stream.setLineWidth(4f);
                stream.addRect(30, 30, pageWidth - 60, pageHeight - 60);
                stream.stroke();

                // 2. Write text line by line
                stream.beginText();

                // Set starting X, Y coordinates (PDF coordinates start from bottom-left)
                float startX = 80;
                float currentY = pageHeight - 110;
                stream.newLineAtOffset(startX, currentY);

                // Line 1: Header Title
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 32);
                stream.setNonStrokingColor(new Color(44, 62, 80));
                stream.showText("CERTIFICATE OF COMPLETION");

                // Line 2: Subtitle
                stream.newLineAtOffset(0, -50); // Move down 50 units
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 16);
                stream.setNonStrokingColor(Color.DARK_GRAY);
                stream.showText("This is to certify that");

                // Line 3: Recipient Name (from Object)
                stream.newLineAtOffset(0, -45);
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD_ITALIC), 36);
                stream.setNonStrokingColor(new Color(41, 128, 185));
                stream.showText(certificateDTO.getEmployee_id());

                // Line 4: Course Description
                stream.newLineAtOffset(0, -45);
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 16);
                stream.setNonStrokingColor(Color.DARK_GRAY);
                stream.showText("has successfully completed the course:");

                // Line 5: Purpose, wrapped to the available page width
                stream.newLineAtOffset(0, -35);
                PDType1Font purposeFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                float purposeFontSize = 22;
                stream.setFont(purposeFont, purposeFontSize);
                stream.setNonStrokingColor(Color.BLACK);

                List<String> purposeLines = wrapText(
                        certificateDTO.getPurpose(), purposeFont, purposeFontSize, pageWidth - 160);
                for (int i = 0; i < purposeLines.size(); i++) {
                    stream.showText(purposeLines.get(i));
                    if (i < purposeLines.size() - 1) {
                        stream.newLineAtOffset(0, -28);
                    }
                }

                // Line 6: Completion Date & Issuer (from Object)
                stream.newLineAtOffset(0, -70);
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
                stream.showText("Date: " + certificateDTO.getIssued_on() + "    |    Issued by: " + certificateDTO.getAddress_to());

                // Line 7: Certificate ID (from Object)
                stream.newLineAtOffset(0, -25);
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), 11);
                stream.setNonStrokingColor(Color.GRAY);
                stream.showText("Certificate ID: " + certificateDTO.getReferenceNo());

                stream.endText();

                // 3. Optional: Draw a signature line
                stream.setStrokingColor(Color.DARK_GRAY);
                stream.setLineWidth(1f);
                stream.moveTo(pageWidth - 280, 120);
                stream.lineTo(pageWidth - 80, 120);
                stream.stroke();
            }

            document.save(outputPath);
        }
    }

    private List<String> wrapText(String text, PDType1Font font, float fontSize, float maxWidth)
            throws java.io.IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            lines.add("");
            return lines;
        }

        StringBuilder line = new StringBuilder();
        for (String word : text.trim().split("\\s+")) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (font.getStringWidth(candidate) / 1000 * fontSize <= maxWidth) {
                line = new StringBuilder(candidate);
                continue;
            }

            if (!line.isEmpty()) {
                lines.add(line.toString());
                line = new StringBuilder();
            }

            if (font.getStringWidth(word) / 1000 * fontSize <= maxWidth) {
                line = new StringBuilder(word);
            } else {
                StringBuilder part = new StringBuilder();
                for (char character : word.toCharArray()) {
                    String candidatePart = part + String.valueOf(character);
                    if (font.getStringWidth(candidatePart) / 1000 * fontSize > maxWidth
                            && !part.isEmpty()) {
                        lines.add(part.toString());
                        part = new StringBuilder().append(character);
                    } else {
                        part = new StringBuilder(candidatePart);
                    }
                }
                line = part;
            }
        }

        if (!line.isEmpty()) {
            lines.add(line.toString());
        }
        return lines;
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
