package org.example.domain.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class HeatMapLog {

    private final Logger logger;

    public HeatMapLog() {
        logger = Logger.getLogger(HeatMapLog.class.getName());
        logger.setUseParentHandlers(false); // Prevents duplicate logs if root logger has handlers

        // ConsoleHandler to log to console
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);  // Log all levels to the console
        consoleHandler.setFormatter(new SimpleFormatter());  // Simple output format
        logger.addHandler(consoleHandler);

        logger.setLevel(Level.ALL);  // Set the default logging level
    }

    // Logging methods (now instance methods)
    public void logInfo(String message) {
        logger.info(message);
    }

    public void logWarning(String message) {
        logger.warning(message);
    }

    public void logSevere(String message) {
        logger.severe(message);
    }

    public void logFine(String message) {
        logger.fine(message);
    }

    public void logFiner(String message) {
        logger.finer(message);
    }

    public void logFinest(String message) {
        logger.finest(message);
    }

    public void logException(Exception e) {
        logger.log(Level.SEVERE, "An exception occurred: ", e);
    }
}
