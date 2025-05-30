package src.main.java.org.example.ui.map;

import org.example.domain.Controleur;
import org.example.domain.barrieres.BarriereDTO;
import org.example.domain.interfaces.MapElement;
import org.example.domain.pointacces.PointAccesDTO;

import java.awt.event.MouseEvent;

public class DragHandler {
    private MapElement draggingElement = null;
    private MapElement drawingElement = null;
    private final Controleur controleur;
    private double originalX = 0;
    private double originalY = 0;

    public DragHandler(Controleur controleur) {
        this.controleur = controleur;
    }

    public MapElement getDraggingElement() {
        return draggingElement;
    }

    public MapElement getDrawingElement() {
        return drawingElement;
    }

    public void setDrawingElement(MapElement drawingElement, double clickX, double clickY) {
        this.originalX = clickX;
        this.originalY = clickY;
        this.drawingElement = drawingElement;
    }

    public void setDraggingElement(MapElement draggingElement) {
        this.draggingElement = draggingElement;
    }
    private int offsetX = -1;
    private int offsetY = -1;

    public void handleMouseReleased(MouseEvent e, double x, double y) {
        if (draggingElement != null || drawingElement != null) {
            offsetX = -1;
            offsetY = -1;
            if (controleur.isMapStateValid()) { // Faut faire de même parce que addHistory va vider nos redo
                controleur.addHistory();
            } else {
                controleur.fixState();
            }
            controleur.calculateSignalForParticules();
            draggingElement = null;
            drawingElement = null;
            this.originalX = 0;
            this.originalY = 0;
        }
    }

    public boolean handleMouseDragged(MouseEvent e, double x, double y) {
        if (draggingElement != null) {
            return handleMoveElement(e, x, y);
        } else if (drawingElement != null) {
            return handleDrawElement(e, this.originalX, this.originalY, x, y);
        }
        return false;
    }

    private boolean handleDrawElement(MouseEvent e, double originalX, double originalY, double x, double y) {
        for (BarriereDTO bar : controleur.getBarrieresDTOList()) {
            if (drawingElement.getId().equals(bar.uuid().toString())) {
                controleur.updateBarrierePosition(bar, originalX, x, originalY, y);
                return true;
            }
        }
        return false;
    }

    private boolean handleMoveElement(MouseEvent e, double x, double y) {
        for (PointAccesDTO pa : controleur.getPointsAccesDTOList()) {
            if (draggingElement.getId().equals(pa.ssid())) {
                controleur.updatePointPosition(pa, x, y);
                return true;
            }
        }
        for (BarriereDTO bar : controleur.getBarrieresDTOList()) {
            if (draggingElement.getId().equals(bar.uuid().toString())) {
                // Le offset est pour garder la barrière à la même position relatif à ta souris
                if (offsetX == -1 && offsetY == -1) {
                    offsetX = (int) x - bar.xStart();
                    offsetY = (int) y - bar.yStart();
                }
                controleur.updateBarrierePosition(bar, x, y, offsetX, offsetY);
                return true;
            }
        }
        return false;
    }
}
