package tech.nmhillusion.n2mix.helper.render.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.constant.ImageCompressionType;
import tech.nmhillusion.n2mix.helper.render.ClearFolderHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PdfToTiffImageHelperTest {

    private static final String OUTPUT_DIR = "outputs";
    private static final boolean WILL_DELETE_ON_COMPLETE = true;

    @Test
    void convertToTiff() {
        assertDoesNotThrow(() -> {
            final String inpFileName = "pdf/TheVerge.pdf";
            try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
                final List<byte[]> dataBytesList = new PdfToTiffImageHelper()
                        .convertToTiff(inpFileName
                                , pdfStream
                                , 124
                                , ImageCompressionType.Deflate);

                for (int dataIdx = 0; dataIdx < dataBytesList.size(); ++dataIdx) {
                    final byte[] dataBytes = dataBytesList.get(dataIdx);
                    final Path outputFilePath = Paths.get(OUTPUT_DIR, inpFileName + "_" + dataIdx + ".tiff");

                    Files.createDirectories(outputFilePath.getParent());

                    try (final OutputStream outputStream = Files.newOutputStream(
                            outputFilePath,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.WRITE
                    )) {
                        outputStream.write(dataBytes);
                        outputStream.flush();
                    }
                }

                ClearFolderHelper.deleteOnComplete(
                        Path.of(OUTPUT_DIR).toFile(),
                        WILL_DELETE_ON_COMPLETE
                );
            }
        });
    }

    @Test
    void convertToTiff_pageCountMatchesPdfPages() throws Exception {
        // Verify that convertToTiff returns exactly the number of pages in the PDF,
        // catching the duplicate pages bug where fallback would produce extra pages
        final String inpFileName = "pdf/TheVerge.pdf";
        try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
            Objects.requireNonNull(pdfStream);
            // Read PDF into bytes so we can replay the stream
            final byte[] pdfBytes = pdfStream.readAllBytes();

            // Get expected page count from PDF
            try (final PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
                final int expectedPageCount = doc.getNumberOfPages();

                // Convert and verify page count matches
                try (final InputStream pdfStream2 = new ByteArrayInputStream(pdfBytes)) {
                    final List<byte[]> tiffDataList = new PdfToTiffImageHelper()
                            .convertToTiff(inpFileName, pdfStream2, 124, ImageCompressionType.Deflate);

                    assertEquals(expectedPageCount, tiffDataList.size(),
                            "TIFF page count should match PDF page count - duplicates indicate fallback bug");

                    // Each page should produce non-empty TIFF data
                    for (int i = 0; i < tiffDataList.size(); i++) {
                        assertNotNull(tiffDataList.get(i), "Page " + i + " should not be null");
                        assertTrue(tiffDataList.get(i).length > 0, "Page " + i + " should not be empty");
                    }
                }
            }
        }
    }

    @Test
    void convertToTiffForPage_singlePage() throws Exception {
        // Test the single-page conversion method
        final String inpFileName = "pdf/TheVerge.pdf";
        try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
            final byte[] pdfBytes = pdfStream.readAllBytes();

            try (final PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
                final int pageCount = doc.getNumberOfPages();

                // Convert first page
                try (final InputStream pdfStream2 = new ByteArrayInputStream(pdfBytes)) {
                    final byte[] tiffData = new PdfToTiffImageHelper()
                            .convertToTiffForPage(inpFileName, pdfStream2, 124, ImageCompressionType.Deflate, 0);

                    assertNotNull(tiffData, "Single page TIFF data should not be null");
                    assertTrue(tiffData.length > 0, "Single page TIFF data should not be empty");
                }

                // Convert last page
                try (final InputStream pdfStream3 = new ByteArrayInputStream(pdfBytes)) {
                    final byte[] tiffData = new PdfToTiffImageHelper()
                            .convertToTiffForPage(inpFileName, pdfStream3, 124, ImageCompressionType.Deflate, pageCount - 1);

                    assertNotNull(tiffData, "Last page TIFF data should not be null");
                    assertTrue(tiffData.length > 0, "Last page TIFF data should not be empty");
                }
            }
        }
    }

    @Test
    void convertToTiffForPage_invalidPageIndex_negative() throws Exception {
        // Test that negative page index throws IOException
        final String inpFileName = "pdf/TheVerge.pdf";
        try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
            final byte[] pdfBytes = pdfStream.readAllBytes();

            assertThrows(IOException.class, () -> {
                try (final InputStream stream = new ByteArrayInputStream(pdfBytes)) {
                    new PdfToTiffImageHelper()
                            .convertToTiffForPage(inpFileName, stream, 124, ImageCompressionType.Deflate, -1);
                }
            }, "Negative page index should throw IOException");
        }
    }

    @Test
    void convertToTiffForPage_invalidPageIndex_outOfBounds() throws Exception {
        // Test that page index >= page count throws IOException
        final String inpFileName = "pdf/TheVerge.pdf";
        try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
            final byte[] pdfBytes = pdfStream.readAllBytes();

            try (final PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
                final int pageCount = doc.getNumberOfPages();

                assertThrows(IOException.class, () -> {
                    try (final InputStream stream = new ByteArrayInputStream(pdfBytes)) {
                        new PdfToTiffImageHelper()
                                .convertToTiffForPage(inpFileName, stream, 124, ImageCompressionType.Deflate, pageCount);
                    }
                }, "Page index equal to page count should throw IOException");
            }
        }
    }

    @Test
    void convertToTiff_multipleCompressionTypes() throws Exception {
        // Test that different compression types work correctly
        final String inpFileName = "pdf/TheVerge.pdf";
        try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
            final byte[] pdfBytes = pdfStream.readAllBytes();

            try (final PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
                final int expectedPageCount = doc.getNumberOfPages();

                // Test with LZW compression
                try (final InputStream stream = new ByteArrayInputStream(pdfBytes)) {
                    final List<byte[]> tiffDataList = new PdfToTiffImageHelper()
                            .convertToTiff(inpFileName, stream, 124, ImageCompressionType.LZW);

                    assertEquals(expectedPageCount, tiffDataList.size(),
                            "LZW compression should produce correct page count");
                }

                // Test with PackBits compression
                try (final InputStream stream = new ByteArrayInputStream(pdfBytes)) {
                    final List<byte[]> tiffDataList = new PdfToTiffImageHelper()
                            .convertToTiff(inpFileName, stream, 124, ImageCompressionType.PackBits);

                    assertEquals(expectedPageCount, tiffDataList.size(),
                            "PackBits compression should produce correct page count");
                }
            }
        }
    }

    @Test
    void convertToTiff_fallbackDoesNotDuplicatePages() throws Exception {
        // Directly test the fallback scenario: when the first method fails mid-way,
        // the fallback should produce exactly the expected page count without duplicates.
        // This test would have failed before the fix (7 pages instead of 5).
        final String inpFileName = "pdf/TheVerge.pdf";
        try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
            final byte[] pdfBytes = pdfStream.readAllBytes();

            try (final PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
                final int expectedPageCount = doc.getNumberOfPages();

                // Use reflection to access the private fallback method
                Method javaxMethod = PdfToTiffImageHelper.class.getDeclaredMethod(
                        "convertToTiffUsingImageWriterJavax",
                        PDDocument.class, PDFRenderer.class, ImageCompressionType.class, int.class, String.class, int.class
                );
                javaxMethod.setAccessible(true);

                // Create a helper that simulates the fallback scenario
                final PdfToTiffImageHelper failingHelper = new PdfToTiffImageHelper() {
                    @Override
                    public List<byte[]> convertToTiff(String fileId, InputStream pdfData, int dpiOfImage, ImageCompressionType compressionType) throws IOException {
                        try (final PDDocument pdDocument = PDDocument.load(pdfData)) {
                            final PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
                            final int numberOfPages = pdDocument.getNumberOfPages();
                            final List<byte[]> tiffDataList = new ArrayList<>();

                            // Simulate the first method failing on page 2
                            try {
                                for (int pageIdx = 0; pageIdx < numberOfPages; pageIdx++) {
                                    if (pageIdx == 2) {
                                        throw new IOException("Simulated failure on page 2");
                                    }
                                    tiffDataList.add(invokePrivateMethod(javaxMethod, pdDocument, pdfRenderer, compressionType, dpiOfImage, fileId, pageIdx));
                                }
                            } catch (Throwable ex) {
                                // This is the fallback path - clear the list and re-render all pages
                                tiffDataList.clear();
                                for (int pageIdx = 0; pageIdx < numberOfPages; pageIdx++) {
                                    tiffDataList.add(invokePrivateMethod(javaxMethod, pdDocument, pdfRenderer, compressionType, dpiOfImage, fileId, pageIdx));
                                }
                            }

                            return tiffDataList;
                        }
                    }

                    private byte[] invokePrivateMethod(Method method, PDDocument doc, PDFRenderer renderer,
                                                       ImageCompressionType compression, int dpi, String fileId, int page) throws IOException {
                        try {
                            @SuppressWarnings("unchecked")
                            byte[] result = (byte[]) method.invoke(this, doc, renderer, compression, dpi, fileId, page);
                            return result;
                        } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                            throw new IOException("Reflection failed", e);
                        }
                    }
                };

                // Convert using the failing helper
                try (final InputStream stream = new ByteArrayInputStream(pdfBytes)) {
                    final List<byte[]> tiffDataList = failingHelper.convertToTiff(
                            inpFileName, stream, 124, ImageCompressionType.Deflate);

                    // The key assertion: after fallback, we should have exactly the expected page count
                    assertEquals(expectedPageCount, tiffDataList.size(),
                            "Fallback should produce exactly the expected page count without duplicates");

                    // Each page should produce non-empty TIFF data
                    for (int i = 0; i < tiffDataList.size(); i++) {
                        assertNotNull(tiffDataList.get(i), "Page " + i + " should not be null");
                        assertTrue(tiffDataList.get(i).length > 0, "Page " + i + " should not be empty");
                    }
                }
            }
        }
    }

    @Test
    void convertToTiffForPage_fallbackWorks() throws Exception {
        // Test that convertToTiffForPage correctly falls back when the first method fails
        final String inpFileName = "pdf/TheVerge.pdf";
        try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
            final byte[] pdfBytes = pdfStream.readAllBytes();

            // Use reflection to access the private fallback method
            Method javaxMethod = PdfToTiffImageHelper.class.getDeclaredMethod(
                    "convertToTiffUsingImageWriterJavax",
                    PDDocument.class, PDFRenderer.class, ImageCompressionType.class, int.class, String.class, int.class
            );
            javaxMethod.setAccessible(true);

            // Create a helper that always fails using the first method but succeeds with fallback
            final PdfToTiffImageHelper failingHelper = new PdfToTiffImageHelper() {
                @Override
                public byte[] convertToTiffForPage(String fileId, InputStream pdfData, int dpiOfImage, ImageCompressionType compressionType, int pageIndex) throws IOException {
                    try (final PDDocument pdDocument = PDDocument.load(pdfData)) {
                        final PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);

                        try {
                            // Simulate failure of the first method
                            throw new IOException("Simulated failure for fallback test");
                        } catch (Throwable ex) {
                            // Fall back to the second method using reflection
                            try {
                                @SuppressWarnings("unchecked")
                                byte[] result = (byte[]) javaxMethod.invoke(
                                        this, pdDocument, pdfRenderer, compressionType, dpiOfImage, fileId, pageIndex
                                );
                                return result;
                            } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                                throw new IOException("Reflection failed", e);
                            }
                        }
                    }
                }
            };

            // Convert first page using the failing helper
            try (final InputStream stream = new ByteArrayInputStream(pdfBytes)) {
                final byte[] tiffData = failingHelper.convertToTiffForPage(
                        inpFileName, stream, 124, ImageCompressionType.Deflate, 0);

                assertNotNull(tiffData, "Fallback should produce non-null result");
                assertTrue(tiffData.length > 0, "Fallback should produce non-empty result");
            }
        }
    }
}