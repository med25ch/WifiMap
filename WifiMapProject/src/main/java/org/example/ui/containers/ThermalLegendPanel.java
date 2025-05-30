package org.example.ui.containers;

import javax.swing.*;
import java.awt.*;

import static org.example.ui.heatmap.HeatMapUtils.getColorForDbm;

public class ThermalLegendPanel extends JPanel {

    public ThermalLegendPanel() {
        setPreferredSize(new Dimension(400, 60));
        setOpaque(true); // Pour bien démarquer le fond
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = 280;
        int height = 20;
        int x = 20;
        int y = 10;

        for (int i = 0; i < width; i++) {
            double ratio = (double) i / width;
            int dBm = (int) (-20 - ratio * 80);
            Color color = getColorForDbm(dBm);
            g2d.setColor(color);
            g2d.drawLine(x + i, y, x + i, y + height);
        }

        g2d.setColor(Color.BLACK);
        g2d.drawString("-100", getXForDbm(-100, x, width), y + height + 15);
        g2d.drawString("-80", getXForDbm(-80, x, width), y + height + 15);
        g2d.drawString("-60", getXForDbm(-60, x, width), y + height + 15);
        g2d.drawString("-40", getXForDbm(-40, x, width), y + height + 15);
        g2d.drawString("-20", getXForDbm(-20, x, width), y + height + 15);

    }

    private int getXForDbm(int dBm, int x, int width) {
        double ratio = (double) (dBm + 20) / -80;
        return (x + (int) (ratio * width)) - 10; // -10 pour les centrer
    }

}
