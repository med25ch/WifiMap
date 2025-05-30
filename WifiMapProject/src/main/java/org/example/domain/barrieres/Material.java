package org.example.domain.barrieres;

public enum Material {
    PLATRE(0.3),
    BOIS(0.5),
    BRIQUE(1.0),
    CIMENT(1.33),
    BETON(1.66),
    METAL(2.0);

    private final double attenuationPerCm;

    Material(double attenuationPerCm) {
        this.attenuationPerCm = attenuationPerCm;
    }

    public double getAttenuationPerCm() {
        return attenuationPerCm;
    }
}
