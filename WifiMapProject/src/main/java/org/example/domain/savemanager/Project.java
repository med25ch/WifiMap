package org.example.domain.savemanager;
import org.example.domain.barrieres.Barriere;
import org.example.domain.pointacces.PointAcces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private List<Barriere> barrieres;
    private List<PointAcces> pointAccesList;
    private int width;
    private int height;
    public Project(List<Barriere> barrieres, List<PointAcces> pointAccesList, int width, int height) {
        this.barrieres = barrieres != null ? new ArrayList<>(barrieres) : null;
        this.pointAccesList = pointAccesList != null ? new ArrayList<>(pointAccesList) : null;
        this.width = width;
        this.height = height;
    }

    public List<Barriere> getBarrieres() {
        return barrieres;
    }

    public List<PointAcces> getPointAccesList() {
        return pointAccesList;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
