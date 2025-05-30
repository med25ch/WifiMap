package src.main.java.org.example.ui.map;

import org.example.domain.Controleur;
import org.example.domain.barrieres.BarriereDTO;
import org.example.domain.interfaces.MapElement;
import org.example.domain.pointacces.PointAccesDTO;

import java.awt.event.MouseEvent;

public class MouseMoveHandler {
    private MapElement drawingElement = null;
    private final Controleur controleur;
    private double originalX = 0;
    private double originalY = 0;

    public MouseMoveHandler(Controleur controleur) {
        this.controleur = controleur;
    }

    public MapElement getDrawingElement() {
        return drawingElement;
    }

    private void setDrawingElement(MapElement drawingElement, double clickX, double clickY) {
        this.originalX = clickX;
        this.originalY = clickY;
        this.drawingElement = drawingElement;
    }

    public void handleMouseClicked(MouseEvent e, double x, double y) {
        if (this.drawingElement != null) {
            if (controleur.isMapStateValid()) { // Faut faire de même parce que addHistory va vider nos redo
                controleur.addHistory();
            } else {
                controleur.fixState();
            }
            controleur.calculateSignalForParticules();
            this.drawingElement = null;
            this.originalX = 0;
            this.originalY = 0;
        } else {
            BarriereDTO bar = controleur.addBarriereStart(x, y);
            MapElement barriere = controleur.getElementById(bar.uuid().toString());
            setDrawingElement(barriere, x, y);
        }
    }

    public boolean handleMouseMoved(MouseEvent e, double x, double y) {
        if (drawingElement != null) {
            return handleDrawElement(e, this.originalX, this.originalY, x, y);
        }
        return false;
    }

    private boolean handleDrawElement(MouseEvent e, double originalX, double originalY, double x, double y) {
        if (!controleur.isDrawBarriereMode()) {
            drawingElement = null;
            controleur.fixState();
            return true;
        }
        for (BarriereDTO bar : controleur.getBarrieresDTOList()) {
            if (drawingElement.getId().equals(bar.uuid().toString())) {
                controleur.updateBarrierePosition(bar, originalX, x, originalY, y);
                return true;
            }
        }
        return false;
    }
}
