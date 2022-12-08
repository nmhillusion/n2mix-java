package app.netlify.nmhillusion.n2mix.helper.office;

import app.netlify.nmhillusion.n2mix.helper.office.excel.ExcelDataModel;
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
                    .addSheetData(new ExcelDataModel()
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
}