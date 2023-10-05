package tech.nmhillusion.n2mix.helper.render;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-06
 */
class SplitPdfToEachPageFileHelperTest {
    private static final String OUTPUT_DIR = "outputs";
    private static final boolean WILL_DELETE_ON_COMPLETE = true;

    @Test
    void splitData() {
        Assertions.assertDoesNotThrow(() -> {
            final String inputFile_ = "pdf/TheVerge.pdf";
            try (final InputStream inputStream_ = getClass().getClassLoader().getResourceAsStream(inputFile_)) {
                final SplitPdfToEachPageFileHelper splitPdfToEachPageFileHelper = new SplitPdfToEachPageFileHelper();
                final List<byte[]> pdfPagesData = splitPdfToEachPageFileHelper
                        .splitData(inputStream_);
                final int pagesCount = pdfPagesData.size();

                for (int pageIdx = 0; pageIdx < pagesCount; ++pageIdx) {
                    final String fileName = Path.of(inputFile_).getFileName().toString();
                    final String outputFileName = "%s_page_%s.pdf".formatted(fileName, pageIdx);
                    final Path outputFilePath = Path.of(OUTPUT_DIR, outputFileName);

                    Files.createDirectories(outputFilePath.getParent());

                    try (final OutputStream outputStream_ = Files.newOutputStream(outputFilePath
                            , StandardOpenOption.CREATE
                            , StandardOpenOption.WRITE)) {
                        outputStream_.write(pdfPagesData.get(pageIdx));
                        outputStream_.flush();
                    }
                }
            }

            ClearFolderHelper.deleteOnComplete(
                    Path.of(OUTPUT_DIR).toFile()
                    , WILL_DELETE_ON_COMPLETE
            );
        });
    }
}