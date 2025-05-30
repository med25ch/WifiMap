package org.example.domain.heatmap;
import java.awt.*;

import static org.example.ui.heatmap.HeatMapUtils.getColorForDbm;

public class Particule {
    private Color color;
    private int dbm = Integer.MIN_VALUE;
    private String qualite;

    public Particule() {
        this.color = getColorForDbm((int)this.dbm);
    }

    public String getQualite() {

        if (dbm > -30) {
            qualite = "Excellent";
        } else if (dbm > -40) {
            qualite = "Très bon";
        } else if (dbm > -60) {
            qualite = "Bon";
        } else if (dbm > -70) {
            qualite = "Moyenne";
        } else if (dbm > -80){
            qualite = "Faible";
        } else if (dbm > -90) {
            qualite = "Très faible";
        } else if (dbm != Integer.MIN_VALUE){
            qualite = "Hors de pôrtée";
        } else {
            qualite = "N/D";
        }
        return qualite;
    }

    public void setQualite(String qualite) {
        this.qualite = qualite;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public int getDbm() {
        return this.dbm;
    }

    public void setDbm(int dbm) {
        this.dbm = dbm;
    }
}
