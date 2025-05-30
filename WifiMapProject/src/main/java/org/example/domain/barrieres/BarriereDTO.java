package org.example.domain.barrieres;

import java.util.UUID;

public record BarriereDTO(UUID uuid, Material materiel, double largeur, Orientation orientation, int xStart, int yStart, int xEnd, int yEnd) { }
