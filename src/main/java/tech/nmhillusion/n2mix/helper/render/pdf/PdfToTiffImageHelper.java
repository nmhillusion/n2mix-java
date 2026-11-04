package tech.nmhillusion.n2mix.helper.render.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
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

    private static void throwIfPageIndexInvalid(String fileId, int pageIndex, int pagesCount) {
        if (0 > pageIndex || pageIndex >= pagesCount) {
            throw new IndexOutOfBoundsException(
                    "[%s] Page index out of bounds: %s, must be in range [0..%s]"
                            .formatted(fileId, pageIndex, pagesCount - 1)
            );
        }
    }

    private byte[] convertToTiffUsingImageWriterJavax(PDFRenderer pdfRenderer,
                                                      ImageCompressionType compressionType,
                                                      int dpiOfImage,
                                                      String fileId,
                                                      int pageIndex
    ) throws IOException {
        final Iterator<ImageWriter> tiffImageWriterIter = ImageIO.getImageWritersByFormatName("TIFF");

        if (!tiffImageWriterIter.hasNext()) {
            throw new IOException("Cannot find ImageWriter for TIFF image");
        }

        final ImageWriter imageWriter_ = tiffImageWriterIter.next();

        final ImageWriteParam imageWriteParam = imageWriter_.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionType(compressionType.getValue());
        imageWriteParam.setCompressionQuality(1);

        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             final ImageOutputStream ios_ = new MemoryCacheImageOutputStream(byteArrayOutputStream)) {
            imageWriter_.setOutput(ios_);
            imageWriter_.prepareWriteSequence(null);

            LogHelper.getLogger(this).info(
                    "rendering on page %s of %s".formatted(pageIndex, fileId)
            );

            final BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(
                    pageIndex,
                    dpiOfImage,
                    ImageType.RGB
            );

            imageWriter_.writeToSequence(new IIOImage(bufferedImage, null, null), imageWriteParam);

            imageWriter_.endWriteSequence();
            ios_.flush();

            return byteArrayOutputStream.toByteArray();
        } finally {
            imageWriter_.dispose();
        }
    }

    private byte[] convertToTiffUsingImageIOUtilPdfBox(PDFRenderer pdfRenderer,
                                                       ImageCompressionType compressionType,
                                                       int dpiOfImage,
                                                       String fileId,
                                                       int pageIndex
    ) throws IOException {
        final BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(
                pageIndex,
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

            return byteArrayOutputStream.toByteArray();
        }
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

            final int numberOfPages = pdDocument.getNumberOfPages();
            try {

                for (int pageIdx = 0; pageIdx < numberOfPages; pageIdx++) {
                    tiffDataList.add(
                            convertToTiffUsingImageIOUtilPdfBox(
                                    pdfRenderer,
                                    compressionType,
                                    dpiOfImage,
                                    fileId,
                                    pageIdx
                            )
                    );
                }
            } catch (Exception ex) {
                LogHelper.getLogger(this).warn(
                        "Fail to convert to tiff (String fileId = %s, InputStream pdfData, int dpiOfImage = %s, ImageCompressionType compressionType = %s) using method convertToTiffUsingImageIOUtilPdfBox, Error: %s, switch to use method convertToTiffUsingImageWriterJavax"
                                .formatted(
                                        fileId,
                                        dpiOfImage,
                                        compressionType,
                                        "[%s] %s".formatted(ex.getClass(), ex.getMessage())
                                )
                );

                tiffDataList.clear();
                try {
                    for (int pageIdx = 0; pageIdx < numberOfPages; pageIdx++) {
                        tiffDataList.add(
                                convertToTiffUsingImageWriterJavax(
                                        pdfRenderer,
                                        compressionType,
                                        dpiOfImage,
                                        fileId,
                                        pageIdx
                                )
                        );
                    }
                } catch (Exception fallbackEx) {
                    fallbackEx.addSuppressed(ex);
                    throw fallbackEx;
                }

            }

            ///////////////////////////////////////////////////
        }
        LogHelper.getLogger(this).info(
                "end convert to tiff of %s with param (int dpiOfImage = %s, ImageCompressionType compressionType = %s)"
                        .formatted(fileId, dpiOfImage, compressionType)
        );
        return tiffDataList;
    }

    public byte[] convertToTiffForPage(String fileId, InputStream pdfData, int dpiOfImage, ImageCompressionType compressionType, int pageIndex) throws IOException {
        LogHelper.getLogger(this).info(
                "start convert to tiff of %s with param (int dpiOfImage = %s, ImageCompressionType compressionType = %s, pageIndex = %s)"
                        .formatted(fileId, dpiOfImage, compressionType, pageIndex)
        );

        byte[] tiffData = null;

        try (final PDDocument pdDocument = PDDocument.load(pdfData)) {
            final PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);

            throwIfPageIndexInvalid(fileId, pageIndex, pdDocument.getNumberOfPages());

            ///////////////////////////////////////////////////

            try {

                tiffData = convertToTiffUsingImageIOUtilPdfBox(
                        pdfRenderer,
                        compressionType,
                        dpiOfImage,
                        fileId,
                        pageIndex
                );
            } catch (Exception ex) {
                LogHelper.getLogger(this).warn(
                        "Fail to convert to tiff (String fileId = %s, InputStream pdfData, int dpiOfImage = %s, ImageCompressionType compressionType = %s, pageIndex = %s) using method convertToTiffUsingImageIOUtilPdfBox, Error: %s, switch to use method convertToTiffUsingImageWriterJavax"
                                .formatted(
                                        fileId,
                                        dpiOfImage,
                                        compressionType,
                                        pageIndex,
                                        "[%s] %s".formatted(ex.getClass(), ex.getMessage())
                                )
                );

                try {
                    tiffData = convertToTiffUsingImageWriterJavax(
                            pdfRenderer,
                            compressionType,
                            dpiOfImage,
                            fileId,
                            pageIndex
                    );
                } catch (Exception fallbackEx) {
                    fallbackEx.addSuppressed(ex);
                    throw fallbackEx;
                }
            }

            ///////////////////////////////////////////////////
        }
        LogHelper.getLogger(this).info(
                "end convert to tiff of %s with param (int dpiOfImage = %s, ImageCompressionType compressionType = %s, pageIndex = %s)"
                        .formatted(fileId, dpiOfImage, compressionType, pageIndex)
        );
        return tiffData;
    }
}
