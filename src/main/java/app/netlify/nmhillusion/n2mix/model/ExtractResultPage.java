package app.netlify.nmhillusion.n2mix.model;

import app.netlify.nmhillusion.n2mix.type.RowResultSetMap;

import java.util.List;

/**
 * date: 2020-09-26
 * created-by: minguy1
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
