package app.netlify.nmhillusion.n2mix.helper.render;

import app.netlify.nmhillusion.n2mix.constant.ImageCompressionType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import static app.netlify.nmhillusion.n2mix.helper.log.LogHelper.getLogger;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
                    }
                }

                deleteOnComplete(
                        Path.of(OUTPUT_DIR).toFile(),
                        WILL_DELETE_ON_COMPLETE
                );
            }
        });
    }

    private void deleteOnComplete(File file2Delete, boolean enable) throws IOException {
        if (!enable) {
            return;
        }
        final File[] pathList = file2Delete.listFiles();
        if (null != pathList) {
            getLogger(this).info("list file to delete: %s".formatted(
                    String.join(", ", Arrays.stream(pathList).map(File::getName).toList())
            ));

            for (File pathItem : pathList) {
                deleteOnComplete(pathItem, enable);
            }
        }
        getLogger(this).info(
                "deleted file: %s -> %s"
                        .formatted(file2Delete, file2Delete.delete())
        );
    }
}