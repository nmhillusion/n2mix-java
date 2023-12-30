package tech.nmhillusion.n2mix.helper.office.excel.writer;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.helper.office.excel.writer.model.BasicExcelDataModel;
import tech.nmhillusion.n2mix.helper.office.excel.writer.model.ExcelDataConverterModel;
import tech.nmhillusion.n2mix.type.ChainList;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CustomExcelWriteHelperTest {

    private static final short TEST_FONT_SIZE = 22;
    private static final String TEST_FONT_FAMILY = "Verdana";

    private CellStyle createTestCellStyle(Workbook workbook) {
        final CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillPattern(FillPatternType.NO_FILL);

        final XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName(TEST_FONT_FAMILY);
        font.setFontHeightInPoints(TEST_FONT_SIZE);
        font.setBold(false);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        return headerStyle;
    }

    private void callbackExportDataWithStyling(ExcelWriteHelper self, ExcelDataSheet dataSheet, Workbook workbookRef, Sheet sheetRef) {
        final Row firstRow_ = sheetRef.getRow(0);
        final short firstCellNum = firstRow_.getFirstCellNum();
        final short lastCellNum = firstRow_.getLastCellNum();

        for (int cellIdx = firstCellNum; cellIdx < lastCellNum; cellIdx++) {
            final Cell cell_ = firstRow_.getCell(cellIdx);

            cell_.setCellStyle(
                    createTestCellStyle(sheetRef.getWorkbook())
            );
        }

        sheetRef.autoSizeColumn(1, true);
        sheetRef.autoSizeColumn(2, true);
    }

    @Test
    void exportData() {
        assertDoesNotThrow(() -> {
            final ExcelWriteHelper excelWriteHelper = new ExcelWriteHelper()
                    .addSheetData(new BasicExcelDataModel()
                            .setHeaders(Collections.singletonList(Arrays.asList("ID", "Name")))
                            .setBodyData(List.of(
                                    Arrays.asList("1", "Rondônia")
                                    , Arrays.asList("2", "This is a very long cell content")
                            ))
                            .setSheetName("user_data")
                    );
            final byte[] excelData = excelWriteHelper
                    .build(this::callbackExportDataWithStyling);
            try (OutputStream os = Files.newOutputStream(new File("test.data.xlsx").toPath())) {
                os.write(excelData);
                os.flush();
            }
        }, "testing export data");
    }

    @Test
    void exportDataWithConverter() {
        assertDoesNotThrow(() -> {
            final byte[] excelData = new ExcelWriteHelper()
                    .addSheetData(new ExcelDataConverterModel<Book>()
                            .setSheetName("books")
                            .setRawData(new ChainList<Book>()
                                    .chainAdd(
                                            new Book()
                                                    .setTitle("Cook Code")
                                                    .setAuthor("Abraham")
                                                    .setPrice(109.2f)
                                    )
                                    .chainAdd(
                                            new Book()
                                                    .setTitle("Failure Programing")
                                                    .setAuthor("Joe Lin")
                                                    .setPrice(208.11f)
                                    )
                            )
                            .addColumnConverters(
                                    "Title", Book::getTitle
                            )
                            .addColumnConverters(
                                    "Price", b -> String.format("%.3f USD", b.getPrice())
                            )
                            .addColumnConverters(
                                    "Author", Book::getAuthor
                            )
                    )
                    .build(this::callbackExportDataWithStyling);
            try (OutputStream os = Files.newOutputStream(new File("test.data.converter.xlsx").toPath())) {
                os.write(excelData);
                os.flush();
            }
        }, "testing export data with converter");
    }

    private static class Book {
        private String title;
        private String author;
        private double price;

        public String getTitle() {
            return title;
        }

        public Book setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getAuthor() {
            return author;
        }

        public Book setAuthor(String author) {
            this.author = author;
            return this;
        }

        public double getPrice() {
            return price;
        }

        public Book setPrice(double price) {
            this.price = price;
            return this;
        }
    }
}