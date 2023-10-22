package tech.nmhillusion.n2mix.helper.storage;

import java.io.File;

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
}
