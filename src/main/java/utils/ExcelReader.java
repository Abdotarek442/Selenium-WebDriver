package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Generic Excel reader using Apache POI.
 * Each sheet inside TestData.xlsx is treated as a separate data set.
 * Row 0 = headers, the rest = data rows.
 */
public class ExcelReader {

    /**
     * Returns the data rows of a sheet as Object[][] suitable for a TestNG
     * @DataProvider. Header row is skipped.
     */
    public static Object[][] getSheetData(String sheetName) {
        String path = ConfigReader.get("excel.path");

        try (FileInputStream fis = new FileInputStream(path);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException(
                        "Sheet '" + sheetName + "' not found in " + path);
            }

            int lastRow = sheet.getLastRowNum(); // 0-based
            // Determine column count from header row
            Row header = sheet.getRow(0);
            if (header == null) {
                return new Object[0][0];
            }
            int cols = header.getLastCellNum();

            // Count non-empty data rows
            int dataRowCount = 0;
            for (int r = 1; r <= lastRow; r++) {
                Row row = sheet.getRow(r);
                if (row != null && !isRowEmpty(row, cols)) {
                    dataRowCount++;
                }
            }

            Object[][] data = new Object[dataRowCount][cols];
            DataFormatter formatter = new DataFormatter();

            int idx = 0;
            for (int r = 1; r <= lastRow; r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowEmpty(row, cols)) {
                    continue;
                }
                for (int c = 0; c < cols; c++) {
                    Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    data[idx][c] = formatter.formatCellValue(cell);
                }
                idx++;
            }
            return data;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file: " + path, e);
        }
    }

    private static boolean isRowEmpty(Row row, int cols) {
        DataFormatter formatter = new DataFormatter();
        for (int c = 0; c < cols; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && !formatter.formatCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
