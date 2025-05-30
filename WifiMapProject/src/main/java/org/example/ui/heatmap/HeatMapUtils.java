package org.example.ui.heatmap;

import javax.swing.*;
import java.awt.*;

public class HeatMapUtils {

    public static Color getColorForDbm(double value) {
        if (value == Integer.MIN_VALUE) return getDefaultColor();
        value = Math.max(-100, Math.min(-20, value));

        double normalized = (value + 100) / 80.0;

        return interpolateHeatmap(normalized);
    }

    private static Color getDefaultColor() {
        Color background = UIManager.getColor("Panel.background");
        return background != null ? background : Color.WHITE;
    }

    private static Color interpolateHeatmap(double t) {
        double[][] colors = {
                {0, 0, 255},
                {0, 165, 255},
                {0, 255, 255},
                {255, 255, 0},
                {255, 165, 0},
                {255, 0, 0},
        };

        int numColors = colors.length - 1;
        double scaledT = t * numColors;
        int index = (int) Math.floor(scaledT);
        double fraction = scaledT - index;

        if (index >= numColors) return new Color((int) colors[numColors][0], (int) colors[numColors][1], (int) colors[numColors][2]);
        if (index < 0) return new Color((int) colors[0][0], (int) colors[0][1], (int) colors[0][2]);

        int r = (int) (colors[index][0] + fraction * (colors[index + 1][0] - colors[index][0]));
        int g = (int) (colors[index][1] + fraction * (colors[index + 1][1] - colors[index][1]));
        int b = (int) (colors[index][2] + fraction * (colors[index + 1][2] - colors[index][2]));

        return new Color(r, g, b);
    }
}
