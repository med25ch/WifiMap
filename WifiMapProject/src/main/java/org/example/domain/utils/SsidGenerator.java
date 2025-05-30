package org.example.domain.utils;

import java.util.Random;

public class SsidGenerator {
    private static final String PREFIX = "PA";
    private static final Random random = new Random();

    public static String generateSsid() {
        int number = 1000 + random.nextInt(9000); // generates number between 1000 and 9999
        return PREFIX + number;
    }
}
