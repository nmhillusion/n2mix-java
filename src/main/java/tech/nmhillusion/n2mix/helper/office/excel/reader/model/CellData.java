package tech.nmhillusion.n2mix.helper.office.excel.reader.model;

import org.apache.poi.ss.usermodel.CellType;
import tech.nmhillusion.n2mix.type.Stringeable;
import tech.nmhillusion.n2mix.util.StringUtil;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-29
 */
public class CellData extends Stringeable {
    private CellType cellType;
    private Object rawData;

    public CellType getCellType() {
        return cellType;
    }

    public CellData setCellType(CellType cellType) {
        this.cellType = cellType;
        return this;
    }

    public Object getRawData() {
        return rawData;
    }

    public CellData setRawData(Object rawData) {
        this.rawData = rawData;
        return this;
    }

    public String getStringValue() {
        return StringUtil.trimWithNull(rawData);
    }
}
