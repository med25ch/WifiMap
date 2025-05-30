package org.example.ui.containers;

import org.example.domain.Controleur;
import org.example.domain.helpers.ServiceLocator;
import org.example.domain.map.MapThermalEffect;
import org.example.domain.pointacces.Frequence;
import org.example.ui.containers.configurations.StyledPanelForBarriere;
import org.example.ui.containers.configurations.StyledPanelForGrille;
import org.example.ui.containers.configurations.StyledPanelForPointAcces;
import org.example.ui.uistrings.UIStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static org.example.ui.UIUtils.createStyledPalette;
import static org.example.ui.uistrings.UIStrings.AJOUTER_EDITER_POINT_ACCES;

public class ConfigurationsContainer extends JPanel {

    private final Controleur controleur;
    private Frequence selectedFrequence;

    private int puissanceInput;

    public ConfigurationsContainer(ActionListener mapPanelUpdateAction) {
        this.controleur = ServiceLocator.getInstance().getService(Controleur.class);
        setBackground(new Color(245, 250, 255));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        init(mapPanelUpdateAction);
    }

    private void init (ActionListener mapPanelUpdateAction){

        // Palette pour éditer les points d'accès
        JPanel accessPointPalette = new StyledPanelForPointAcces(AJOUTER_EDITER_POINT_ACCES,mapPanelUpdateAction);

        // Palette pour éditer les barrières
        JPanel barrierPalette = new StyledPanelForBarriere("Ajouter / Éditer une Barrière",mapPanelUpdateAction);


        // Palette pour éditer la grille
        JPanel otherConfigPalette = new StyledPanelForGrille("Configuration Grille",mapPanelUpdateAction);


        // Palette pour les effets thermiques
        JPanel thermalEffectPalette = createStyledPalette("Effet Thermique");
        ButtonGroup effectGroup = new ButtonGroup();
        JRadioButton normalEffectButton = new JRadioButton(UIStrings.MODE_NORMAL);
        JRadioButton warmEffectButton = new JRadioButton(UIStrings.MODE_CHAUD);
        JRadioButton coldEffectButton = new JRadioButton(UIStrings.MODE_FROID );
        normalEffectButton.setSelected(true);

        // Appliquer les actions
        normalEffectButton.addActionListener(e -> {
            //thermalEffect = "Normal";
            controleur.setMapThermalEffect(MapThermalEffect.NORMAL);
            mapPanelUpdateAction.actionPerformed(e);
        });
        warmEffectButton.addActionListener(e -> {
            controleur.setMapThermalEffect(MapThermalEffect.CHAUD);
            mapPanelUpdateAction.actionPerformed(e);
        });
        coldEffectButton.addActionListener(e -> {
            controleur.setMapThermalEffect(MapThermalEffect.FROID);
            mapPanelUpdateAction.actionPerformed(e);
        });

        effectGroup.add(normalEffectButton);
        effectGroup.add(warmEffectButton);
        effectGroup.add(coldEffectButton);
        thermalEffectPalette.add(normalEffectButton);
        thermalEffectPalette.add(warmEffectButton);
        thermalEffectPalette.add(coldEffectButton);


        // Ajout des contrôles au panneau principal des contrôles
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(accessPointPalette);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(barrierPalette);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(otherConfigPalette);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(thermalEffectPalette);
    }
}
