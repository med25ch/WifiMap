package org.example.ui.heatmap;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HeatMap {
    private BufferedImage heatmap;
    private int width, height;
    private int cellSize = 1;

    public HeatMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void generateHeatmap(int[][] colorMatrix) {
        heatmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = heatmap.createGraphics();

        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.SrcOver);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g2d.setColor(new Color(colorMatrix[x][y], true));
                g2d.fillRect(x, y, cellSize, cellSize);
            }
        }

        g2d.dispose();
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public BufferedImage getHeatmap() {
        return heatmap;
    }
}
