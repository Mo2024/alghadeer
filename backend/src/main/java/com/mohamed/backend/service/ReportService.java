package com.mohamed.backend.service;

import com.mohamed.backend.dto.semester.StudentExportDto;
import com.mohamed.backend.repository.semester.SemesterRepository;
import com.mohamed.backend.utils.Logger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final SemesterRepository semesterRepository;
    private final Logger logger;

    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN','SUPERVISOR')")
    @Transactional
    public byte[] getEnrolledStudentsTelephoneExcel() throws Exception {

        log.info("Calling [semesterRepository].[getEnrolledStudentsTelephone]");
        List<StudentExportDto> students = semesterRepository.getEnrolledStudentsTelephone();
        log.info("[semesterRepository].[getEnrolledStudentsTelephone] called successfully");

        logger.logJsonObject("Enrolled students numbers:\n", students);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Students");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Student Name");
            header.createCell(1).setCellValue("CPR");
            header.createCell(2).setCellValue("Telephone 1");
            header.createCell(3).setCellValue("Telephone 2");
            header.createCell(4).setCellValue("Class");
            header.createCell(5).setCellValue("enrollmentDate");

            CreationHelper createHelper = workbook.getCreationHelper();
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("yyyy-MM-dd")
            );

            int rowIdx = 1;
            for (StudentExportDto s : students) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getName());
                row.createCell(1).setCellValue(s.getCpr());
                row.createCell(2).setCellValue(s.getTelephone1());
                row.createCell(3).setCellValue(s.getTelephone2());
                row.createCell(4).setCellValue(s.getClassName());
                Cell dateCell = row.createCell(5);
                LocalDateTime ldt = s.getEnrollmentDate();
                if (ldt != null) {
                    ZoneId bahrainZone = ZoneId.of("Asia/Bahrain");
                    Date date = Date.from(ldt.atZone(bahrainZone).toInstant());
                    dateCell.setCellValue(date);
                    dateCell.setCellStyle(dateCellStyle);
                }

            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
