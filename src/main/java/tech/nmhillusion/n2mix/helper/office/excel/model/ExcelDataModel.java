package tech.nmhillusion.n2mix.helper.office.excel.model;

import tech.nmhillusion.n2mix.exception.MissingDataException;

import java.util.List;

/**
 * date: 2023-03-05
 * <p>
 * created-by: nmhillusion
 */

public interface ExcelDataModel {
    String getSheetName();

    List<List<String>> getHeaders();

    List<List<String>> getBodyData() throws MissingDataException;
}
