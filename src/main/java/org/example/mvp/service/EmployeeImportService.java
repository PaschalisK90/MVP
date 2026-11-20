package org.example.mvp.service;

import com.grapecity.documents.excel.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EmployeeImportService {

    public EmployeeImportService() {
        Workbook workbook = new Workbook();
        workbook.open("EventBudget.xlsx");
    }
}
