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

public interface MapElementFactory {
    Carte createCarte();
    Carte createCarte(Dimension dimension, MapThermalEffect thermalEffect);

    Grille createGrille();
    Grille createGrille(int taille, boolean isMagnetic, boolean isActive);

    PointAcces createPointAcces(String ssid, double x, double y, Frequence frequence);
    PointAcces createPointAcces(String ssid,double x, double y, Frequence frequence,int puissance);

    Particule createParticule();
    Barriere createBarriere(UUID uuid, Material materiel, double largeur, Orientation orientation, int xStart, int yStart, int xEnd, int yEnd);
}
