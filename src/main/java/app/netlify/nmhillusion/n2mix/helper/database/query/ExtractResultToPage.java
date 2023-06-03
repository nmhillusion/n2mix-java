package app.netlify.nmhillusion.n2mix.helper.database.query;

import app.netlify.nmhillusion.n2mix.exception.ApiResponseException;
import app.netlify.nmhillusion.n2mix.model.ExtractResultPage;
import app.netlify.nmhillusion.n2mix.model.database.DbExportDataModel;
import app.netlify.nmhillusion.n2mix.type.RowResultSetMap;
import app.netlify.nmhillusion.n2mix.util.ExceptionUtil;
import app.netlify.nmhillusion.n2mix.util.NumberUtil;
import org.springframework.util.NumberUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.netlify.nmhillusion.n2mix.helper.log.LogHelper.getLogger;


/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public class ExtractResultToPage {
    public static ExtractResultPage extractOracleResultToPage(ResultSet ors, int pageIndex, int pageSize) throws ApiResponseException {
        ExtractResultPage result = new ExtractResultPage();
        try {
            int startIdx = pageIndex * pageSize;
            int endIndex = startIdx + pageSize;

            int index = -1;
            ResultSetMetaData metaData = ors.getMetaData();

            int columnCnt = metaData.getColumnCount();
            List<String> columnNames = new ArrayList<>();

            // collect column names
            for (int columnIdx = 1; columnIdx <= columnCnt; ++columnIdx) {
                columnNames.add(metaData.getColumnName(columnIdx));
            }

            List<RowResultSetMap<Object>> content = new ArrayList<>();
            while (ors.next()) {
                index += 1;
                if (startIdx <= index && index < endIndex) {
                    RowResultSetMap<Object> row = new RowResultSetMap<>();
                    for (String columnName : columnNames) {
                        Object data = ors.getObject(columnName);
                        row.put(columnName, data);
                    }
                    content.add(row);
                }
            }
            result.setTotal(index + 1);
            result.setContent(content);
            return result;
        } catch (Exception ex) {
            getLogger(ExtractResultToPage.class).error(ex);
            throw ExceptionUtil.throwException(ex);
        }
    }

    public static String toStringResultSetMetadata(ResultSetMetaData metaData) throws SQLException {
        StringBuilder builder = new StringBuilder("[ResultSetMetaData] {");
        builder.append("\ncolumnCount: ").append(metaData.getColumnCount());
        builder.append("\ncolumns: ");
        for (int column = 1; column < metaData.getColumnCount(); ++column) {
            builder.append("\n\tname: ").append(metaData.getColumnName(column));
            builder.append("\n\ttype: ").append(metaData.getColumnTypeName(column));
            builder.append("\n\tlabel: ").append(metaData.getColumnLabel(column));
            builder.append("\n\tdisplaySize: ").append(metaData.getColumnDisplaySize(column));
            builder.append("\n\tclassName: ").append(metaData.getColumnClassName(column));
            builder.append("\n\ttable: ").append(metaData.getTableName(column));
            builder.append("\n\tschema: ").append(metaData.getSchemaName(column));
            builder.append("\n\tcatalog: ").append(metaData.getCatalogName(column));
            builder.append("\n\t==================");
        }
        builder.append("\n}");
        return builder.toString();
    }

    public static <T> T getOrDefaultColumn(ResultSet resultSet, String columnName, Class<T> type, T defaultValue) {
        try {
            return resultSet.getObject(columnName, type);
        } catch (Exception ex) {
            getLogger(ExtractResultToPage.class).error(ex.getMessage());
            return defaultValue;
        }
    }

    public static List<String> getAllColumnNames(ResultSet resultSet) throws SQLException {
        final List<String> columnNames = new ArrayList<>();
        final ResultSetMetaData metaData = resultSet.getMetaData();

        for (int colIndex = 0; colIndex < metaData.getColumnCount(); ++colIndex) {
            columnNames.add(metaData.getColumnLabel(colIndex + 1));
        }

        return columnNames;
    }

    public static DbExportDataModel buildDbExportDataModel(ResultSet resultSet) throws SQLException {
        final DbExportDataModel model = new DbExportDataModel();
        List<String> header = ExtractResultToPage.getAllColumnNames(resultSet);
        List<List<String>> values = new ArrayList<>();
        while (resultSet.next()) {
            List<String> row = new ArrayList<>();
            for (int i = 1; i <= header.size(); i++) {
                row.add(resultSet.getString(i));
            }
            values.add(row);
        }
        model.setHeader(header);
        model.setValues(values);
        return model;
    }

    public static Map<String, Object> parseFirstRowToMap(ResultSet resultSet) throws SQLException {
        return parseRowToMap(resultSet, 0);
    }

    public static Map<String, Object> parseRowToMap(ResultSet resultSet, int rowIndexToGet) throws SQLException {
        final Map<String, Object> resultMap = new HashMap<>();

        int rowIndex = -1;
        while (rowIndex < rowIndexToGet) {
            if (resultSet.next()) {
                rowIndex += 1;
            } else {
                break;
            }
        }

        final List<String> columnNames = getAllColumnNames(resultSet);
        if (rowIndex >= 0) {
            for (String colName : columnNames) {
                final Object rawValue = resultSet.getObject(colName);
                final String stringRawValue = String.valueOf(rawValue);
                Object rowValue = rawValue;

                if (NumberUtil.isInteger(rawValue)) {
                    rowValue = NumberUtils.parseNumber(stringRawValue, Integer.class);
                } else if (NumberUtil.isLong(rawValue)) {
                    rowValue = NumberUtils.parseNumber(stringRawValue, Long.class);
                } else if (NumberUtil.isFloat(rawValue)) {
                    rowValue = NumberUtils.parseNumber(stringRawValue, Float.class);
                } else if (NumberUtil.isDouble(rawValue)) {
                    rowValue = NumberUtils.parseNumber(stringRawValue, Double.class);
                }

                resultMap.put(colName, rowValue);
            }
        }

        return resultMap;
    }
}
