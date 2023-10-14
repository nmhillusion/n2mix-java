package tech.nmhillusion.n2mix.helper.storage;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-14
 */
class FileHelperTest {
    private static final String folderName_ = "folder_";

    @Test
    void testRecursiveDeleteFolder() throws IOException {
        for (int folIdx = 0; folIdx < 100; ++folIdx) {
            final Path createdFolder_ = Files.createDirectories(
                    Path.of(folderName_, folIdx + "_")
            );

            for (int fileIdx = 0; fileIdx < 89; ++fileIdx) {
                try (final OutputStream fileOS = Files.newOutputStream(
                        Path.of(createdFolder_.toString(), "file_" + fileIdx + ".txt")
                        , StandardOpenOption.CREATE, StandardOpenOption.WRITE)
                ) {
                    final String message_ = "this is content of file: " + Math.random() * 999;
                    fileOS.write(message_.getBytes());
                    fileOS.flush();
                }
            }
        }

        final boolean deleteResult = FileHelper.recursiveDeleteFolder(Path.of(folderName_).toFile());
        getLogger(this).info("delete folder result: " + deleteResult);
    }
}