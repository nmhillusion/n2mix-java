package app.netlify.nmhillusion.n2mix.helper.office.excel;

import app.netlify.nmhillusion.n2mix.util.CollectionUtil;
import app.netlify.nmhillusion.n2mix.util.NumberUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    public ExcelDataModel getExcelDataModel() {
        return excelDataModel;
    }

    public ExcelDataSheet setExcelDataModel(ExcelDataModel excelDataModel) {
        this.excelDataModel = excelDataModel;
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

    public void addBodyData(Workbook workbook, Sheet sheet) {
        if (!CollectionUtil.isNullOrEmpty(excelDataModel.getBodyData())) {
            for (List<String> rowData : excelDataModel.getBodyData()) {
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
}
