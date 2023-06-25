package com.chubb.library.report_render.service;

import com.chubb.library.report_render.constant.ImageCompressionType;
import com.chubb.library.report_render.service_impl.PdfToTiffImageServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * date: 2023-05-13
 * <p>
 * created-by: minguy1
 */

public interface PdfToTiffImageService {
    PdfToTiffImageService INSTANCE = new PdfToTiffImageServiceImpl();

    List<byte[]> convertToTiff(String fileId, InputStream pdfData, int dpiOfImage, ImageCompressionType compressionType) throws IOException;
}
