package org.example.domain.pointacces;

import org.example.domain.interfaces.MapElement;

import java.io.Serializable;

public class PointAcces implements MapElement, Serializable {

    private String ssid;

    private int puissanceEmission = 15;

    private double x, y;

    private Frequence frequence;

    private static final int RADIUS = 2;

    public int getPuissanceEmission() {
        return puissanceEmission;
    }

    public void setPuissanceEmission(int puissanceEmission) {
        this.puissanceEmission = puissanceEmission;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getSsid() {
        return ssid;
    }

    public String getId() {
        return this.getSsid();
    }

    public PointAcces(String ssid, double x, double y, Frequence frequence) {
        this.ssid = ssid;
        this.x = x;
        this.y = y;
        this.frequence = frequence;
    }

    public PointAcces(String ssid, double x, double y, Frequence frequence, int puissanceEmission) {
        this.ssid = ssid;
        this.puissanceEmission = puissanceEmission;
        this.x = x;
        this.y = y;
        this.frequence = frequence;
    }

    @Override
    public boolean contains(double x, double y) {
        double dx = this.x - x;
        double dy = this.y - y;
        return Math.sqrt(dx * dx + dy * dy) <= RADIUS;
    }

    @Override
    public void handleClick() {
        System.out.println("Clicked on PointAcces: " + ssid);
    }

    @Override
    public void update(Object... params) {
        if (params.length != 4) {
            throw new IllegalArgumentException("Expected 4 parameters: x (Double), y (Double), puissance (Integer), frequence (Frequence)");
        }

        updatePosition((Integer) params[0], (Integer) params[1], (Integer) params[2], (Frequence) params[3]);
    }

    private void updatePosition(double x, double y, int puissance, Frequence frequence) {
        this.setX(x);
        this.setY(y);
        this.setPuissanceEmission(puissance);
        this.setFrequence(frequence);
    }

    @Override
    public String getDetails() {

        return "Puissance: " + getPuissanceEmission() + " dBm" +
                " | Fréquence: " + getFrequence();
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public Frequence getFrequence() {
        return frequence;
    }

    public void setFrequence(Frequence frequence) {
        this.frequence = frequence;
    }

}
