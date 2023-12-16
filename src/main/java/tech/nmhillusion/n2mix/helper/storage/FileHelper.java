package tech.nmhillusion.n2mix.helper.storage;

import tech.nmhillusion.n2mix.util.StringUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-14
 */
public abstract class FileHelper {
    public static boolean recursiveDeleteFolder(File folderToDelete) {
        if (null == folderToDelete) {
            return true;
        }
        final File[] allContents = folderToDelete.listFiles();
        if (null != allContents) {
            for (final File subFile : allContents) {
                recursiveDeleteFolder(subFile);
            }
        }
        return folderToDelete.delete();
    }

    public static String convertToUnixPath(String originalPath) {
        String convertedPath = StringUtil.trimWithNull(originalPath);
        convertedPath = convertedPath.replaceAll("\\\\", "/");

        return convertedPath;
    }

    public static Optional<String> getFileExtensionFromName(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> !StringValidator.isBlank(f) &&
                        f.contains(".") &&
                        0 < fileName.lastIndexOf(".") &&
                        fileName.lastIndexOf(".") < fileName.length() - 1
                )
                .filter(f -> {
                    final String unixPath = convertToUnixPath(f);
                    return !unixPath.contains("/") || unixPath.lastIndexOf("/") < unixPath.lastIndexOf(".");
                })
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1))
                ;
    }

    public static String getContentTypeOfFile(Path filePath) throws IOException {
        final AtomicReference<String> contentType_ = new AtomicReference<>("application/octet-stream");

        if (null != filePath) {
            final Optional<String> extNameOpt_ = getFileExtensionFromName(
                    filePath.getFileName().toString()
            );

            if (extNameOpt_.isPresent()) {
                final String extName = extNameOpt_.get();

                if ("log".equalsIgnoreCase(extName)) {
                    contentType_.set("text/plain;charset=utf-8");
                } else {
                    final String probedContentType = Files.probeContentType(filePath);
                    if (!StringValidator.isBlank(probedContentType)) {
                        contentType_.set(probedContentType);
                    }
                }
            }
        }

        return contentType_.get();
    }
}
