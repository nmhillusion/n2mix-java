package tech.nmhillusion.n2mix.helper.office.excel.reader.model;

import tech.nmhillusion.n2mix.type.Stringeable;

import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-29
 */
public class RowData extends Stringeable {
    private List<CellData> cells;

    public List<CellData> getCells() {
        return cells;
    }

    public RowData setCells(List<CellData> cells) {
        this.cells = cells;
        return this;
    }
}
