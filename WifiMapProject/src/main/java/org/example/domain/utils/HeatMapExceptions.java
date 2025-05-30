package org.example.domain.utils;

public class HeatMapExceptions {

    // Point is out of map range
    public static class PointOutOfMapRangeException extends Exception {
        public PointOutOfMapRangeException(String message) {
            super(message);
        }
    }

    // Map size is invalid (e.g. zero or negative)
    public static class InvalidMapSizeException extends Exception {
        public InvalidMapSizeException(String message) {
            super(message);
        }
    }

    // Barriere already exists
    public static class ExistingBarriereException extends Exception {
        public ExistingBarriereException(String message) {
            super(message);
        }
    }

    // Generic domain error (optional)
    public static class HeatMapException extends Exception {
        public HeatMapException(String message) {
            super(message);
        }
    }
}
