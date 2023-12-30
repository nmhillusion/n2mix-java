package tech.nmhillusion.n2mix.helper.office.excel.writer;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tech.nmhillusion.n2mix.exception.MissingDataException;
import tech.nmhillusion.n2mix.helper.office.excel.writer.model.CallbackAddedDataToSheet;
import tech.nmhillusion.n2mix.helper.office.excel.writer.model.ExcelDataModel;
import tech.nmhillusion.n2mix.util.CollectionUtil;
import tech.nmhillusion.n2mix.util.NumberUtil;

import java.util.List;

/**
 * date: 2022-01-19
 * <p>
 * created-by: nmhillusion
 */

public class ExcelDataSheet {
    private static final short FONT_SIZE = 12;
    private static final String FONT_FAMILY = "Arial";
    private static final short DATA_FORMAT__GENERAL = (short) BuiltinFormats.getBuiltinFormat(BuiltinFormats.getBuiltinFormat(0));
    private static final short DATA_FORMAT__NUMBER = (short) BuiltinFormats.getBuiltinFormat("0");

    private ExcelDataModel excelDataModel;
    private int mainRowIndex = 0;

    private CallbackAddedDataToSheet callbackFunc;

    private Sheet sheetRef;

    public ExcelDataModel getExcelDataModel() {
        return excelDataModel;
    }

    public ExcelDataSheet setExcelDataModel(ExcelDataModel basicExcelDataModel) {
        this.excelDataModel = basicExcelDataModel;
        return this;
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        final CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillPattern(FillPatternType.NO_FILL);

        final XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName(FONT_FAMILY);
        font.setFontHeightInPoints(FONT_SIZE);
        font.setBold(true);
        headerStyle.setFont(font);

        return headerStyle;
    }

    public void addHeaders(Workbook workbook, Sheet sheet) {
        if (null == sheetRef) {
            sheetRef = sheet;
        }

        if (!CollectionUtil.isNullOrEmpty(excelDataModel.getHeaders())) {
            for (List<String> headerData : excelDataModel.getHeaders()) {
                if (!CollectionUtil.isNullOrEmpty(headerData)) {
                    final Row header = sheet.createRow(mainRowIndex++);
                    for (String headerCellData : headerData) {
                        final Cell headerCell = header.createCell(Math.max(0, header.getLastCellNum()));
                        headerCell.setCellStyle(createHeaderCellStyle(workbook));
                        headerCell.setCellValue(headerCellData);
                    }
                }
            }
        }
    }

    private CellStyle createCellStyle(Workbook workbook, String cellData) {
        final CellStyle rowStyle = workbook.createCellStyle();
        rowStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        rowStyle.setFillPattern(FillPatternType.NO_FILL);

        final XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName(FONT_FAMILY);
        font.setFontHeightInPoints(FONT_SIZE);
        font.setBold(false);
        rowStyle.setFont(font);

        if (NumberUtil.isNumber(cellData)) {
            rowStyle.setDataFormat(DATA_FORMAT__NUMBER);
        } else {
            rowStyle.setDataFormat(DATA_FORMAT__GENERAL);
        }

        return rowStyle;
    }

    public void addBodyData(Workbook workbook, Sheet sheet) throws MissingDataException {
        if (null == sheetRef) {
            sheetRef = sheet;
        }

        final List<List<String>> bodyData = excelDataModel.getBodyData();
        if (!CollectionUtil.isNullOrEmpty(bodyData)) {
            for (List<String> rowData : bodyData) {
                if (!CollectionUtil.isNullOrEmpty(rowData)) {
                    final Row row = sheet.createRow(mainRowIndex++);
                    for (String cellData : rowData) {
                        final Cell cell = row.createCell(Math.max(0, row.getLastCellNum()));
//                        cell.setCellStyle(createCellStyle(workbook, cellData));
                        cell.setCellValue(cellData);
                    }
                }
            }
        }
    }

    public Sheet getSheetRef() {
        return sheetRef;
    }

    public CallbackAddedDataToSheet getCallbackFunc() {
        return callbackFunc;
    }

    public ExcelDataSheet setCallbackFunc(CallbackAddedDataToSheet callbackFunc) {
        this.callbackFunc = callbackFunc;
        return this;
    }
}
