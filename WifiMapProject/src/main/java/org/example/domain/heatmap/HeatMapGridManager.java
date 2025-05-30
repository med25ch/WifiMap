package org.example.domain.heatmap;

import org.example.domain.barrieres.Barriere;
import org.example.domain.heatmap.Particule;
import org.example.domain.pointacces.PointAcces;

import java.util.List;

public class HeatMapGridManager {
    private final HeatMapGrid heatmapGrid;
    private int[][] colorMatrix = null;

    public HeatMapGridManager() {
        heatmapGrid = new HeatMapGrid();
    }

    public Particule getParticuleAt(int x, int y) {
        return heatmapGrid.getParticuleAt(x, y);
    }

    public HeatMapGrid getHeatmapGrid() {
        return heatmapGrid;
    }

    public int[][] getColorMatrix() {
        return colorMatrix;
    }

    public boolean getIsGoodCoverage() {
        return heatmapGrid.getIsGoodCoverage();
    }

    public void calculateSignalForParticules(List<PointAcces> pointAccesListe, List<Barriere> barriereListe, int layoutWidth, int layoutHeight) {
        colorMatrix = heatmapGrid.calculateSignalForParticules(pointAccesListe, barriereListe, layoutWidth, layoutHeight);
    }

    public void initializeGrid(int width, int height) {
        heatmapGrid.initializeParticuleArr(width, height);
    }
}
