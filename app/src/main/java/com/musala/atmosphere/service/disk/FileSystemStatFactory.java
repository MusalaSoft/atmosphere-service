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
