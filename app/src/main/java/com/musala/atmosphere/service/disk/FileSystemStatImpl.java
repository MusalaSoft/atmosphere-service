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
