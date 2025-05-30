package org.example.domain.heatmap;

import org.example.domain.barrieres.Barriere;
import org.example.domain.heatmap.Particule;
import org.example.domain.pointacces.Frequence;
import org.example.domain.pointacces.PointAcces;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.geom.Line2D.linesIntersect;
import static org.example.ui.heatmap.HeatMapUtils.getColorForDbm;

public class HeatMapGrid {
    private final List<List<Particule>> particules = new ArrayList<>();
    private final int BARRIER_Y_OFFSET = 1;
    private int[][] colorMatrix;
    private boolean isGoodCoverage = false;
    int cellSize = 1;

    public void initializeParticuleArr(int width, int height) {
        particules.clear();

        for (int x = 0; x < width; x++) {
            particules.add(new ArrayList<>());

            for (int y = 0; y < height; y++) {
                particules.get(x).add(new Particule());
            }
        }
    }

    public Particule getParticuleAt(int x, int y) {
        try {
            return particules.get(x).get(y);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int[][] calculateSignalForParticules(List<PointAcces> pointAccesListe, List<Barriere> barriereListe, int layoutWidth, int layoutHeight) {
        int[][] colorMatrix = new int[layoutWidth][layoutHeight];
        isGoodCoverage = true;
        for (int x = 0; x < layoutWidth; x += cellSize) {
            for (int y = 0; y < layoutHeight; y += cellSize) {
                Particule particule = particules.get(x).get(y);
                double maxSignalStrength = pointAccesListe.isEmpty() ? Integer.MIN_VALUE : -100;

                for (PointAcces pa : pointAccesListe) {

                    double distance = Math.sqrt(Math.pow(x - pa.getX(), 2) + Math.pow(y - pa.getY(), 2));

                    double signalStrength = calculateReceivedPower(pa.getPuissanceEmission(), pa.getFrequence(), distance);

                    // Parfois si on "cross" par le même X,Y 2 fois (surtout le même start ou end) on va avoir un double d'atténuation
                    // Cela mènera à une ligne droite atténuée. On gère ça ici.
                    Map<Point, Double> maxAttenuationMap = new HashMap<>();

                    for (Barriere barrier : barriereListe) {
                        if (doesSignalCrossBarriere(pa.getX(), pa.getY(), x, y, barrier)) {
                            Point barrierPoint = null;

                            if (doesSignalPassThroughPoint(pa.getX(), pa.getY(), x, y, barrier.getXStart(), barrier.getYStart() - BARRIER_Y_OFFSET)) {
                                barrierPoint = new Point(barrier.getXStart(), barrier.getYStart() - BARRIER_Y_OFFSET);
                            } else if (doesSignalPassThroughPoint(pa.getX(), pa.getY(), x, y, barrier.getXEnd(), barrier.getYEnd() - BARRIER_Y_OFFSET)) {
                                barrierPoint = new Point(barrier.getXEnd(), barrier.getYEnd() - BARRIER_Y_OFFSET);
                            }

                            if (barrierPoint != null) {
                                maxAttenuationMap.compute(barrierPoint, (k, v) -> v == null ? barrier.getAttenuationDB() : Math.max(v, barrier.getAttenuationDB()));
                            } else {
                                signalStrength -= barrier.getAttenuationDB();
                            }
                        }
                    }

                    for (double attenuation : maxAttenuationMap.values()) {
                        signalStrength -= attenuation;
                    }

                    if (signalStrength > maxSignalStrength) {
                        maxSignalStrength = signalStrength;
                    }


                }
                Color heatmapColor = getColorForDbm(maxSignalStrength);
                colorMatrix[x][y] = heatmapColor.getRGB();
                particule.setColor(heatmapColor);
                particule.setDbm((int) Math.min(-20, Math.round(maxSignalStrength)));
                if (particule.getDbm() <= -60) isGoodCoverage = false;
            }
        }
        this.colorMatrix = colorMatrix;
        return this.colorMatrix;
    }

    public boolean getIsGoodCoverage() {
        return isGoodCoverage;
    }

    private double calculateReceivedPower(double transmittedPowerDbm, Frequence freq, double distanceInMetersFromTransmitter) {
        double lambda = 0.125;
        if (freq == Frequence.GHz5) {
            lambda = 0.06;
        }
        return transmittedPowerDbm + 20 * Math.log10(lambda / (4 * Math.PI * distanceInMetersFromTransmitter));
    }

    private boolean doesSignalCrossBarriere(double x1, double y1, double x2, double y2, Barriere barrier) {
        return linesIntersect(x1, y1, x2, y2, barrier.getXStart(), barrier.getYStart() - BARRIER_Y_OFFSET, barrier.getXEnd(), barrier.getYEnd() - BARRIER_Y_OFFSET);
    }

    private boolean doesSignalPassThroughPoint(double x1, double y1, double x2, double y2, double x3, double y3) {
        return linesIntersect(x1, y1, x2, y2, x3, y3, x3, y3);
    }

}
