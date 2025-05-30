package org.example.domain.grille;

public class Grille {
    private int taille;
    private boolean isMagnetic ;
    private boolean isActive ;

    public Grille() {
        this.taille = 20;
        this.isMagnetic = false;
        this.isActive = false;
    }

    public Grille(int taille, boolean isMagnetic, boolean isActive) {
        this.taille = taille;
        this.isMagnetic = isMagnetic;
        this.isActive = isActive;
    }

    public int getTaille() {
        return taille;
    }

    public void setTaille(int taille) {
        this.taille = taille;
    }

    public boolean isMagnetic() {
        return isMagnetic;
    }

    public void setMagnetic(boolean magnetic) {
        isMagnetic = magnetic;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
