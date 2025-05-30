package org.example.domain;

import org.example.domain.heatmap.HeatMapGridManager;
import org.example.domain.barrieres.Barriere;
import org.example.domain.barrieres.BarriereDTO;
import org.example.domain.barrieres.Material;
import org.example.domain.barrieres.Orientation;
import org.example.domain.elementsfactory.ConcreteMapElementFactory;
import org.example.domain.elementsfactory.MapElementFactory;
import org.example.domain.grille.Grille;
import org.example.domain.grille.GrilleDTO;
import org.example.domain.heatmap.Particule;
import org.example.domain.heatmap.ParticuleDTO;
import org.example.domain.helpers.ServiceLocator;
import org.example.domain.interfaces.MapElement;
import org.example.domain.map.Carte;
import org.example.domain.map.CarteDto;
import org.example.domain.map.MapThermalEffect;
import org.example.domain.pointacces.Frequence;
import org.example.domain.pointacces.PointAcces;
import org.example.domain.pointacces.PointAccesDTO;
import org.example.domain.savemanager.Project;
import org.example.domain.utils.HeatMapExceptions;
import org.example.domain.utils.Mappers;
import org.example.domain.utils.SsidGenerator;
import src.main.java.org.example.domain.historymanager.HistoryManager;
import org.example.domain.savemanager.SaveManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.awt.geom.Line2D.linesIntersect;
import static org.example.domain.utils.MapUtils.getInteriorLine;
import static org.example.domain.utils.MapUtils.getOrientation;

public class Controleur {

    private final Carte carte;
    private final Grille grille;
    private final List<ActionListener> listeners = new ArrayList<>();
    private final MapElementFactory factory;
    private final List<MapElement> mapElements;
    private final HeatMapGridManager heatMapGridManager;
    private MapElement selectedMapItem = null;

    private List<PointAcces> listePointAcces;
    private List<Barriere> listeBarrieres;
    private String projectName;
    private final HistoryManager historyManager = new HistoryManager();
    private boolean isDrawBarriereMode = false;
    private boolean isDrawPointMode = false;

    private Frequence selectedFrequence = Frequence.GHz2_4;

    private int selectedPuissance = 15;

    private Material selectedMateriel = Material.PLATRE;

    private int selectedEpaisseur = 30;

    public GrilleDTO getGrille() {
        return Mappers.toGrilleDTO(grille);
    }

    public void setTailleGrille(int tailleGrille) {
        grille.setTaille(tailleGrille);
    }

    public Controleur() {
        this.factory = ServiceLocator.getInstance().getService(ConcreteMapElementFactory.class);
        this.carte = factory.createCarte();
        this.grille = factory.createGrille();
        this.mapElements = new ArrayList<>();
        this.listePointAcces = new ArrayList<>();
        this.listeBarrieres = new ArrayList<>();
        this.heatMapGridManager = new HeatMapGridManager();
    }

    public void addHistory() {
        historyManager.addState(toProject());
    }

    public void undo() {
        try {
            Project project = historyManager.getLastState();
            this.changeState(project);
        } catch (IndexOutOfBoundsException e) {
            // historique passé vide, rien a faire
        }
    }

    public boolean isMapStateValid() {
        HashMap<Point, Integer> hmPAs = new HashMap<>();
        for (var pa : listePointAcces) {
            Point pos = new Point((int) pa.getX(), (int) pa.getY());
            if (!isPointWithinBounds((int)pa.getX(), (int)pa.getY())) return false;
            if (hmPAs.containsKey(pos)) return false;
            hmPAs.put(pos, 1);
        }

        for (int i = 0; i < listeBarrieres.size(); i++) {
            var b1 = listeBarrieres.get(i);
            if (!isBarrierePositionValid(b1, i+1)) return false;
        }

        return true;
    }

    public void fixState() {
        Project project = historyManager.getCurrentState();
        this.changeState(project);
    }

    public void redo() {
        try {
            Project project = historyManager.getNextState();
            this.changeState(project);
        } catch (IndexOutOfBoundsException e) {
            // historique futur vide, rien a faire
        }
    }

    public void loadProjectFile(String path, String name) {
        Project project = SaveManager.load(path);
        if (project != null) {
            historyManager.reset();
            this.loadProject(project, name);
            addHistory();
        }
    }

    public void loadProject(Project project, String name) {
        this.clearProject();
        this.setupProject(project.getWidth(), project.getHeight(), name);
        for (PointAcces pa : project.getPointAccesList())
            this.loadPoint(pa.getX(), pa.getY(), pa.getFrequence(), pa.getPuissanceEmission());
        for (Barriere ba : project.getBarrieres()) {
            this.loadBarriere(new BarriereDTO(ba.getUuid(), ba.getMateriel(), ba.getLargeur(), ba.getOrientation(), ba.getXStart(), ba.getYStart(), ba.getXEnd(), ba.getYEnd()));
        }
    }

    public void saveProject(String path) {
        SaveManager.save(toProject(), path);
    }

    public void clearProject() {
        this.mapElements.clear();
        this.listePointAcces.clear();
        this.listeBarrieres.clear();
    }

    private void setupProject(int width, int height, String name) {
        this.carte.setDimension(new Dimension(width, height));
        this.projectName = name;
        this.setSelectedMapElement(null);

        initializeParticuleArr(width, height);
        calculateSignalForParticules();
    }

    public void newProject(int width, int height, String name) {
        historyManager.reset();
        setupProject(width, height, name);
        addHistory();
    }

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String name) {
        this.projectName = name;
    }

    public ParticuleDTO getParticuleAt(int x, int y) {
        Particule particule = heatMapGridManager.getParticuleAt(x, y);
        if (particule == null) return null;
        return Mappers.toParticuleDTO(particule);
    }

    public Project toProject() {
        Project project = new Project(listeBarrieres, listePointAcces, getMapWidth(), getMapHeight());
        return project;
    }

    public CarteDto getCarteDTO() {
        return Mappers.toCarteDto(carte);
    }

    public int getMapHeight() {
        return getCarteDTO().dimension().height;
    }

    public int getMapWidth() {
        return getCarteDTO().dimension().width;
    }

    public void activateGrille(boolean activate) {
        grille.setActive(activate);
    }

    public void activateGrilleMagnetisme(boolean activate) {
        grille.setMagnetic(activate);
    }

    public boolean getGrilleState(){
        return getGrille().isActive();
    }

    public int getGrilleSize(){
        return getGrille().taille();
    }

    public MapThermalEffect getMapThermalEffect() {
        return this.carte.getThermalEffect();
    }

    public void setMapThermalEffect(MapThermalEffect mapThermalEffect) {
        this.carte.setThermalEffect(mapThermalEffect);
    }

    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    public void setSelectedMapElement(MapElement mapElement) {
        this.selectedMapItem = mapElement;
        firePointSelectedEvent();
    }

    private void firePointSelectedEvent() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "pointSelected");
        for (ActionListener listener : listeners) {
            listener.actionPerformed(event);
        }
    }

    public void initializeParticuleArr(int width, int height) {
        heatMapGridManager.initializeGrid(width, height);
    }

    public Object getSelectedElement() {
        if (selectedMapItem instanceof PointAcces) {
            return Mappers.toPointAccesDTO((PointAcces) selectedMapItem);
        } else if (selectedMapItem instanceof Barriere) {
            return Mappers.toBarriereDTO((Barriere) selectedMapItem);
        }
        return null; // No selected element
    }

    public void updateSelectedElement(Object... params) {
        if (this.selectedMapItem != null) {
            this.selectedMapItem.update(params);
            addHistory();
            calculateSignalForParticules();
        }
    }

    public int[][] getColorMatrix() {
        return heatMapGridManager.getColorMatrix();
    }

    public void addPoint(double x, double y) {
        PointAcces point = factory.createPointAcces(UUID.randomUUID().toString(), x, y, getSelectedFrequence(), getSelectedPuissance());
        try {
            snapPointAccesToGrid(point);
            if (getPointAccesAt(x, y) != null) return;
            listePointAcces.add(point);
            mapElements.add(point);
            calculateSignalForParticules();
            addHistory();
        } catch (HeatMapExceptions.HeatMapException e) {
            // fait rien
        }
    }

    public boolean getIsGoodCoverage() {
        return heatMapGridManager.getIsGoodCoverage();
    }

    private PointAcces getPointAccesAt(double x, double y) {
        for (PointAcces pa : listePointAcces) {
            if (pa.getX() == x && pa.getY() == y) {
                return pa;
            }
        }
        return null;
    }

    public void addPoint(double x, double y, Frequence frequence, int puissance) {
        PointAcces point = factory.createPointAcces(SsidGenerator.generateSsid(), x, y, frequence,puissance);
        listePointAcces.add(point);
        mapElements.add(point);
        calculateSignalForParticules();
        addHistory();
    }

    public void loadPoint(double x, double y, Frequence frequence, int puissance) {
        // c'Est load point ici
        PointAcces point = factory.createPointAcces(SsidGenerator.generateSsid(), x, y, frequence,puissance);
        listePointAcces.add(point);
        mapElements.add(point);
        calculateSignalForParticules();
    }

    public void updatePointPosition(PointAccesDTO point, double x, double y) {
        // Ne pas ajouter a l'historique
        for (PointAcces pa : listePointAcces) {
            if (pa.getSsid().equals(point.ssid())) {
                pa.setX(snapToGrid(x));
                pa.setY(snapToGrid(y));
                return;
            }
        }
    }

    public void updateBarrierePosition(BarriereDTO barriere, double x, double y, int offsetX, int offsetY) {
        for (Barriere bar : listeBarrieres) {
            if (bar.getUuid().equals(barriere.uuid())) {
                if (bar.getOrientation().equals(Orientation.HORIZONTAL)) {
                    int newXStart = (int)x - offsetX;
                    int newXEnd = newXStart + (barriere.xEnd() - barriere.xStart());
                    int newY = (int)y - offsetY;

                    newXStart = snapToGrid(newXStart);
                    newXEnd = snapToGrid(newXEnd);
                    newY = snapToGrid(newY);

                    bar.setxStart(newXStart);
                    bar.setxEnd(newXEnd);
                    bar.setyStart(newY);
                    bar.setyEnd(newY);
                } else {
                    int newYStart = (int)y - offsetY;
                    int newYEnd = newYStart + (barriere.yEnd() - barriere.yStart());
                    int newX = (int)x - offsetX;
                    newYStart =snapToGrid(newYStart);
                    newYEnd = snapToGrid(newYEnd);
                    newX = snapToGrid(newX);

                    bar.setxStart(newX);
                    bar.setxEnd(newX);
                    bar.setyStart(newYStart);
                    bar.setyEnd(newYEnd);
                }
                return;
            }
        }
    }

    public void updateBarrierePosition(BarriereDTO barriere, double xStart, double xEnd, double yStart, double yEnd) {
        for (Barriere bar : listeBarrieres) {
            if (bar.getUuid().equals(barriere.uuid())) {

                if (grille.isMagnetic()) {
                    int gridSize = this.grille.getTaille();
                    xStart = Math.round(xStart / gridSize) * gridSize;
                    xEnd   = Math.round(xEnd   / gridSize) * gridSize;
                    yStart = Math.round(yStart / gridSize) * gridSize;
                    yEnd   = Math.round(yEnd   / gridSize) * gridSize;
                }

                double dx = Math.abs(xEnd - xStart);
                double dy = Math.abs(yEnd - yStart);

                bar.setxStart((int) xStart);
                bar.setyStart((int) yStart);

                if (dx < dy) {
                    bar.setxEnd((int) xStart);
                    bar.setyEnd((int) yEnd);
                    bar.setOrientation(Orientation.VERTICAL);
                } else {
                    bar.setxEnd((int) xEnd);
                    bar.setyEnd((int) yStart);
                    bar.setOrientation(Orientation.HORIZONTAL);
                }

                return;
            }
        }
    }

    public BarriereDTO addBarriereStart(double x, double y) {
        int intX = (int) x;
        int intY = (int) y;
        Barriere barriere = new Barriere(getSelectedMateriel(), getSelectedEpaisseur(), Orientation.HORIZONTAL, intX, intY, intX, intY);
        listeBarrieres.add(barriere);
        mapElements.add(barriere);
        return new BarriereDTO(barriere.getUuid(), barriere.getMateriel(), barriere.getLargeur(), barriere.getOrientation(), barriere.getXStart(), barriere.getYStart(), barriere.getXEnd(), barriere.getYEnd());
    }

    // Expose only DTOs to the UI
    public List<PointAccesDTO> getPointsAccesDTOList() {
        List<PointAccesDTO> dtos = new ArrayList<>();
        for (MapElement element : mapElements) {
            if (element instanceof PointAcces) {
                dtos.add(Mappers.toPointAccesDTO((PointAcces) element));
            }
        }
        return dtos;
    }

    public PointAccesDTO getPointAccesDtoAt(Double x, Double y) {
        for (MapElement element : mapElements) {
            if (element instanceof PointAcces) {
                PointAcces point = (PointAcces) element;
                if (point.contains(x, y)) {
                    return Mappers.toPointAccesDTO(point);
                }
            }
        }
        return null;
    }

    public void supprimerSelectedElement() {
        if (this.selectedMapItem == null) return;
        if (!listePointAcces.remove(this.selectedMapItem)) {
            listeBarrieres.remove(this.selectedMapItem);
        }
        mapElements.remove(this.selectedMapItem);
        this.selectedMapItem = null;
        calculateSignalForParticules();
        firePointSelectedEvent();
        addHistory();
    }

    public List<BarriereDTO> getBarrieresDTOList() {
        List<BarriereDTO> barriereDTOArrayList = new ArrayList<>();
        for (MapElement element : mapElements) {
            if (element instanceof Barriere) {
                barriereDTOArrayList.add(Mappers.toBarriereDTO((Barriere) element));
            }
        }
        return barriereDTOArrayList;
    }

    public boolean isPointInsideMap(int x, int y) {
        return x >= 0 && x <= getMapWidth() && y >= 0 && y <= getMapHeight();
    }

    public void addBarriere(BarriereDTO barriere) {
        Barriere newBarriere = Mappers.fromBarriereDTO(barriere);
        try {
            snapBarriereToGrid(newBarriere);
            listeBarrieres.add(newBarriere);
            mapElements.add(newBarriere);
            calculateSignalForParticules();
            addHistory();
        } catch (HeatMapExceptions.HeatMapException e) {
            // fait rien
        }
    }

    public MapElement getClickedElement(double xMeters, double yMeters) {
        for (MapElement element : mapElements) {
            if (element.contains(xMeters, yMeters)) {
                return element;
            }
        }
        return null;
    }

    public MapElement getElementById(String id) {
        for (MapElement element : mapElements) {
            if (element.getId().equals(id)) {
                return element;
            }
        }
        return null;
    }

    public void addMapElement(MapElement element) {
        mapElements.add(element);
        calculateSignalForParticules();
        addHistory();
    }

    public boolean isSelectionModePointAcces() {
        return selectedMapItem instanceof PointAcces;
    }

    public boolean isSelectionModeBarriere() {
        return selectedMapItem instanceof Barriere;
    }

    public boolean isBarriereExists(BarriereDTO barriereDTO) {
        for (MapElement element : mapElements) {
            if (element instanceof Barriere) {
                Barriere barriere = (Barriere) element;
                if (barriere.getXStart() == barriereDTO.xStart() &&
                        barriere.getYStart() == barriereDTO.yStart() &&
                        barriere.getXEnd() == barriereDTO.xEnd() &&
                        barriere.getYEnd() == barriereDTO.yEnd()) {
                    return true; // Found an existing matching Barriere
                }
            }
        }
        return false;
    }

    public List<PointAcces> getPointsAccesList() {
        return this.listePointAcces; // ou autre nom exact de ta liste réelle
    }

    public void calculateSignalForParticules() {
        heatMapGridManager.calculateSignalForParticules(listePointAcces, listeBarrieres, getMapWidth(), getMapHeight());
    }

    private void changeState(Project project) {
        clearProject();
        listePointAcces.addAll(project.getPointAccesList());
        listeBarrieres.addAll(project.getBarrieres());
        mapElements.addAll(listePointAcces);
        mapElements.addAll(listeBarrieres);
        calculateSignalForParticules();
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "stateChanged");
        for (ActionListener listener : listeners) {
            listener.actionPerformed(event);
        }
    }


    public void addBarriere(Point pointDepart, Point pointArrive) {
        var orientation = getOrientation(pointDepart.x, pointDepart.y, pointArrive.x, pointArrive.y);
        var barriereDTO =  new BarriereDTO(UUID.randomUUID(), Material.PLATRE, 30,orientation, pointDepart.x, pointDepart.y,pointArrive.x,pointArrive.y);
        addBarriere(barriereDTO);
    }


    public boolean isDrawBarriereMode() {
        return isDrawBarriereMode;
    }

    public void toggleDrawBarriereMode() {
        isDrawBarriereMode = !isDrawBarriereMode;
        if (isDrawPointMode && isDrawBarriereMode) toggleDrawPointMode();
    }

    public boolean isDrawPointMode() {
        return isDrawPointMode;
    }

    public void toggleDrawPointMode() {
        isDrawPointMode = !isDrawPointMode;
        if (isDrawPointMode && isDrawBarriereMode) toggleDrawBarriereMode();
    }

    private void loadBarriere(BarriereDTO barriere) {
        Barriere newBarriere = Mappers.fromBarriereDTO(barriere);
        listeBarrieres.add(newBarriere);
        mapElements.add(newBarriere);
        calculateSignalForParticules();
    }

    private PointAcces snapPointAccesToGrid(PointAcces point) throws HeatMapExceptions.HeatMapException {
        if (grille.isMagnetic()) {
            int x = snapToGrid(point.getX(), false);
            int y = snapToGrid(point.getY(), true);
            if (getPointAccesAt(x, y) == null) {
                point.setX(x);
                point.setY(y);
                return point;
            } else {
                throw new HeatMapExceptions.HeatMapException("Invalid position (snapPointAccesToGrid)");
            }
        } else {
            return point;
        }
    }

    private Barriere snapBarriereToGrid(Barriere barriere) throws HeatMapExceptions.HeatMapException {
        Point startPosition = new Point(barriere.getXStart(), barriere.getYStart());
        Point endPosition = new Point(barriere.getXEnd(), barriere.getYEnd());
        if (grille.isMagnetic()) {
            int xStart = snapToGrid(startPosition.x, false);
            int yStart = snapToGrid(startPosition.y, true);
            int xEnd = snapToGrid(endPosition.getX(), false);
            int yEnd = snapToGrid(endPosition.getY(), true);

            if (!isBarrierePositionValid(barriere, listeBarrieres.size()-1)) {
                barriere.setxStart(xStart);
                barriere.setyStart(yStart);
                barriere.setxEnd(xEnd);
                barriere.setyEnd(yEnd);
            } else {
                throw new HeatMapExceptions.HeatMapException("Invalid position (snapBarriereToGrid)");
            }
        }
        return barriere;
    }

    private boolean isPointWithinBounds(int x, int y) {
        int width = getMapWidth();
        int height = getMapHeight();

        return !(x < 0 || x > width ||
                y < 0 || y > height);
    }

    private boolean isBarrierePositionValid(Barriere barriere, int listeStartPosition) {
        Point b1Start = new Point(barriere.getXStart(), barriere.getYStart());
        Point b1End = new Point(barriere.getXEnd(), barriere.getYEnd());
        Point b1InteriorStart = getInteriorLine(b1Start, b1End);
        Point b1InteriorEnd = getInteriorLine(b1End, b1Start);

        // est hors la map ?
        int x1 = barriere.getXStart(), y1 = barriere.getYStart();
        int x2 = barriere.getXEnd(), y2 = barriere.getYEnd();

        if (!isPointWithinBounds(x1, y1) || !isPointWithinBounds(x2, y2)) {
            return false;
        }

        // s'intersect ?
        for (int j = listeStartPosition; j < listeBarrieres.size(); j++) {
            var b2 = listeBarrieres.get(j);
            if (b2 == barriere) continue;
            Point b2Start = new Point(b2.getXStart(), b2.getYStart());
            Point b2End = new Point(b2.getXEnd(), b2.getYEnd());

            Point b2InteriorStart = b2.getOrientation() == barriere.getOrientation() ? b2Start : getInteriorLine(b2Start, b2End);
            Point b2InteriorEnd = b2.getOrientation() == barriere.getOrientation() ? b2End : getInteriorLine(b2End, b2Start);

            if (linesIntersect(b1InteriorStart.x, b1InteriorStart.y, b1InteriorEnd.x, b1InteriorEnd.y,
                    b2InteriorStart.x, b2InteriorStart.y, b2InteriorEnd.x, b2InteriorEnd.y)) {
                return false;
            }
        }
        return true;
    }

    private int snapToGrid(double value) {
        // pour le drag
        if (grille.isMagnetic()) {
            int gridSize = grille.getTaille();
            return (int)Math.round(value / gridSize) * gridSize;
        }
        return (int)value;
    }

    private int snapToGrid(double value, boolean isY) {
        // pour les placements
        int gridSize = grille.getTaille();
        int initialSnapPosition = (int)Math.round(value / gridSize) * gridSize;
        if (isY) {
            if (initialSnapPosition > getMapHeight()) {
                return initialSnapPosition - gridSize;
            } else if (initialSnapPosition < 0) {
                return gridSize;
            } else {
                return initialSnapPosition;
            }
        } else {
            if (initialSnapPosition > getMapWidth()) {
                return initialSnapPosition - gridSize;
            } else if (initialSnapPosition < 0) {
                return gridSize;
            } else {
                return initialSnapPosition;
            }
        }
    }

    public Frequence getSelectedFrequence()
    {
        return selectedFrequence;
    }

    public void setSelectedFrequence(Frequence selectedFrequence)
    {
        this.selectedFrequence = selectedFrequence;
    }

    public int getSelectedPuissance()
    {
        return selectedPuissance;
    }

    public void setSelectedPuissance(int selectedPuissance)
    {
        this.selectedPuissance = selectedPuissance;
    }

    public Material getSelectedMateriel()
    {
        return selectedMateriel;
    }

    public void setSelectedMateriel(Material selectedMateriel)
    {
        this.selectedMateriel = selectedMateriel;
    }

    public int getSelectedEpaisseur()
    {
        return selectedEpaisseur;
    }

    public void setSelectedEpaisseur(int selectedEpaisseur)
    {
        this.selectedEpaisseur = selectedEpaisseur;
    }
}
