package com.musala.atmosphere.service.disk;

import android.os.StatFs;

/**
 * A {@link FileSystemStat} implementation <b>compatible only with API 17 and below</b>.
 *
 * @author yordan.petrov
 *
 */
@SuppressWarnings("deprecation")
public class FileSystemStatCompat implements FileSystemStat {
    private StatFs statistics;

    /**
     * Constructs a new {@link FileSystemStatCompat} object wrapping and {@link StatFs} object.
     *
     * @param stats
     *        - the {@link StatFs} object to be wrapped
     */
    public FileSystemStatCompat(StatFs stats) {
        this.statistics = stats;
    }

    @Override
    public long getBlockSize() {
        return statistics.getBlockSize();
    }

    @Override
    public long getAvailableBlocks() {
        return statistics.getAvailableBlocks();
    }

    @Override
    public long getBlockCount() {
        return statistics.getBlockCount();
    }

    @Override
    public long getFreeBlocks() {
        return statistics.getFreeBlocks();
    }
}
