package tech.nmhillusion.n2mix.helper.office;

import tech.nmhillusion.n2mix.helper.office.excel.ExcelWriteHelper;
import tech.nmhillusion.n2mix.helper.office.excel.model.BasicExcelDataModel;
import tech.nmhillusion.n2mix.helper.office.excel.model.ExcelDataConverterModel;
import tech.nmhillusion.n2mix.type.ChainList;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ExcelWriteHelperTest {

    @Test
    void exportData() {
        assertDoesNotThrow(() -> {
            final byte[] excelData = new ExcelWriteHelper()
                    .addSheetData(new BasicExcelDataModel()
                            .setHeaders(Collections.singletonList(Arrays.asList("ID", "Name")))
                            .setBodyData(Collections.singletonList(Arrays.asList("1", "Rondônia")))
                            .setSheetName("user_data")
                    )
                    .build();
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
                    .build();
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