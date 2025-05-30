package org.example.domain.pointacces;

public record PointAccesDTO(
        String ssid,
        double x,
        double y,
        Frequence frequence,
        int puissanceEmission
) {
}
