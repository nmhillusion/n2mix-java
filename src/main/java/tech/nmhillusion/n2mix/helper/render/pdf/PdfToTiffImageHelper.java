package tech.nmhillusion.n2mix.helper.render.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import tech.nmhillusion.n2mix.constant.ImageCompressionType;
import tech.nmhillusion.n2mix.helper.log.LogHelper;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * date: 2023-05-13
 * <p>
 * created-by: nmhillusion
 */

public class PdfToTiffImageHelper {

    private List<byte[]> convertToTiffUsingImageWriterJavax(PDDocument pdDocument,
                                                            PDFRenderer pdfRenderer,
                                                            ImageCompressionType compressionType,
                                                            int dpiOfImage,
                                                            String fileId
    ) throws IOException {
        final List<byte[]> tiffDataList = new ArrayList<>();
        final Iterator<ImageWriter> tiffImageWriterIter = ImageIO.getImageWritersByFormatName("TIFF");

        if (!tiffImageWriterIter.hasNext()) {
            throw new IOException("Cannot find ImageWriter for TIFF image");
        }

        final ImageWriter imageWriter_ = tiffImageWriterIter.next();

        final ImageWriteParam imageWriteParam = imageWriter_.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionType(compressionType.getValue());
        imageWriteParam.setCompressionQuality(1);

        int pageCounter = 0;
        for (PDPage page_ : pdDocument.getPages()) {
            try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 final ImageOutputStream ios_ = new MemoryCacheImageOutputStream(byteArrayOutputStream)) {
                imageWriter_.setOutput(ios_);
                imageWriter_.prepareWriteSequence(null);

                LogHelper.getLogger(this).info(
                        "rendering on page %s of %s".formatted(pageCounter, fileId)
                );

                final BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(
                        pageCounter++,
                        dpiOfImage,
                        ImageType.RGB);

                imageWriter_.writeToSequence(new IIOImage(bufferedImage, null, null), imageWriteParam);

                imageWriter_.endWriteSequence();
                ios_.flush();

                tiffDataList.add(
                        byteArrayOutputStream.toByteArray()
                );
            }
        }

        imageWriter_.dispose();
        ///////////////////////////////////////////////////
        return tiffDataList;
    }

    private List<byte[]> convertToTiffUsingImageIOUtilPdfBox(PDDocument pdDocument,
                                                             PDFRenderer pdfRenderer,
                                                             ImageCompressionType compressionType,
                                                             int dpiOfImage,
                                                             String fileId
    ) throws IOException {
        final List<byte[]> tiffDataList = new ArrayList<>();

        int pageCounter = 0;
        for (PDPage page_ : pdDocument.getPages()) {
            LogHelper.getLogger(this).info(
                    "rendering on page %s of %s".formatted(pageCounter, fileId)
            );

            final BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(
                    pageCounter++,
                    dpiOfImage,
                    ImageType.RGB);

            try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ImageIOUtil.writeImage(
                        bufferedImage,
                        "tiff",
                        byteArrayOutputStream,
                        dpiOfImage,
                        1,
                        compressionType.getValue()
                );

                tiffDataList.add(
                        byteArrayOutputStream.toByteArray()
                );
            }
        }

        return tiffDataList;
    }

    public List<byte[]> convertToTiff(String fileId, InputStream pdfData, int dpiOfImage, ImageCompressionType compressionType) throws IOException {
        LogHelper.getLogger(this).info(
                "start convert to tiff of %s with param (int dpiOfImage = %s, ImageCompressionType compressionType = %s)"
                        .formatted(fileId, dpiOfImage, compressionType)
        );
        final List<byte[]> tiffDataList = new ArrayList<>();

        try (final PDDocument pdDocument = PDDocument.load(pdfData)) {
            final PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);

            ///////////////////////////////////////////////////

            try {
                tiffDataList.addAll(
                        convertToTiffUsingImageIOUtilPdfBox(
                                pdDocument,
                                pdfRenderer,
                                compressionType,
                                dpiOfImage,
                                fileId
                        )
                );
            } catch (Throwable ex) {
                LogHelper.getLogger(this).warn(
                        "Fail to convert to tiff (String fileId = %s, InputStream pdfData, int dpiOfImage = %s, ImageCompressionType compressionType = %s) using method convertToTiffUsingImageIOUtilPdfBox, Error: %s, switch to use method convertToTiffUsingImageWriterJavax"
                                .formatted(
                                        fileId,
                                        dpiOfImage,
                                        compressionType,
                                        ex.getMessage()
                                )
                );

                tiffDataList.addAll(
                        convertToTiffUsingImageWriterJavax(
                                pdDocument,
                                pdfRenderer,
                                compressionType,
                                dpiOfImage,
                                fileId
                        )
                );
            }

            ///////////////////////////////////////////////////
        }
        LogHelper.getLogger(this).info(
                "end convert to tiff of %s with param (int dpiOfImage = %s, ImageCompressionType compressionType = %s)"
                        .formatted(fileId, dpiOfImage, compressionType)
        );
        return tiffDataList;
    }
}
