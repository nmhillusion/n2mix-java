package tech.nmhillusion.n2mix.helper.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

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

    @Test
    void testConvertToUnixPath() {
        Assertions.assertEquals(
                "/working/data/office",
                FileHelper.convertToUnixPath("\\working\\data\\office")
        );

        Assertions.assertEquals(
                "/working/data/office/",
                FileHelper.convertToUnixPath("\\working\\data\\office\\")
        );

        Assertions.assertEquals(
                "d:/working/data/office/",
                FileHelper.convertToUnixPath("d:\\working\\data\\office\\")
        );

        Assertions.assertEquals(
                "/",
                FileHelper.convertToUnixPath("\\")
        );

        Assertions.assertEquals(
                "d:/",
                FileHelper.convertToUnixPath("d:\\")
        );
    }

    @Test
    void testGetFileExtensionFromName() {
        Assertions.assertEquals(
                Optional.empty(),
                FileHelper.getFileExtensionFromName("d:/")
        );

        Assertions.assertEquals(
                Optional.empty(),
                FileHelper.getFileExtensionFromName("d:/abc")
        );

        Assertions.assertEquals(
                Optional.empty(),
                FileHelper.getFileExtensionFromName("d:/abc/data.")
        );

        Assertions.assertEquals(
                Optional.empty(),
                FileHelper.getFileExtensionFromName(".exe")
        );

        Assertions.assertEquals(
                Optional.empty(),
                FileHelper.getFileExtensionFromName("./home/data.txt/io")
        );

        Assertions.assertEquals(
                Optional.of("txt"),
                FileHelper.getFileExtensionFromName("d:/abc/data.txt")
        );

        Assertions.assertEquals(
                Optional.of("jpeg"),
                FileHelper.getFileExtensionFromName("d:/abc/data.jpeg")
        );

        Assertions.assertEquals(
                Optional.of("exe"),
                FileHelper.getFileExtensionFromName("data.exe")
        );

        Assertions.assertEquals(
                Optional.of("txt"),
                FileHelper.getFileExtensionFromName("./home/data.txt")
        );

        Assertions.assertEquals(
                Optional.of("txt"),
                FileHelper.getFileExtensionFromName("./home/.data.txt")
        );

        Assertions.assertEquals(
                Optional.of("bashrc"),
                FileHelper.getFileExtensionFromName("./home/john/.bashrc")
        );

        Assertions.assertEquals(
                Optional.of("sh"),
                FileHelper.getFileExtensionFromName("./home/john/.bashrc.sh")
        );
    }

    @Test
    void testGetContentTypeOfFile() {
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertEquals(
                    MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    FileHelper.getContentTypeOfFile(
                            Path.of("./home/john/.bashrc")
                    )
            );

            Assertions.assertEquals(
                    MediaType.TEXT_PLAIN_VALUE,
                    FileHelper.getContentTypeOfFile(
                            Path.of("./home/data.txt")
                    )
            );

            Assertions.assertEquals(
                    MediaType.TEXT_XML_VALUE,
                    FileHelper.getContentTypeOfFile(
                            Path.of("./home/data.xml")
                    )
            );

            Assertions.assertEquals(
                    MediaType.TEXT_PLAIN_VALUE.contains("charset") ? MediaType.TEXT_PLAIN_VALUE : MediaType.TEXT_PLAIN_VALUE + ";charset=utf-8",
                    FileHelper.getContentTypeOfFile(
                            Path.of("./home/data.log")
                    )
            );

            Assertions.assertEquals(
                    MediaType.APPLICATION_PDF_VALUE,
                    FileHelper.getContentTypeOfFile(
                            Path.of("./home/data.pdf")
                    )
            );

            Assertions.assertEquals(
                    MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    FileHelper.getContentTypeOfFile(
                            Path.of("./home/data.dat")
                    )
            );
        });
    }
}