package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;
import javafx.collections.ObservableList;
import models.Items;
import models.Transactions;

public class ExcelExporter {

    /**
     * Export items to Excel with optional logo
     *
     * @param items ObservableList of Items
     * @param filePath path to save the Excel file
     * @param logoPath path to the logo image file (PNG/JPG)
     */
    public static void exportItems(ObservableList<Items> items, String filePath, String logoPath) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Items Report");

            // ===== Styles =====
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);

            Font subtitleFont = workbook.createFont();
            subtitleFont.setBold(true);
            subtitleFont.setFontHeightInPoints((short) 12);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle subtitleStyle = workbook.createCellStyle();
            subtitleStyle.setFont(subtitleFont);
            subtitleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            int rowNum = 0;
            // ===== Report Header =====
            String schoolName = "Ramon Magsaysay Memorial Colleges - Integrated School";
            String schoolAddress = "Beatiles St, Brgy. Dadiangas, General Santos City";
            String systemName = "Clinic Inventory Management System";
            String reportTitle = "Item Inventory Report";
            String reportDate = "Date: " + java.time.LocalDate.now();

            // School Name
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(schoolName);
            cell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7));

            // School Address
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(schoolAddress);
            cell.setCellStyle(subtitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7));

            // System Name
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(systemName);
            cell.setCellStyle(subtitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7));

            // Report Title
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(subtitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7));

            // Report Date
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(reportDate);
            cell.setCellStyle(subtitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7));

            rowNum++; // empty row for spacing

            // ===== Header Row =====
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Item ID", "Item Name", "Category", "Stock", "Unit", "Expiry Date", "Supplier", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(headers[i]);
                headerCell.setCellStyle(headerStyle);
            }

            // ===== Data Rows =====
            for (Items item : items) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(item.getItemID());
                dataRow.createCell(1).setCellValue(item.getItemName());
                dataRow.createCell(2).setCellValue(item.getCategory());
                dataRow.createCell(3).setCellValue(item.getStock());
                dataRow.createCell(4).setCellValue(item.getUnit());
                dataRow.createCell(5).setCellValue(item.getExpiryDate());
                dataRow.createCell(6).setCellValue(item.getSupplier());
                dataRow.createCell(7).setCellValue(item.getStatus());

                for (int i = 0; i < 8; i++) {
                    dataRow.getCell(i).setCellStyle(dataStyle);
                }
            }

            // ===== Autosize Columns =====
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ===== Save to File =====
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("Excel exported successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Optional: keep old 2-arg call for backward compatibility
    public static void exportItems(ObservableList<Items> items, String filePath) {
        exportItems(items, filePath, null);
    }

    public static void exportTransactions(
            ObservableList<Transactions> transactions,
            String filePath
    ) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Transactions Report");

            // ===== Styles =====
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);

            Font subtitleFont = workbook.createFont();
            subtitleFont.setBold(true);
            subtitleFont.setFontHeightInPoints((short) 12);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle subtitleStyle = workbook.createCellStyle();
            subtitleStyle.setFont(subtitleFont);
            subtitleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            int rowNum = 0;

            // ===== Report Header =====
            String schoolName = "Ramon Magsaysay Memorial Colleges - Integrated School";
            String schoolAddress = "Beatiles St, Brgy. Dadiangas, General Santos City";
            String systemName = "Clinic Inventory Management System";
            String reportTitle = "Transaction Records Report";
            String reportDate = "Date: " + java.time.LocalDate.now();

            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(schoolName);
            cell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(schoolAddress);
            cell.setCellStyle(subtitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(systemName);
            cell.setCellStyle(subtitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 8));

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(reportTitle);
            cell.setCellStyle(subtitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 8));

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(reportDate);
            cell.setCellStyle(subtitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 8));

            rowNum++; // spacing

            // ===== Table Header =====
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {
                "Transaction ID", "Date", "Item Name", "Type",
                "Quantity", "Performed By", "Requester Name",
                "Requester ID", "Remarks"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(headers[i]);
                headerCell.setCellStyle(headerStyle);
            }

            // ===== Data Rows =====
            for (Transactions t : transactions) {
                Row dataRow = sheet.createRow(rowNum++);

                dataRow.createCell(0).setCellValue(t.getTransactionId());
                dataRow.createCell(1).setCellValue(t.getDate());
                dataRow.createCell(2).setCellValue(t.getItemName());
                dataRow.createCell(3).setCellValue(t.getType());
                dataRow.createCell(4).setCellValue(t.getQuantity());
                dataRow.createCell(5).setCellValue(t.getPerformedBy());
                dataRow.createCell(6).setCellValue(t.getRequesterName());
                dataRow.createCell(7).setCellValue(t.getRequesterId());
                dataRow.createCell(8).setCellValue(t.getRemarks());

                for (int i = 0; i < headers.length; i++) {
                    dataRow.getCell(i).setCellStyle(dataStyle);
                }
            }

            // ===== Autosize =====
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ===== Save =====
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("Transactions Excel exported successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
