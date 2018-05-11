// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

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
