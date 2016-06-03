package com.musala.atmosphere.service.disk;

import android.os.Build;
import android.os.StatFs;

/**
 * A {@link FileSystemStat} factory that returns an implementation compatible with the current API level.
 *
 * @author yordan.petrov
 *
 */
public class FileSystemStatFactory {
    private static final boolean IS_LEGACY = Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2;

    /**
     * Returns an implementation of {@link FileSystemStat} compatible with the current API level.
     *
     * @param stats
     *        - a {@link StatFs} object that will be used to obtain the information
     * @return an implementation of {@link FileSystemStat} compatible with the current API level
     */
    public static FileSystemStat getFileSystemStat(StatFs stats) {
        return IS_LEGACY ? new FileSystemStatCompat(stats) : new FileSystemStatImpl(stats);
    }
}
