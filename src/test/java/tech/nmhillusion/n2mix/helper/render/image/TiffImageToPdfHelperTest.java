package tech.nmhillusion.n2mix.helper.render.image;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.helper.render.ClearFolderHelper;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * created by: chubb
 * <p>
 * created date: 2023-10-05
 */
class TiffImageToPdfHelperTest {

    private static final String OUTPUT_DIR = "outputs";
    private static final boolean WILL_DELETE_ON_COMPLETE = true;

    @Test
    void renderFirstPageOfTiffImage() {

        Assertions.assertDoesNotThrow(() -> {
            final String inputFile_ = "tiff_/TheVerge.tiff";

            final Path outputDirPath = Path.of(OUTPUT_DIR);
            try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(inputFile_)) {
                final TiffImageToPdfHelper tiffImageToPdfHelper = new TiffImageToPdfHelper();
                final byte[] pdfData = tiffImageToPdfHelper.renderFirstPageOfTiffImage(inputStream);

                final String fileName = Path.of(inputFile_).getFileName().toString()
                        .replaceFirst("(?i)\\.tiff$", ".pdf");

                Files.createDirectories(outputDirPath);
                try (final OutputStream outputStream = Files.newOutputStream(
                        Paths.get(OUTPUT_DIR, fileName),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE)
                ) {
                    outputStream.write(pdfData);
                    outputStream.flush();
                }
            }

            ClearFolderHelper.deleteOnComplete(
                    outputDirPath.toFile(),
                    WILL_DELETE_ON_COMPLETE
            );
        });

    }
}