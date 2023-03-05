package app.netlify.nmhillusion.n2mix.helper.office.excel;

import app.netlify.nmhillusion.n2mix.exception.MissingDataException;
import app.netlify.nmhillusion.n2mix.helper.office.excel.model.ExcelDataModel;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * date: 2021-11-15
 * <p>
 * created-by: nmhillusion
 */

public class ExcelWriteHelper {
    private final Workbook workbook = new XSSFWorkbook();
    private final List<ExcelDataSheet> dataSheets = new ArrayList<>();

    public ExcelWriteHelper addSheetData(ExcelDataModel excelDataOfSheet) {
        dataSheets.add(
                new ExcelDataSheet()
                        .setExcelDataModel(excelDataOfSheet)
        );
        return this;
    }

    public byte[] build() throws IOException, MissingDataException {
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            for (ExcelDataSheet dataSheet : dataSheets) {
                final Sheet sheet = workbook.createSheet(dataSheet.getExcelDataModel().getSheetName());

                dataSheet.addHeaders(workbook, sheet);
                dataSheet.addBodyData(workbook, sheet);
            }

            workbook.write(byteArrayOutputStream);
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }
}
