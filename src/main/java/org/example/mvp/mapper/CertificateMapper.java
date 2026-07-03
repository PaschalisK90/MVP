package org.example.mvp.mapper;

import org.example.mvp.dto.CertificateDTO;
import org.example.mvp.model.Certificate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CertificateMapper {

    CertificateMapper INSTANCE = Mappers.getMapper(CertificateMapper.class);

    // Entity to DTO
    CertificateDTO toDto(Certificate entity);

    // DTO to Entity
    Certificate toEntity(CertificateDTO dto);
}
