package org.example.mvp.mapper;

import org.example.mvp.dto.EmployeeDTO;
import org.example.mvp.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "employeeId", target = "employeeId")
    @Mapping(source = "employeeNumber", target = "employeeNumber")
    @Mapping(source = "employeeName", target = "employeeName")
    @Mapping(source = "addressTo", target = "addressTo")
    @Mapping(source = "position", target = "position")
    EmployeeDTO toDto(Employee entity);

    @Mapping(source = "employeeId", target = "employeeId")
    @Mapping(source = "employeeNumber", target = "employeeNumber")
    @Mapping(source = "employeeName", target = "employeeName")
    @Mapping(source = "addressTo", target = "addressTo")
    @Mapping(source = "position", target = "position")
    @Mapping(target = "certificates", ignore = true)
    Employee toEntity(EmployeeDTO dto);
}
