package com.musala.atmosphere.service.disk;

import android.os.StatFs;

/**
 * A {@link FileSystemStat} implementation <b>compatible only with API 18 and above</b>.
 *
 * @author yordan.petrov
 *
 */
public class FileSystemStatImpl implements FileSystemStat {
    private StatFs statistics;

    /**
     * Constructs a new {@link FileSystemStatImpl} object wrapping and {@link StatFs} object.
     *
     * @param stats
     *        - the {@link StatFs} object to be wrapped
     */
    public FileSystemStatImpl(StatFs stats) {
        this.statistics = stats;
    }

    @Override
    public long getBlockSize() {
        return statistics.getBlockSizeLong();
    }

    @Override
    public long getAvailableBlocks() {
        return statistics.getAvailableBlocksLong();
    }

    @Override
    public long getBlockCount() {
        return statistics.getBlockCountLong();
    }

    @Override
    public long getFreeBlocks() {
        return statistics.getFreeBlocksLong();
    }
}
