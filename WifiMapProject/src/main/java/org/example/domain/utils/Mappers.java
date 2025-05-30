package org.example.domain.utils;

import org.example.domain.barrieres.Barriere;
import org.example.domain.barrieres.BarriereDTO;
import org.example.domain.elementsfactory.ConcreteMapElementFactory;
import org.example.domain.grille.Grille;
import org.example.domain.grille.GrilleDTO;
import org.example.domain.heatmap.Particule;
import org.example.domain.heatmap.ParticuleDTO;
import org.example.domain.helpers.ServiceLocator;
import org.example.domain.map.Carte;
import org.example.domain.map.CarteDto;
import org.example.domain.pointacces.PointAcces;
import org.example.domain.pointacces.PointAccesDTO;

public class Mappers {

    public static GrilleDTO toGrilleDTO(Grille grille) {
        return new GrilleDTO(grille.getTaille(), grille.isMagnetic(), grille.isActive());
    }

    public static Grille fromGrilleDTO(GrilleDTO dto) {
        var factory = ServiceLocator.getInstance().getService(ConcreteMapElementFactory .class);
        return factory.createGrille(dto.taille(), dto.isMagnetic(), dto.isActive());
    }

    public static BarriereDTO toBarriereDTO(Barriere barriere) {
        return new BarriereDTO(barriere.getUuid(), barriere.getMateriel(),barriere.getLargeur(),barriere.getOrientation(),barriere.getXStart(),barriere.getYStart(),barriere.getXEnd(),barriere.getYEnd());
    }

    public static Barriere fromBarriereDTO(BarriereDTO barriereDTO) {
        var factory = ServiceLocator.getInstance().getService(ConcreteMapElementFactory.class);
        return factory.createBarriere(barriereDTO.uuid(), barriereDTO.materiel(),barriereDTO.largeur(),barriereDTO.orientation(),barriereDTO.xStart(),barriereDTO.yStart(),barriereDTO.xEnd(),barriereDTO.yEnd());
    }

    public static CarteDto toCarteDto(Carte carte) {
        return new CarteDto(carte.getDimension(),carte.getThermalEffect());
    }

    public static Carte fromCarteDto(CarteDto carteDTO) {
        var factory = ServiceLocator.getInstance().getService(ConcreteMapElementFactory .class);
        return factory.createCarte(carteDTO.dimension(),carteDTO.thermalEffect());
    }

    public static PointAccesDTO toPointAccesDTO(PointAcces pointAcces) {
        return new PointAccesDTO(
                pointAcces.getSsid(),
                pointAcces.getX(),
                pointAcces.getY(),
                pointAcces.getFrequence(),
                pointAcces.getPuissanceEmission()
        );
    }

    public static PointAcces fromPointAccesDTO(PointAccesDTO dto) {
        var factory = ServiceLocator.getInstance().getService(ConcreteMapElementFactory .class);
        return factory.createPointAcces(dto.ssid(), dto.x(), dto.y(), dto.frequence(), dto.puissanceEmission());
    }

    public static ParticuleDTO toParticuleDTO(Particule particule) {
        return new ParticuleDTO(
                particule.getColor(),
                particule.getDbm(),
                particule.getQualite()
        );
    }

    public static Particule fromParticuleDTO(ParticuleDTO dto) {
        var factory = ServiceLocator.getInstance().getService(ConcreteMapElementFactory .class);
        var particule = factory.createParticule();
        particule.setColor(dto.color());
        particule.setDbm(dto.dbm());
        particule.setQualite(dto.qualite());
        return particule;
    }

}
