package org.example.ui.containers.toolbar;

import org.example.domain.Controleur;
import org.example.domain.helpers.ServiceLocator;
import org.example.ui.containers.ThermalLegendPanel;

import javax.swing.*;
import java.awt.*;

import static org.example.ui.UIUtils.loadIcon;

public class CustomToolBar extends JToolBar {

    JButton undoButton;
    JButton redoButton;
    JButton magneticPointButton;
    JButton magneticLineButton;
    JButton selectionToolButton;
    JPanel coverageStatusPanel;
    JLabel coverageText;
    Controleur controleur;

    public CustomToolBar() {

        setFloatable(false);
        setBackground(Color.WHITE);

        controleur = ServiceLocator.getInstance().getService(Controleur.class);


        undoButton = createButton("Undo", "/pictures/undo.png");
        redoButton = createButton("Redo", "/pictures/redo.png");
        selectionToolButton = createButton("Sélection", "/pictures/cursor.png");
        magneticPointButton = createButton("Point d'accès", "/pictures/wifi.png");
        magneticLineButton = createButton("Barrière", "/pictures/barriere.png");

        initListeners();
        updateButtonHighlight(selectionToolButton, !controleur.isDrawPointMode() && !controleur.isDrawBarriereMode());


        add(undoButton);
        add(redoButton);
        add(selectionToolButton);
        add(magneticPointButton);
        add(magneticLineButton);


        add(Box.createHorizontalStrut(250));

        addCoverageStatus();

        ThermalLegendPanel legendPanel = new ThermalLegendPanel();
        add(legendPanel);

    }

    public void updateCoverageStatus() {
        if (controleur.getIsGoodCoverage()) {
            coverageText.setForeground(new Color(39, 174, 96));
            coverageText.setText("OK");
        } else {
            coverageText.setForeground(new Color(192, 57, 43));
            coverageText.setText("PAS OK");
        }
    }

    private void initListeners() {

        undoButton.addActionListener(e -> {
            controleur.undo();
        });

        redoButton.addActionListener(e -> {
            controleur.redo();
        });
        magneticLineButton.addActionListener(e -> {
            controleur.toggleDrawBarriereMode();
            updateButtonHighlight(magneticLineButton, controleur.isDrawBarriereMode());
            updateButtonHighlight(magneticPointButton, controleur.isDrawPointMode());
            updateButtonHighlight(selectionToolButton, !controleur.isDrawPointMode() && !controleur.isDrawBarriereMode());
        });

        magneticPointButton.addActionListener(e -> {
            controleur.toggleDrawPointMode();
            updateButtonHighlight(magneticPointButton, controleur.isDrawPointMode());
            updateButtonHighlight(magneticLineButton, controleur.isDrawBarriereMode());
            updateButtonHighlight(selectionToolButton, !controleur.isDrawPointMode() && !controleur.isDrawBarriereMode());
        });

        selectionToolButton.addActionListener(e -> {
            if (controleur.isDrawPointMode()) controleur.toggleDrawPointMode();
            else if (controleur.isDrawBarriereMode()) controleur.toggleDrawBarriereMode();
            updateButtonHighlight(selectionToolButton, !controleur.isDrawPointMode() && !controleur.isDrawBarriereMode());
            updateButtonHighlight(magneticPointButton, controleur.isDrawPointMode());
            updateButtonHighlight(magneticLineButton, controleur.isDrawBarriereMode());
        });
    }


    private JButton createButton(String tooltip, String iconPath) {

        ImageIcon icon = loadIcon(iconPath,32,32);

        if (icon.getIconWidth() == -1) {
            System.out.println("Icon not found or failed to load");
        }

        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);

        return button;
    }

    private void updateButtonHighlight(JButton button, boolean active) {
        if (active) {
            button.setBackground(new Color(200, 200, 255)); // light blue
        } else {
            button.setBackground(null); // reset to default
        }
    }

    private void addCoverageStatus() {
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        wrapperPanel.setOpaque(false);

        coverageStatusPanel = new JPanel();
        coverageStatusPanel.setLayout(new BoxLayout(coverageStatusPanel, BoxLayout.Y_AXIS));
        coverageStatusPanel.setOpaque(false);
        coverageStatusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

        JLabel coverageTitle = new JLabel("Couverture");
        coverageTitle.setForeground(Color.DARK_GRAY);
        coverageTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        coverageText = new JLabel();
        coverageText.setAlignmentX(Component.CENTER_ALIGNMENT);

        coverageStatusPanel.add(coverageTitle);
        coverageStatusPanel.add(Box.createVerticalStrut(3));
        coverageStatusPanel.add(coverageText);

        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(Box.createVerticalGlue());
        verticalBox.add(coverageStatusPanel);
        verticalBox.add(Box.createVerticalGlue());

        wrapperPanel.add(verticalBox);

        add(wrapperPanel);
    }
}
