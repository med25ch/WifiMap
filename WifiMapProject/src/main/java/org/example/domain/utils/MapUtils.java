package org.example.domain.utils;

import org.example.domain.barrieres.Orientation;

import java.awt.*;
import java.util.UUID;

public class MapUtils {
    public static String RandomUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static int roundBasedOnFraction(double d) {
        // Check the fractional part of the number
        if (d - Math.floor(d) >= 0.5) {
            return (int) Math.ceil(d); // Round up if fractional part >= 0.5
        } else {
            return (int) Math.floor(d); // Round down if fractional part < 0.5
        }
    }

    public static double calculerPuissanceSignal(double txPowerDbm, double freqGHz, double x1, double y1, double x2, double y2) {
        double c = 3e8; // vitesse de la lumière
        double freqHz = freqGHz * 1e9;
        double lambda = c / freqHz;
        double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        if (d < 1) d = 1; // éviter log(0)
        return txPowerDbm + 20 * Math.log10(lambda / (4 * Math.PI * d));
    }

    public static Point getInteriorLine(Point from, Point to) {
        int dx = to.x - from.x;
        int dy = to.y - from.y;

        int stepX = Integer.compare(dx, 0);
        int stepY = Integer.compare(dy, 0);
        return new Point(from.x + stepX, from.y + stepY);
    }

    public static Orientation getOrientation(int x1, int y1, int x2, int y2) {
        if (y1 == y2) {
            return Orientation.HORIZONTAL;
        } else if (x1 == x2) {
            return Orientation.VERTICAL;
        } else {
            throw new IllegalArgumentException("The line is neither perfectly horizontal nor vertical.");
        }
    }

}
