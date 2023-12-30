package tech.nmhillusion.n2mix.helper.office.excel.writer.model;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import tech.nmhillusion.n2mix.helper.office.excel.writer.ExcelDataSheet;
import tech.nmhillusion.n2mix.helper.office.excel.writer.ExcelWriteHelper;

import java.io.IOException;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-12-28
 */
@FunctionalInterface
public interface CallbackAddedDataToSheet {
    void exec(ExcelWriteHelper self, ExcelDataSheet dataSheet, Workbook workbookRef, Sheet sheetRef) throws IOException;
}
