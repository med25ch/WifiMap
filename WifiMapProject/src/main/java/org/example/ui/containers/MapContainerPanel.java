package org.example.ui.containers;

import org.example.domain.Controleur;
import org.example.domain.helpers.ServiceLocator;
import org.example.ui.containers.toolbar.CustomToolBar;
import org.example.ui.map.MapPanel;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class MapContainerPanel extends JPanel implements Serializable {
    private MapPanel mapPanel;
    private final Controleur controleur;
    private CustomToolBar customToolBar;
    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public MapContainerPanel() {
        controleur = ServiceLocator.getInstance().getService(Controleur.class);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Ajout de la légende thermique en haut
        ThermalLegendPanel legendPanel = new ThermalLegendPanel();
        customToolBar = new CustomToolBar();
        add(customToolBar, BorderLayout.NORTH);
    }

    public void setBounds() {
        if (mapPanel != null) remove(mapPanel);

        InfoPanel infoPanel = new InfoPanel();
        mapPanel = new MapPanel(infoPanel);
        setPreferredSize(mapPanel.getPreferredSize());

        add(mapPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        customToolBar.updateCoverageStatus();
    }
}
