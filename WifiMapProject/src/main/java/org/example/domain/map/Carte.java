package org.example.domain.map;

import java.awt.*;

public class Carte {

    private Dimension dimension;
    private MapThermalEffect thermalEffect;

    public MapThermalEffect getThermalEffect() {
        return thermalEffect;
    }

    public void setThermalEffect(MapThermalEffect thermalEffect) {
        this.thermalEffect = thermalEffect;
    }

    public Carte() {
        dimension = new Dimension(500, 500);
        thermalEffect = MapThermalEffect.NORMAL;
    }

    public Carte(Dimension dimension, MapThermalEffect thermalEffect) {
        this.dimension = dimension;
        this.thermalEffect = thermalEffect;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

}
