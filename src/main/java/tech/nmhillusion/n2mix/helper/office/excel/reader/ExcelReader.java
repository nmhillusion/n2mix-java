package tech.nmhillusion.n2mix.helper.office.excel.reader;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tech.nmhillusion.n2mix.helper.office.excel.reader.model.CellData;
import tech.nmhillusion.n2mix.helper.office.excel.reader.model.RowData;
import tech.nmhillusion.n2mix.helper.office.excel.reader.model.SheetData;
import tech.nmhillusion.n2mix.util.StringUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-29
 */
public abstract class ExcelReader {
    public static List<SheetData> read(InputStream excelData) throws IOException {
        final List<SheetData> result_;

        try (final Workbook workbook = new XSSFWorkbook(excelData)) {
            final int sheetCount = workbook.getNumberOfSheets();

            result_ = new ArrayList<>(IntStream.range(0, sheetCount)
                    .mapToObj(sheetIdx -> readSheetContent(workbook.getSheetAt(sheetIdx)))
                    .toList());
        }
        return result_;
    }

    private static SheetData readSheetContent(Sheet sheet_) {
        final SheetData sheetData = new SheetData();
        final List<RowData> rowDataList = new ArrayList<>();

        final int firstRowNum = sheet_.getFirstRowNum();

        if (0 <= firstRowNum) {
            final int lastRowNum = sheet_.getLastRowNum();

            IntStream.rangeClosed(firstRowNum, lastRowNum)
                    .mapToObj(rowIdx -> readRowContent(sheet_.getRow(rowIdx)))
                    .forEach(rowDataList::add);
        }

        {
            //-- Mark: remove empty last row
            final int lastIdx = rowDataList.size() - 1;

            if (
                    rowDataList.get(lastIdx).getCells().stream().allMatch(it -> StringValidator.isBlank(
                            StringUtil.trimWithNull(it.getRawData())
                    ))
            ) {
                rowDataList.remove(lastIdx);
            }
        }

        return sheetData
                .setSheetName(sheet_.getSheetName())
                .setRows(rowDataList);
    }

    private static RowData readRowContent(Row row_) {
        if (null == row_) {
            return new RowData()
                    .setCells(new ArrayList<>());
        }

        final RowData rowData_ = new RowData();
        final List<CellData> cellDataList = new ArrayList<>();

        final short firstCellNum = row_.getFirstCellNum();
        if (0 <= firstCellNum) {
            final short lastCellNum = row_.getLastCellNum();

            for (int cellIdx = firstCellNum; cellIdx < lastCellNum; cellIdx += 1) {
                cellDataList.add(
                        readCellContent(
                                row_.getCell(cellIdx)
                        )
                );
            }
        }


        return rowData_
                .setCells(cellDataList);
    }

    private static CellData readCellContent(Cell cell_) {
        if (null == cell_) {
            return new CellData()
                    .setCellType(CellType.BLANK)
                    .setRawData("");
        }

        final CellType cellType = cell_.getCellType();
        Object rawValue = null;

        switch (cellType) {
            case BOOLEAN -> rawValue = cell_.getBooleanCellValue();
            case FORMULA -> rawValue = cell_.getRichStringCellValue().getString();
            case STRING -> rawValue = cell_.getStringCellValue();
            case NUMERIC -> rawValue = cell_.getNumericCellValue();
            default -> rawValue = "";
        }

        return new CellData()
                .setCellType(cellType)
                .setRawData(rawValue)
                ;
    }
}
