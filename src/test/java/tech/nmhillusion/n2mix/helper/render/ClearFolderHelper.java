package tech.nmhillusion.n2mix.helper.render;

import tech.nmhillusion.n2mix.helper.log.LogHelper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * created by: chubb
 * <p>
 * created date: 2023-10-05
 */
public abstract class ClearFolderHelper {
    public static void deleteOnComplete(File file2Delete, boolean enable) throws IOException {
        if (!enable) {
            return;
        }
        final File[] pathList = file2Delete.listFiles();
        if (null != pathList) {
            LogHelper.getLogger(ClearFolderHelper.class).info("list file to delete: %s".formatted(
                    String.join(", ", Arrays.stream(pathList).map(File::getName).toList())
            ));

            for (File pathItem : pathList) {
                deleteOnComplete(pathItem, enable);
            }
        }
        LogHelper.getLogger(ClearFolderHelper.class).info(
                "deleted file: %s -> %s"
                        .formatted(file2Delete, file2Delete.delete())
        );
    }
}
