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