package tech.nmhillusion.n2mix.helper.office.excel.writer.model;

import java.util.List;

/**
 * date: 2022-01-19
 * <p>
 * created-by: nmhillusion
 */

public class BasicExcelDataModel implements ExcelDataModel {
    private String sheetName;
    private List<List<String>> headers;
    private List<List<String>> bodyData;

    public String getSheetName() {
        return sheetName;
    }

    public BasicExcelDataModel setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public List<List<String>> getHeaders() {
        return headers;
    }

    public BasicExcelDataModel setHeaders(List<List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public List<List<String>> getBodyData() {
        return bodyData;
    }

    public BasicExcelDataModel setBodyData(List<List<String>> bodyData) {
        this.bodyData = bodyData;
        return this;
    }
}
