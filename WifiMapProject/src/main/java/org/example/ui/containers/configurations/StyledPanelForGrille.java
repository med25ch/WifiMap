package org.example.ui.containers.configurations;

import org.example.domain.Controleur;
import org.example.domain.helpers.ServiceLocator;
import org.example.domain.utils.HeatMapExceptions;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.example.ui.UIUtils.createStyledButton;

public class StyledPanelForGrille extends JPanel {

    private final Controleur controleur;
    private JLabel errorLabel;

    public StyledPanelForGrille(String title, ActionListener mapPanelUpdateAction) {
        this.controleur = ServiceLocator.getInstance().getService(Controleur.class);
        initPanel(title, mapPanelUpdateAction);
    }

    private void initPanel(String title, ActionListener mapPanelUpdateAction) {

        setLayout(new GridLayout(6, 1, 10, 10));
        this.setBackground(Color.WHITE);
        this.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(100, 150, 200), 2, true), title),
                new EmptyBorder(10, 10, 10, 10)
        ));

        errorLabel = new JLabel();

        JCheckBox toggleGridButton = new JCheckBox("Afficher Grille", controleur.getGrilleState());
        toggleGridButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        toggleGridButton.setBackground(new Color(245, 250, 255));
        toggleGridButton.addActionListener(e -> {
            controleur.activateGrille(toggleGridButton.isSelected());
            mapPanelUpdateAction.actionPerformed(e);
        });

        // Boutons pour activer ou désactiver la grille
        JCheckBox toggleMagheticGridButton = new JCheckBox("Magnétique", false);
        toggleMagheticGridButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        toggleMagheticGridButton.setBackground(new Color(245, 250, 255));
        toggleMagheticGridButton.addActionListener(e -> {
            controleur.activateGrilleMagnetisme(toggleMagheticGridButton.isSelected());
        });

        JLabel tailleLabel = new JLabel("Taille :");
        JTextField tailleField = new JTextField(String.valueOf(controleur.getGrilleSize()));


        JButton appliquerButton = createStyledButton("Appliquer");
        appliquerButton.addActionListener(e -> {
            try {
                int taille = Integer.parseInt(tailleField.getText());

                if(taille > controleur.getMapHeight() || taille > controleur.getMapWidth()){
                    throw new HeatMapExceptions.PointOutOfMapRangeException("Attention : verifier la taille de la grille !");
                }


                // Met directement la taille dans la grille
                controleur.setTailleGrille(taille);

                // On respecte l'état de la checkbox "afficher"
                controleur.activateGrille(toggleGridButton.isSelected());

                // Rafraîchir l'affichage
                mapPanelUpdateAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "update"));

                errorLabel.setVisible(false);

            } catch (NumberFormatException ex) {
                errorLabel.setText("Attention : Entrez un nombre entier!");
                errorLabel.setForeground(Color.RED);
                errorLabel.setVisible(true);
            } catch (HeatMapExceptions.PointOutOfMapRangeException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setForeground(Color.RED);
                errorLabel.setVisible(true);

            }
        });

        this.add(tailleLabel);
        this.add(tailleField);
        this.add(toggleGridButton);
        this.add(toggleMagheticGridButton);
        this.add(appliquerButton);
        this.add(errorLabel);
    }
}
