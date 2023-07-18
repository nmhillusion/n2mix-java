package tech.nmhillusion.n2mix.model;

import tech.nmhillusion.n2mix.type.RowResultSetMap;

import java.util.List;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public class ExtractResultPage {
    public static final String TOTAL_ROWS_COLUMN_NAME = "total_rows";
    private long total;
    private List<RowResultSetMap<Object>> content;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<RowResultSetMap<Object>> getContent() {
        return content;
    }

    public void setContent(List<RowResultSetMap<Object>> content) {
        this.content = content;
    }
}
