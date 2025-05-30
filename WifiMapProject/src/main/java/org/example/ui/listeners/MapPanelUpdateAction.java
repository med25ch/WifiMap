package org.example.ui.listeners;

import org.example.ui.map.MapPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapPanelUpdateAction implements ActionListener {

    private final MapPanel mapPanel;

    public MapPanelUpdateAction(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mapPanel.repaint();
    }
}
