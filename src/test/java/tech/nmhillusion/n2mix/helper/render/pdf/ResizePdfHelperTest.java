package tech.nmhillusion.n2mix.helper.render.pdf;

import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.helper.render.ClearFolderHelper;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-22
 */
class ResizePdfHelperTest {
    private static final String OUTPUT_DIR = "outputs";
    private static final boolean WILL_DELETE_ON_COMPLETE = false;

    @Test
    void testRender() {
        assertDoesNotThrow(() -> {
            final String inpFileName = "pdf/contract.pdf";
            try (final InputStream pdfStream = getClass().getClassLoader().getResourceAsStream(inpFileName)) {
                final ResizePdfHelper resizePdfHelper = new ResizePdfHelper(595, 842);

                final byte[] renderedData_ = resizePdfHelper.render(pdfStream);

                final Path outputPath_ = Path.of(OUTPUT_DIR, inpFileName);
                Files.createDirectories(outputPath_.getParent());

                try (final OutputStream outputStream_ = Files.newOutputStream(
                        outputPath_
                )) {
                    outputStream_.write(renderedData_);
                    outputStream_.flush();
                }
            }

            ClearFolderHelper.deleteOnComplete(
                    Path.of(OUTPUT_DIR)
                            .toFile(),
                    WILL_DELETE_ON_COMPLETE
            );
        });
    }
}