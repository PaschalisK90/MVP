package org.example.mvp.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.mvp.model.Employee;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;

@Component
@Slf4j
@Order(1)
@ConditionalOnProperty(
        name = "data.employee.migration",
        havingValue = "true"
)
public class EmployeeImportService {

    private final EmployeeService employeeService;

    @Value("${data.employee.migration}")
    private boolean migrationEmployee;

    @Value("${data.employee.file}")
    private String fileEmployee;
    public EmployeeImportService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onImportEvent(ApplicationReadyEvent event) {
        if (migrationEmployee) {
            log.info("Starting employee data migration...");
            try (FileInputStream fileInputStream = new FileInputStream(fileEmployee);
                 XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {
                Sheet sheet = workbook.getSheetAt(0);
                this.processRow(sheet);
            } catch (Exception e) {
                log.error("Error during employee data migration: ", e);
            }
            // Call the method to perform the migration
            log.info("Employee data migration completed.");
        } else {
            log.info("Employee data migration is disabled.");
        }
    }

    private void processRow(Sheet sheet){
        int rowCounter = 0;
        for(Row row: sheet){
            if(rowCounter == 0){
                rowCounter++;
                continue; // Skip header row
            }

            this.updateEmployee(row);
            // Process the employee data as needed
            log.info("Processing Employee: {}", row.getCell(1).getStringCellValue());
        }
    }

    private void updateEmployee(Row row) {
        Employee employee = new Employee();

        var numberCell = row.getCell(0);
        if (numberCell != null && numberCell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
            employee.setEmployeeNumber((int) numberCell.getNumericCellValue());
        } else if (numberCell != null && numberCell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING
                && !numberCell.getStringCellValue().isBlank()) {
            employee.setEmployeeNumber(Integer.parseInt(numberCell.getStringCellValue().trim()));
        } else {
            employee.setEmployeeNumber(employeeService.increaseEmployNumeberToOne());
        }

        employee.setEmployeeName(row.getCell(1).getStringCellValue());
        employee.setAddressTo(row.getCell(2).getStringCellValue());
        employee.setPosition(row.getCell(3).getStringCellValue());

        employeeService.migrateEmployeeData(employee);
    }
}
