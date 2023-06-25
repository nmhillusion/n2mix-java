package com.chubb.library.report_render.service_impl;

import com.chubb.library.report_render.constant.ImageCompressionType;
import com.chubb.library.report_render.service.PdfToTiffImageService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

class PdfToTiffImageServiceImplTest {

    @Test
    void convertToTiff() throws IOException {
        final String inpPinterestPdfFileName = "photo/Pinterest.pdf";
        try (InputStream fis = getClass().getClassLoader().getResourceAsStream(inpPinterestPdfFileName)) {
            final List<byte[]> tiffDataList = PdfToTiffImageService.INSTANCE.convertToTiff(
                    inpPinterestPdfFileName,
                    fis,
                    124,
                    ImageCompressionType.ZLib
            );


            for (int pageIdx = 0; pageIdx < tiffDataList.size(); ++pageIdx) {
                final Path outputFilePath = Paths.get("output/" + inpPinterestPdfFileName + "__" + (pageIdx + 1) + ".tiff");
                Files.createDirectories(outputFilePath.getParent());

                try (final OutputStream imageOutput = Files.newOutputStream(
                        outputFilePath,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE
                )) {
                    final byte[] tiffData = tiffDataList.get(pageIdx);

                    imageOutput.write(tiffData);
                    imageOutput.flush();
                }
            }
        }
    }
}