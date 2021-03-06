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

package com.musala.atmosphere.service.logger;

import org.apache.log4j.Level;

import android.content.Context;
import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Call {@link #configure()} from the application's activity or service.
 * 
 * @see <a href="https://code.google.com/p/android-logging-log4j/">https://code.google.com/p/android-logging-log4j/</a>
 * 
 * @author yordan.petrov
 */
public class Log4JConfigurator {
    private static final String LOGGER_NAME = "org.apache";

    private static final String LOG_FILE = "service.log";

    /**
     * Configures log4j to use an Android appender.
     * 
     * @param context
     *        - the context of the application or service
     */
    public static void configure(Context context) {
        final LogConfigurator logConfigurator = new LogConfigurator();

        String logfilePath = context.getFilesDir() + LOG_FILE;
        logConfigurator.setFileName(logfilePath);
        logConfigurator.setRootLevel(Level.DEBUG);
        // Set log level of a specific logger
        logConfigurator.setLevel(LOGGER_NAME, Level.ERROR);
        logConfigurator.configure();
    }
}
