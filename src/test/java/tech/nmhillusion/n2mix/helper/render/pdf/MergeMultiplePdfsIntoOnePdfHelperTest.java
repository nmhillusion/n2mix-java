package tech.nmhillusion.n2mix.helper.render.pdf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.helper.render.ClearFolderHelper;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-06
 */
class MergeMultiplePdfsIntoOnePdfHelperTest {
    private static final String OUTPUT_DIR = "outputs";
    private static final boolean WILL_DELETE_ON_COMPLETE = true;

    @Test
    void mergePdfPages() {
        Assertions.assertDoesNotThrow(() -> {
            final String outputFileName = "GoogleDoodlesAndVerge.pdf";
            final List<String> inputFileNames = List.of("pdf/GoogleDoodles.pdf", "pdf/TheVerge.pdf");
            final List<InputStream> inputFileStreamList = new ArrayList<>();
            try {
                inputFileNames.forEach(f_ -> inputFileStreamList.add(
                        getClass().getClassLoader().getResourceAsStream(f_)
                ));

                final MergeMultiplePdfsIntoOnePdfHelper mergePdfIntoOnePageHelper = new MergeMultiplePdfsIntoOnePdfHelper();
                final byte[] mergePdfPagesData = mergePdfIntoOnePageHelper.mergePdfPages(inputFileStreamList);

                final Path outputFilePath = Path.of(OUTPUT_DIR, outputFileName);
                Files.createDirectories(outputFilePath.getParent());

                try (final OutputStream outputStream_ = Files.newOutputStream(outputFilePath
                        , StandardOpenOption.CREATE
                        , StandardOpenOption.WRITE)) {
                    outputStream_.write(mergePdfPagesData);
                    outputStream_.flush();
                }
            } finally {
                for (InputStream inputStream_ : inputFileStreamList) {
                    inputStream_.close();
                }
            }

            ClearFolderHelper
                    .deleteOnComplete(
                            Path.of(OUTPUT_DIR).toFile()
                            , WILL_DELETE_ON_COMPLETE
                    );
        });
    }
}