package com.musala.atmosphere.service.disk;

/**
 * Basic interface for file system statistics.
 *
 * @author yordan.petrov
 *
 */
public interface FileSystemStat {
    /**
     * The size, in bytes, of a block on the file system. This corresponds to the Unix statvfs.f_bsize field.
     *
     * @return the size, in bytes, of a block on the file system
     */
    long getBlockSize();

    /**
     * The number of blocks that are free on the file system and available to applications. This corresponds to the Unix
     * statvfs.f_bavail field.
     *
     * @return the number of blocks that are free on the file system and available to applications
     */
    long getAvailableBlocks();

    /**
     * The total number of blocks on the file system.
     *
     * @return the total number of blocks on the file system
     */
    long getBlockCount();

    /**
     * The total number of blocks that are free on the file system, including reserved blocks (that are not available to
     * normal applications).
     *
     * @return the total number of blocks that are free on the system
     */
    long getFreeBlocks();
}