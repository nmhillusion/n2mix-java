package tech.nmhillusion.n2mix.helper.office.excel.writer.model;

import tech.nmhillusion.n2mix.exception.InvalidArgument;
import tech.nmhillusion.n2mix.exception.MissingDataException;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.type.ChainList;
import tech.nmhillusion.n2mix.type.Pair;
import tech.nmhillusion.n2mix.type.function.ThrowableFunction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * date: 2023-03-05
 * <p>
 * created-by: nmhillusion
 */

public class ExcelDataConverterModel<T> implements ExcelDataModel {
    private final List<Map.Entry<String, ThrowableFunction<T, String>>> columnConverters = new ArrayList<>();
    private String sheetName;
    private List<T> rawData;

    @Override
    public String getSheetName() {
        return sheetName;
    }

    public ExcelDataConverterModel<T> setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    @Override
    public List<List<String>> getHeaders() {
        return new ChainList<List<String>>()
                .chainAdd(
                        columnConverters.stream().map(Map.Entry::getKey).collect(Collectors.toList())
                );
    }

    public List<T> getRawData() {
        return rawData;
    }

    public ExcelDataConverterModel<T> setRawData(List<T> rawData) {
        this.rawData = rawData;
        return this;
    }

    public ExcelDataConverterModel<T> addColumnConverters(String columnName, ThrowableFunction<T, String> columnConverter) throws InvalidArgument {
        if (null == columnName) {
            throw new InvalidArgument("columnName must not be null");
        }

        if (null == columnConverter) {
            throw new InvalidArgument("columnConverter must not be null");
        }

        this.columnConverters.add(new Pair<>(columnName, columnConverter));
        return this;
    }

    @Override
    public List<List<String>> getBodyData() throws MissingDataException {
        if (null == rawData) {
            throw new MissingDataException("Missing `rawData` to execute excel converter");
        }

        return rawData.stream()
                .map(item -> {
                    final List<String> rowData = new LinkedList<>();
                    for (Map.Entry<String, ThrowableFunction<T, String>> colConverter : columnConverters) {
                        String cellData = "";
                        try {
                            cellData = colConverter.getValue().throwableApply(item);
                        } catch (Throwable e) {
                            LogHelper.getLogger(this).error(e);
                        }
                        rowData.add(cellData);
                    }
                    return rowData;
                })
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
