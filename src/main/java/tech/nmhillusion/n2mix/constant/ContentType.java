package tech.nmhillusion.n2mix.constant;

import org.springframework.http.MediaType;

public class ContentType {
    public static final String JSON = MediaType.APPLICATION_JSON_VALUE;
    public static final String FORM_URLENCODED = MediaType.APPLICATION_FORM_URLENCODED_VALUE;
    public static final String MULTIPART_FORM_DATA = MediaType.MULTIPART_FORM_DATA_VALUE;
    public static final String TEXT_PLAIN = MediaType.TEXT_PLAIN_VALUE;
    public static final String TEXT_HTML = MediaType.TEXT_HTML_VALUE;
    public static final String TEXT_XML = MediaType.TEXT_XML_VALUE;
    public static final String IMAGE_PNG = MediaType.IMAGE_PNG_VALUE;
    public static final String OCTET_STREAM = MediaType.APPLICATION_OCTET_STREAM_VALUE;
    public static final String PDF = MediaType.APPLICATION_PDF_VALUE;
    public static final String MS_EXCEL_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MS_EXCEL_XLS = "application/vnd.ms-excel";
    public static final String MS_WORD_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingm";
    public static final String MS_WORD_DOC = "application/msword";
}
