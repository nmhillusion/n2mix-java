package tech.nmhillusion.n2mix.helper.office.excel.reader.model;

import tech.nmhillusion.n2mix.type.Stringeable;

import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-29
 */
public class SheetData extends Stringeable {
    private String sheetName;
    private List<RowData> rows;

    public String getSheetName() {
        return sheetName;
    }

    public SheetData setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public List<RowData> getRows() {
        return rows;
    }

    public SheetData setRows(List<RowData> rows) {
        this.rows = rows;
        return this;
    }
}
