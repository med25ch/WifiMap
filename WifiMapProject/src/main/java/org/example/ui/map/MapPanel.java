package org.example.ui.map;

import org.example.ui.heatmap.HeatMap;
import org.example.domain.Controleur;
import org.example.domain.barrieres.BarriereDTO;
import org.example.domain.heatmap.ParticuleDTO;
import org.example.domain.helpers.ServiceLocator;
import org.example.domain.interfaces.MapElement;
import org.example.domain.pointacces.PointAccesDTO;
import org.example.domain.utils.HeatMapLog;
import org.example.ui.containers.InfoPanel;
import src.main.java.org.example.ui.map.DragHandler;
import src.main.java.org.example.ui.map.MouseMoveHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import static org.example.domain.utils.MapUtils.roundBasedOnFraction;

public class MapPanel extends JPanel implements ActionListener {
    
    private double xOffset = Integer.MIN_VALUE;
    private double yOffset = Integer.MIN_VALUE;

    private HeatMapLog heatMapLog;
    private static int counter = 0;
    private int mapWidth;
    private int mapHeight;
    private Controleur controleur;
    private double xMeters = -1, yMeters = -1;

    private HeatMap heatMap;
    private final int marginLeft = 40;
    private final int marginTop = 20;
    private InfoPanel infoPanel;
    private DragHandler dragHandler;
    private MouseMoveHandler mouseMoveHandler;
    private String hoverText = null;
    private int hoverX = 0, hoverY = 0;

    private double zoom = 1.0;
    private BufferedImage heatmapImage;
    private Point firstPoint = null;


    public MapPanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;
        this.controleur = ServiceLocator.getInstance().getService(Controleur.class);
        this.dragHandler = new DragHandler(this.controleur);
        this.mouseMoveHandler = new MouseMoveHandler(this.controleur);
        this.heatMapLog = ServiceLocator.getInstance().getService(HeatMapLog.class);
        this.mapWidth = (int) controleur.getCarteDTO().dimension().getWidth();
        this.mapHeight = (int) controleur.getCarteDTO().dimension().getHeight();
        heatMap = new HeatMap(this.mapWidth, this.mapHeight);
        this.setOpaque(false);
        this.controleur.addActionListener(this);
        setPreferredSize(new Dimension(mapWidth + marginLeft, mapHeight + marginTop));

        // on resize seulement au boot, une seule fois, pour centrer la carte
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {

                if (xOffset == Integer.MIN_VALUE && yOffset == Integer.MIN_VALUE) {
                    centerMap();
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                double xClicked = (e.getX() - marginLeft - xOffset) / zoom;
                double yClicked = (e.getY() - marginTop - yOffset) / zoom;

                yClicked = mapHeight - yClicked;

                xMeters = xClicked;
                yMeters = yClicked;

                if (isMouseOutOfBounds(xMeters, yMeters)) return;

                var drawMode = controleur.isDrawBarriereMode() || controleur.isDrawPointMode();

                if(!drawMode) {
                    MapElement clickedElement = controleur.getClickedElement(xMeters, yMeters);

                    if (clickedElement != null) {
                        dragHandler.setDraggingElement(clickedElement);
                        clickedElement.handleClick();
                        controleur.setSelectedMapElement(clickedElement);
                    }
                }else {
                    if(controleur.isDrawBarriereMode()) {
                        mouseMoveHandler.handleMouseClicked(e, xMeters, yMeters);
                    } else if(controleur.isDrawPointMode()){
                        controleur.addPoint(xMeters, yMeters);
                    }

                }

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                double xReleased = (e.getX() - marginLeft - xOffset) / zoom;
                double yReleased = (e.getY() - marginTop - yOffset) / zoom;
                yReleased = mapHeight - yReleased;

                dragHandler.handleMouseReleased(e, xReleased, yReleased);
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved (MouseWheelEvent e){

                double factor = 1.1;
                double newZoom = (e.getWheelRotation() < 0) ? zoom * factor : zoom / factor;

                double mouseXMap = (e.getX() - marginLeft - xOffset) / zoom;
                double mouseYMap = (e.getY() - marginTop - yOffset) / zoom;

                zoom = newZoom;

                xOffset = e.getX() - marginLeft - (mouseXMap * zoom);
                yOffset = e.getY() - marginTop - (mouseYMap * zoom);

                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                double xClicked = (e.getX() - marginLeft - xOffset) / zoom;
                double yClicked = (e.getY() - marginTop - yOffset) / zoom;
                yClicked = mapHeight - yClicked;

                xMeters = xClicked;
                yMeters = yClicked;

                updateInfoBar(xClicked, yClicked);

                if (isMouseOutOfBounds(xMeters, yMeters)) return;
                hoverText = null;
                boolean shouldRepaint = dragHandler.handleMouseDragged(e, (int)xMeters, (int)yMeters);
                if (shouldRepaint) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                double xClicked = (e.getX() - marginLeft - xOffset) / zoom;
                double yClicked = (e.getY() - marginTop - yOffset) / zoom;
                yClicked = mapHeight - yClicked;

                int roundedX = (int) (xClicked + 0.5);
                int roundedY = (int) (yClicked + 0.5);

                hoverX = e.getX();
                hoverY = e.getY();

                MapElement underCursorElement = controleur.getClickedElement(xClicked, yClicked);
                if (underCursorElement != null && mouseMoveHandler.getDrawingElement() != underCursorElement) {
                    hoverText = underCursorElement.getDetails();
                } else {
                    hoverText = null;
                }

                updateInfoBar(roundedX, roundedY);
                if (!isMouseOutOfBounds(xClicked, yClicked)) {
                    mouseMoveHandler.handleMouseMoved(e, roundedX, roundedY);
                }
                repaint();
            }
        });
    }

    private boolean isMouseOutOfBounds(double x, double y) {
        return x > mapWidth || x < 0 || y > mapHeight || y < 0;
    }

    private void centerMap() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        xOffset = (panelWidth - (mapWidth)) / 2.0;
        yOffset = (panelHeight - (mapHeight)) / 2.0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("stateChanged".equals(e.getActionCommand())) {
            repaint();
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.translate(marginLeft + xOffset, marginTop + yOffset);
        g2d.scale(zoom, zoom);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, mapWidth, mapHeight);

        if (controleur.getColorMatrix() != null) {
            heatMap.generateHeatmap(controleur.getColorMatrix());
            placeHeatmap(g2d);
        }


        g2d.drawRect(0, 0, mapWidth, mapHeight);

        if (controleur.getGrilleState())
            drawGrid(g2d, controleur.getGrilleSize());

        drawAxes(g2d);
        drawBarriere(g2d);
        applyThermalEffect(g2d);
        drawPointAcces(g2d);

        g2d.dispose();

        if (hoverText != null) {
            Graphics2D hud = (Graphics2D) g.create();
            hud.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Font font = new Font("Arial", Font.BOLD, 12);
            hud.setFont(font);
            FontMetrics fm = hud.getFontMetrics();

            int paddingX = 12;
            int paddingY = 8;
            int iconSize = 14;

            int textWidth = fm.stringWidth(hoverText);
            int boxWidth = textWidth + paddingX * 2 + iconSize + 6;
            int boxHeight = fm.getHeight() + paddingY * 2;

            int boxX = hoverX + 12;
            int boxY = hoverY + 12;

            for (int i = 4; i > 0; i--) {
                hud.setColor(new Color(0, 0, 0, 50 / i));
                hud.fillRoundRect(boxX - i, boxY - i, boxWidth + i * 2, boxHeight + i * 2, 14, 14);
            }

            hud.setColor(new Color(0, 0, 0, 220));
            hud.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 12, 12);

            int iconX = boxX + paddingX - 2;
            int iconY = boxY + paddingY + 2;
            hud.setColor(Color.WHITE);
            hud.fillOval(iconX + 5, iconY + 9, 4, 4);
            hud.drawArc(iconX, iconY + 2, 14, 14, 45, 90);
            hud.drawArc(iconX - 4, iconY - 2, 22, 22, 45, 90);

            int textX = iconX + iconSize + 6;
            int textY = boxY + paddingY + fm.getAscent();
            hud.setColor(Color.WHITE);
            hud.drawString(hoverText, textX, textY);

            hud.dispose();
        }
    }

    private void drawAxes(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        int step = 50;

        g2d.drawLine(0, mapHeight, mapWidth, mapHeight);
        g2d.drawLine(mapWidth - 5, mapHeight - 5, mapWidth, mapHeight);
        g2d.drawLine(mapWidth - 5, mapHeight + 5, mapWidth, mapHeight);

        for (int x = 0; x <= mapWidth; x += step) {
            g2d.drawLine(x, mapHeight - 3, x, mapHeight + 3);
            g2d.drawString(Integer.toString(x), x - 10, mapHeight + 15);
        }

        g2d.drawLine(0, mapHeight, 0, 0);
        g2d.drawLine(-5, 5, 0, 0);
        g2d.drawLine(5, 5, 0, 0);

        for (int y = 0; y <= mapHeight; y += step) {
            g2d.drawLine(-3, mapHeight - y, 3, mapHeight - y);
            g2d.drawString(Integer.toString(y), -25, mapHeight - y + 5);
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("X", mapWidth + 10, mapHeight + 5);
        g2d.drawString("Y", -10, -10);
    }

    private void drawPointAcces(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        for (PointAccesDTO pa : controleur.getPointsAccesDTOList()) {
            int screenX = (int) pa.x();
            int screenY = (int) (mapHeight - pa.y());
            g2d.fillOval((screenX - 2), (screenY - 2), 5, 5);
        }
    }

    private void drawBarriere(Graphics2D g) {
        for (BarriereDTO b : controleur.getBarrieresDTOList()) {
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke((float) b.largeur() / 100));
            int yStart = mapHeight - b.yStart();
            int yEnd = mapHeight - b.yEnd();
            g.drawLine(b.xStart(), yStart, b.xEnd(), yEnd);
        }
    }

    private void drawGrid(Graphics g, int size) {
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x <= mapWidth; x += size) {
            g.drawLine(x, 0, x, mapHeight);
        }
        for (int y = 0; y <= mapHeight; y += size) {
            g.drawLine(0, mapHeight - y, mapWidth, mapHeight - y);
        }
    }

    private void applyThermalEffect(Graphics g) {
        var thermalEffect = controleur.getMapThermalEffect();
        switch (thermalEffect) {
            case CHAUD -> g.setColor(new Color(255, 200, 150, 50));
            case FROID -> g.setColor(new Color(150, 200, 255, 50));
            default -> {
                return;
            }
        }
        g.fillRect(0, 0, mapWidth, mapHeight);
    }

    private void updateInfoBar(double x, double y) {
        int roundedX = (int) (x + .5);
        int roundedY = (int) (y + .5);
        ParticuleDTO particule = controleur.getParticuleAt(roundedX, roundedY);
        if (particule != null) {
            infoPanel.updateStatus("dBm: " + (particule.dbm() == Integer.MIN_VALUE ? "N/D" : particule.dbm()) + " | X: " + roundedX + " | Y: " + roundedY + " | Qualité: " + particule.qualite());
        } else {
            infoPanel.updateStatus("dBm: N/D | X: N/D" + " | Y: N/D | Qualité: N/D");
        }
    }


    private void placeHeatmap(Graphics2D g2d) {
        BufferedImage image = heatMap.getHeatmap();
        // Inverser l’image verticalement
        AffineTransform at = AffineTransform.getScaleInstance(1, -1);
        at.translate(0, -image.getHeight());
        g2d.drawImage(image, at, null);
    }

}
