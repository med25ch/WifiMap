package org.example.domain.barrieres;

import org.example.domain.interfaces.MapElement;

import java.io.Serializable;
import java.util.UUID;

public class Barriere implements MapElement, Serializable {
    private UUID uuid;
    private Material materiel;
    private Orientation orientation;
    private double largeur; // Largeur in cm
    private int xStart, yStart, xEnd, yEnd;

    public void setxStart(int xStart) {
        this.xStart = xStart;
    }

    public void setyStart(int yStart) {
        this.yStart = yStart;
    }

    public void setxEnd(int xEnd) {
        this.xEnd = xEnd;
    }

    public void setyEnd(int yEnd) {
        this.yEnd = yEnd;
    }

    private double attenuationDB;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return this.getUuid().toString();
    }

    public Barriere(Material materiel, double largeur, Orientation orientation,int xStart, int yStart, int xEnd, int yEnd) {
        this.materiel = materiel;
        this.largeur = largeur;
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.uuid = UUID.randomUUID();
        this.orientation = orientation;
        calculateAttenuation();
    }

    public Barriere(UUID uuid, Material materiel, double largeur, Orientation orientation,int xStart, int yStart, int xEnd, int yEnd) {
        if (uuid == null) uuid = UUID.randomUUID();
        this.materiel = materiel;
        this.largeur = largeur;
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.uuid = uuid;
        this.orientation = orientation;
        calculateAttenuation();
    }

    // Calculate attenuation based on material and width
    private void calculateAttenuation() {
        attenuationDB = largeur * materiel.getAttenuationPerCm();
    }

    // Display barrier information
    public String afficherInformations() {
        return "Matériel: " + materiel + ", Largeur: " + largeur + " cm, Atténuation: " + attenuationDB + " dB";
    }

    // Getters
    public Material getMateriel() { return materiel; }
    public double getLargeur() { return largeur; }
    public int getXStart() { return xStart; }
    public int getYStart() { return yStart; }
    public int getXEnd() { return xEnd; }
    public int getYEnd() { return yEnd; }
    public double getAttenuationDB() { return attenuationDB; }
    public Orientation getOrientation() { return orientation; }

    // Setters
    public void setMateriel(Material materiel) {
        this.materiel = materiel;
        calculateAttenuation();
    }

    public void setLargeur(double largeur) {
        this.largeur = largeur;
        calculateAttenuation();
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public void setPosition(int xStart, int yStart, int xEnd, int yEnd) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
    }

    @Override
    public boolean contains(double x, double y) {
        double threshold = 1.0;
        return isPointNearLine(x, y, xStart, yStart, xEnd, yEnd, threshold);
    }

    @Override
    public void handleClick() {
        System.out.println("Clicked on a Barriere at: " + xStart + "," + yStart);
    }

    @Override
    public void update(Object... params) {
        if (params.length != 7) {
            throw new IllegalArgumentException("Expected 7 parameters: xStart (Integer), yStart (Integer), xEnd (Integer), yEnd (Integer), materiel (Material), orientation (Orientation), largeur (Double)");
        }


        try {
            updateBarriere((Integer) params[0],(Integer) params[1],(Integer) params[2],(Integer) params[3],(Material) params[4],(Orientation) params[5],(Double) params[6]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateBarriere(int xStart, int yStart, int xEnd, int yEnd, Material materiel, Orientation orientation, double largeur) {
        this.setxStart(xStart);
        this.setyStart(yStart);
        this.setxEnd(xEnd);
        this.setyEnd(yEnd);
        this.setMateriel(materiel);
        this.setOrientation(orientation);
        this.setLargeur(largeur);
    }

    @Override
    public String getDetails() {
        return "Matériau: " + getMateriel() +
                " | Largeur: " + getLargeur() + " cm" +
                " | Orientation: " + getOrientation();
    }

    private boolean isPointNearLine(double px, double py, double x1, double y1, double x2, double y2, double threshold) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = len_sq != 0 ? dot / len_sq : -1;

        double xx, yy;
        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy) <= threshold;
    }
}
