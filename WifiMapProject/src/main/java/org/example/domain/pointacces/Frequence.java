package org.example.domain.pointacces;

public enum Frequence {

    GHz2_4(2.4),
    GHz5(5.0);

    private final double valeur;

    Frequence(double valeur) {
        this.valeur = valeur;
    }

    public double getValeur() {
        return this.valeur;
    }


    @Override
    public String toString() {
        return valeur + " Ghz";
    }
}