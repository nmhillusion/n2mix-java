package tech.nmhillusion.n2mix.helper.office.excel.reader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.helper.office.excel.reader.model.CellData;
import tech.nmhillusion.n2mix.helper.office.excel.reader.model.SheetData;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-29
 */
class ExcelReaderTest {

    @Test
    void testSimpleReadWithoutThrow() {
        Assertions.assertDoesNotThrow(this::testBasicReadSimpleExcelFile);
    }

    @Test
    void testBasicReadSimpleExcelFile() throws IOException {
        try (final InputStream excelData_ = getClass().getClassLoader().getResourceAsStream("excel/sample_data.xlsx")) {
            final List<SheetData> readData = ExcelReader.read(excelData_);

            LogHelper.getLogger(this).info("read data from excel: " + readData);

            Assertions.assertFalse(readData.isEmpty());
            Assertions.assertEquals(1, readData.size());
            Assertions.assertEquals(5, readData.get(0).getRows().size());
            Assertions.assertEquals("Sheet1", readData.get(0).getSheetName());
            Assertions.assertEquals(
                    List.of("ID", "First Name", "Last Name", "Full Name", "Gender", "Achieved"),
                    readData.get(0).getRows().get(0).getCells()
                            .stream().map(CellData::getRawData)
                            .toList()
            );
            Assertions.assertEquals(
                    List.of(1d, "Thanh", "Trần Văn", "Trần Văn Thanh", "M", "Dr"),
                    readData.get(0).getRows().get(1).getCells()
                            .stream().map(CellData::getRawData)
                            .toList()
            );
            Assertions.assertEquals(
                    List.of(2d, "Tuấn", "Kiều Nhật", "Kiều Nhật Tuấn", "M"),
                    readData.get(0).getRows().get(2).getCells()
                            .stream().map(CellData::getRawData)
                            .toList()
            );
            Assertions.assertEquals(
                    List.of(3d, "Minh", "Thái Thị Tuyết", "Thái Thị Tuyết Minh", "F", "Bachelor"),
                    readData.get(0).getRows().get(3).getCells()
                            .stream().map(CellData::getRawData)
                            .toList()
            );

            Assertions.assertEquals(
                    List.of("2.0", "Tuấn", "Kiều Nhật", "Kiều Nhật Tuấn", "M"),
                    readData.get(0).getRows().get(2).getCells()
                            .stream().map(CellData::getStringValue)
                            .toList()
            );

            Assertions.assertEquals(
                    List.of(4d, "", "", "Thái Minh", "", "General"),
                    readData.get(0).getRows().get(4).getCells()
                            .stream().map(CellData::getRawData)
                            .toList()
            );
        }
    }
}