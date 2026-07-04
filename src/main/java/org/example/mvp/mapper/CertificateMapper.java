package org.example.mvp.mapper;

import org.example.mvp.dto.CertificateDTO;
import org.example.mvp.model.Certificate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CertificateMapper {


    @Mapping(source = "referenceNo", target = "referenceNo")
    @Mapping(source = "addressTo", target = "address_to")
    @Mapping(source = "issuedOn", target = "issued_on", dateFormat = "d/M/yyyy") // <-- Converts LocalDate to String automatically
    @Mapping(source = "employeeId", target = "employee_id")
    CertificateDTO toDto(Certificate entity);

    @Mapping(source = "referenceNo", target = "referenceNo")
    @Mapping(source = "address_to", target = "addressTo")
    @Mapping(source = "issued_on", target = "issuedOn", dateFormat = "d/M/yyyy") // <-- Converts String to LocalDate automatically
    @Mapping(source = "employee_id", target = "employeeId")
    Certificate toEntity(CertificateDTO dto);
}
