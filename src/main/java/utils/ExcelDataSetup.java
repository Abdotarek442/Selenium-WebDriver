package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * One-time utility: adds/updates all data sheets in TestData.xlsx.
 * Run with: mvn compile exec:java -Dexec.mainClass="utils.ExcelDataSetup" -Dexec.classpathScope="compile"
 */
public class ExcelDataSetup {

    public static void main(String[] args) throws Exception {
        String path = "testdata/TestData.xlsx";

        XSSFWorkbook workbook;
        try (FileInputStream fis = new FileInputStream(path)) {
            workbook = new XSSFWorkbook(fis);
        }

        // TC05–TC10 data sheets
        addSheet(workbook, "CurrencyChange",
                new String[]{"category", "fromSymbol", "toCurrencyCode", "toSymbol"},
                new String[][]{{"Desktops", "$", "EUR", "€"}});

        addSheet(workbook, "Navigation",
                new String[]{"category", "expectedBreadcrumb", "expectedSidebar"},
                new String[][]{{"Tablets", "Tablets", "Tablets"}});

        addSheet(workbook, "SortByName",
                new String[]{"category"},
                new String[][]{{"Phones & PDAs"}});

        addSheet(workbook, "SearchByName",
                new String[]{"keyword"},
                new String[][]{{"Mac"}});

        addSheet(workbook, "SearchSubcategories",
                new String[]{"keyword", "category", "expectedProduct"},
                new String[][]{{"Apple", "Components", "Apple Cinema 30"}});

        addSheet(workbook, "CartItems",
                new String[]{"tabletProduct", "laptopProduct", "deliveryDate"},
                new String[][]{{"Samsung Galaxy Tab 10.1", "HP LP3065", "2026-12-31"}});

        // TC11 Checkout sheet – add productName/productCategory/deliveryDate columns
        // and a second row for HP LP3065
        updateCheckoutSheet(workbook);

        try (FileOutputStream fos = new FileOutputStream(path)) {
            workbook.write(fos);
        }
        workbook.close();
        System.out.println("Done — TestData.xlsx updated.");
    }

    /** Adds productName, productCategory, deliveryDate columns to the existing Checkout sheet
     *  (idempotent) and appends an HP LP3065 row. */
    private static void updateCheckoutSheet(XSSFWorkbook wb) {
        Sheet sheet = wb.getSheet("Checkout");
        if (sheet == null) {
            System.out.println("  Checkout sheet not found — skipping");
            return;
        }

        DataFormatter fmt = new DataFormatter();
        Row headerRow = sheet.getRow(0);
        int existingCols = headerRow.getLastCellNum();

        // Idempotency check — skip if productName column already present
        for (int c = 0; c < existingCols; c++) {
            Cell cell = headerRow.getCell(c);
            if (cell != null && "productName".equalsIgnoreCase(cell.getStringCellValue())) {
                System.out.println("  Checkout sheet already has product columns — skipping");
                return;
            }
        }

        // Append new column headers
        headerRow.createCell(existingCols).setCellValue("productName");
        headerRow.createCell(existingCols + 1).setCellValue("productCategory");
        headerRow.createCell(existingCols + 2).setCellValue("deliveryDate");

        // Tag every existing data row as iPod Shuffle / MP3 (no delivery date needed)
        int lastDataRow = sheet.getLastRowNum();
        for (int r = 1; r <= lastDataRow; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            row.createCell(existingCols).setCellValue("iPod Shuffle");
            row.createCell(existingCols + 1).setCellValue("MP3");
            row.createCell(existingCols + 2).setCellValue("");
        }

        // Append HP LP3065 row — copy billing data from first data row
        Row srcRow = sheet.getRow(1);
        if (srcRow != null) {
            Row hpRow = sheet.createRow(lastDataRow + 1);
            for (int c = 0; c < existingCols; c++) {
                Cell src = srcRow.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                hpRow.createCell(c).setCellValue(fmt.formatCellValue(src));
            }
            hpRow.createCell(existingCols).setCellValue("HP LP3065");
            hpRow.createCell(existingCols + 1).setCellValue("Laptops");
            hpRow.createCell(existingCols + 2).setCellValue("2026-12-31");
        }

        System.out.println("  Sheet updated: Checkout (productName/productCategory/deliveryDate added; HP LP3065 row appended)");
    }

    private static void addSheet(XSSFWorkbook wb, String name, String[] headers, String[][] data) {
        int existing = wb.getSheetIndex(name);
        if (existing >= 0) wb.removeSheetAt(existing);

        Sheet sheet = wb.createSheet(name);
        Row headerRow = sheet.createRow(0);
        for (int c = 0; c < headers.length; c++) {
            headerRow.createCell(c).setCellValue(headers[c]);
        }
        for (int r = 0; r < data.length; r++) {
            Row row = sheet.createRow(r + 1);
            for (int c = 0; c < data[r].length; c++) {
                row.createCell(c).setCellValue(data[r][c]);
            }
        }
        System.out.println("  Sheet added: " + name);
    }
}
