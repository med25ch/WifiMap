package org.example.domain.elementsfactory;

import org.example.domain.barrieres.Barriere;
import org.example.domain.barrieres.Material;
import org.example.domain.barrieres.Orientation;
import org.example.domain.grille.Grille;
import org.example.domain.heatmap.Particule;
import org.example.domain.map.Carte;
import org.example.domain.map.MapThermalEffect;
import org.example.domain.pointacces.Frequence;
import org.example.domain.pointacces.PointAcces;

import java.awt.*;
import java.util.UUID;

public class ConcreteMapElementFactory implements MapElementFactory {

    @Override
    public Carte createCarte() {
        return new Carte(); // Default Carte
    }

    @Override
    public Carte createCarte(Dimension dimension, MapThermalEffect thermalEffect) {
        return new Carte(dimension, thermalEffect);
    }

    @Override
    public Grille createGrille() {
        return new Grille(); // Default Grille
    }

    @Override
    public Grille createGrille(int taille, boolean isMagnetic, boolean isActive) {
        return new Grille(taille, isMagnetic, isActive);
    }

    @Override
    public PointAcces createPointAcces(String ssid, double x, double y, Frequence frequence) {
        return new PointAcces(ssid, x, y, frequence);
    }

    @Override
    public PointAcces createPointAcces(String ssid,double x, double y, Frequence frequence, int puissance) {
        return new PointAcces(ssid, x,y,frequence,puissance);
    }

    @Override
    public Barriere createBarriere(UUID uuid, Material materiel, double largeur, Orientation orientation, int xStart, int yStart, int xEnd, int yEnd) {
        return new Barriere(uuid, materiel, largeur, orientation, xStart, yStart, xEnd, yEnd);
    }

    @Override
    public Particule createParticule() {
        return new Particule();
    }

}
