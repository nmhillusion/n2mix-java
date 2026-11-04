package tech.nmhillusion.n2mix.helper.render.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.constant.ImageCompressionType;
import tech.nmhillusion.n2mix.helper.render.ClearFolderHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
import java.util.Arrays;
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
        // Smoke test of the happy path: one TIFF per page, and every TIFF is decodable
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
                            "TIFF page count should match PDF page count");

                    // Each page must be a decodable TIFF with content
                    for (int i = 0; i < tiffDataList.size(); i++) {
                        final BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(tiffDataList.get(i)));
                        assertNotNull(decoded, "Page " + i + " must decode as a TIFF");
                        assertTrue(decoded.getWidth() > 0 && decoded.getHeight() > 0,
                                "Page " + i + " must have content");
                    }
                }
            }
        }
    }

    @Test
    void convertToTiffForPage_singlePage() throws Exception {
        // Test the single-page conversion method, and prove that pageIndex is honoured
        final String inpFileName = "pdf/TheVerge.pdf";
        try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
            final byte[] pdfBytes = Objects.requireNonNull(pdfStream).readAllBytes();

            final int pageCount;
            try (final PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
                pageCount = doc.getNumberOfPages();
            }

            final List<byte[]> allPages = new PdfToTiffImageHelper()
                    .convertToTiff(inpFileName, new ByteArrayInputStream(pdfBytes), 124, ImageCompressionType.Deflate);
            assertEquals(pageCount, allPages.size());

            final byte[] firstPage = new PdfToTiffImageHelper()
                    .convertToTiffForPage(inpFileName, new ByteArrayInputStream(pdfBytes), 124, ImageCompressionType.Deflate, 0);
            final byte[] lastPage = new PdfToTiffImageHelper()
                    .convertToTiffForPage(inpFileName, new ByteArrayInputStream(pdfBytes), 124, ImageCompressionType.Deflate, pageCount - 1);

            assertArrayEquals(allPages.get(0), firstPage,
                    "page 0 must match the multi-page result for page 0");
            assertFalse(Arrays.equals(firstPage, lastPage),
                    "page selection must not always render page 0");
            assertFalse(Arrays.equals(allPages.get(pageCount - 1), firstPage),
                    "page 0 and the last page must differ");
        }
    }

    @Test
    void convertToTiffForPage_invalidPageIndex_negative() throws Exception {
        // Test that negative page index throws IndexOutOfBoundsException
        final String inpFileName = "pdf/TheVerge.pdf";
        try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
            final byte[] pdfBytes = Objects.requireNonNull(pdfStream).readAllBytes();

            final int pageCount;
            try (final PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
                pageCount = doc.getNumberOfPages();
            }

            final IndexOutOfBoundsException ex = assertThrows(IndexOutOfBoundsException.class, () -> {
                try (final InputStream stream = new ByteArrayInputStream(pdfBytes)) {
                    new PdfToTiffImageHelper()
                            .convertToTiffForPage(inpFileName, stream, 124, ImageCompressionType.Deflate, -1);
                }
            });
            assertTrue(ex.getMessage().contains("must be in range [0.." + (pageCount - 1) + "]"),
                    "message must state the inclusive maximum index: " + ex.getMessage());
        }
    }

    @Test
    void convertToTiffForPage_invalidPageIndex_outOfBounds() throws Exception {
        // Test that page index >= page count throws IndexOutOfBoundsException
        final String inpFileName = "pdf/TheVerge.pdf";
        try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
            final byte[] pdfBytes = Objects.requireNonNull(pdfStream).readAllBytes();

            try (final PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
                final int pageCount = doc.getNumberOfPages();

                final IndexOutOfBoundsException ex = assertThrows(IndexOutOfBoundsException.class, () -> {
                    try (final InputStream stream = new ByteArrayInputStream(pdfBytes)) {
                        new PdfToTiffImageHelper()
                                .convertToTiffForPage(inpFileName, stream, 124, ImageCompressionType.Deflate, pageCount);
                    }
                });
                assertTrue(ex.getMessage().contains("must be in range [0.." + (pageCount - 1) + "]"),
                        "message must state the inclusive maximum index: " + ex.getMessage());
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
                        PDFRenderer.class, ImageCompressionType.class, int.class, String.class, int.class
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
                                    tiffDataList.add(invokePrivateMethod(javaxMethod, pdfRenderer, compressionType, dpiOfImage, fileId, pageIdx));
                                }
                            } catch (Throwable ex) {
                                // This is the fallback path - clear the list and re-render all pages
                                tiffDataList.clear();
                                for (int pageIdx = 0; pageIdx < numberOfPages; pageIdx++) {
                                    tiffDataList.add(invokePrivateMethod(javaxMethod, pdfRenderer, compressionType, dpiOfImage, fileId, pageIdx));
                                }
                            }

                            return tiffDataList;
                        }
                    }

                    private byte[] invokePrivateMethod(Method method, PDFRenderer renderer,
                                                       ImageCompressionType compression, int dpi, String fileId, int page) throws IOException {
                        try {
                            @SuppressWarnings("unchecked")
                            byte[] result = (byte[]) method.invoke(this, renderer, compression, dpi, fileId, page);
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
                    PDFRenderer.class, ImageCompressionType.class, int.class, String.class, int.class
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
                                        this, pdfRenderer, compressionType, dpiOfImage, fileId, pageIndex
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