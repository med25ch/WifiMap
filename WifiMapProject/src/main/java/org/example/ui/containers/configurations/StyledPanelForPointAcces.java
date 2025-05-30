package org.example.ui.containers.configurations;

import org.example.domain.Controleur;
import org.example.domain.helpers.ServiceLocator;
import org.example.domain.pointacces.Frequence;
import org.example.domain.pointacces.PointAccesDTO;
import org.example.domain.utils.HeatMapExceptions;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

import static org.example.domain.utils.MapUtils.roundBasedOnFraction;
import static org.example.ui.UIUtils.createStyledButton;

public class StyledPanelForPointAcces extends JPanel {

    private final Controleur controleur;
    JLabel errorLabel;
    JSlider sliderPuissanceDbm;
    JComboBox<Frequence> frequencyComboBox;
    JTextField xAxisField;
    JTextField yAxisField;
    JButton accessPointSupprimerButton;
    JButton accessPointApplyButton;

    public StyledPanelForPointAcces(String title, ActionListener mapPanelUpdateAction) {
        this.controleur = ServiceLocator.getInstance().getService(Controleur.class);
        initPanel(title, mapPanelUpdateAction);
    }

    private void initPanel(String title, ActionListener mapPanelUpdateAction) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Set titled border with blue color
        setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(34, 133, 225), 2, true), title, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,new Font("SansSerif", Font.BOLD, 13), null),
                new EmptyBorder(10, 10, 10, 10)
        ));
        setBackground(Color.WHITE);




        errorLabel = new JLabel("Attention : Veuillez vérifier les entrées!");
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);


        // Puissance
        JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sliderPuissanceDbm = new JSlider(JSlider.HORIZONTAL, 11, 20, 15);
        sliderPuissanceDbm.setMajorTickSpacing(1);
        sliderPuissanceDbm.setPaintTicks(true);
        sliderPuissanceDbm.setPaintLabels(true);

        sliderPanel.add(new JLabel("Puissance (dBm):   "));
        sliderPanel.add(sliderPuissanceDbm);
        sliderPanel.setBackground(Color.WHITE);

        // Frequence
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        frequencyComboBox = new JComboBox<>(Frequence.values());
        comboPanel.add(new JLabel("Fréquence (GHz):   "));
        comboPanel.add(frequencyComboBox);
        comboPanel.setBackground(Color.WHITE);

        // X
        JPanel textFieldPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textFieldPanel1.add(new JLabel("X-Axis:                    "));
        xAxisField = new JTextField(6);
        textFieldPanel1.add(xAxisField);
        textFieldPanel1.setBackground(Color.WHITE);


        //Y
        JPanel textFieldPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textFieldPanel2.add(new JLabel("Y-Axis:                    "));
        yAxisField = new JTextField(6);
        textFieldPanel2.add(yAxisField);
        textFieldPanel2.setBackground(Color.WHITE);


        // Apply / Delete
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        accessPointApplyButton = createStyledButton("Appliquer");
        buttonPanel.add(accessPointApplyButton);
        accessPointSupprimerButton = createStyledButton("Supprimer");
        accessPointSupprimerButton.setEnabled(false);

        buttonPanel.add(accessPointSupprimerButton);
        buttonPanel.setBackground(Color.WHITE);


        controleur.addActionListener(e -> {
            var point = controleur.getSelectedElement();
            if(point instanceof PointAccesDTO myPoint){
                xAxisField.setText(String.valueOf(roundBasedOnFraction(myPoint.x())));
                yAxisField.setText(String.valueOf(roundBasedOnFraction(myPoint.y())));
                frequencyComboBox.setSelectedItem(myPoint.frequence());
                sliderPuissanceDbm.setValue(myPoint.puissanceEmission());
                accessPointSupprimerButton.setEnabled(true);
            } else {
                resetInputs();
            }
        });



        accessPointApplyButton.addActionListener(e -> {

            try {
                var puisance = sliderPuissanceDbm.getValue();
                var frequence = (Frequence) frequencyComboBox.getSelectedItem();
                var x = roundBasedOnFraction(Double.parseDouble(xAxisField.getText()));
                var y = roundBasedOnFraction(Double.parseDouble(yAxisField.getText()));

                //
                if(!controleur.isPointInsideMap(x, y)) {
                    throw new HeatMapExceptions.PointOutOfMapRangeException("Attention : un point est hors carte!");
                }

                if(controleur.isSelectionModePointAcces()){
                    controleur.updateSelectedElement(x, y,puisance,frequence);
                }else {
                    // Ajouter un point avec Panneau
                    controleur.addPoint(x, y,frequence,puisance);
                }
                errorLabel.setVisible(false);
                mapPanelUpdateAction.actionPerformed(e);
            } catch (NumberFormatException ex) {
                errorLabel.setVisible(true);
            } catch (HeatMapExceptions.PointOutOfMapRangeException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });



        accessPointSupprimerButton.addActionListener(e -> {
            try {
                if(controleur.isSelectionModePointAcces()){
                    controleur.supprimerSelectedElement();
                    mapPanelUpdateAction.actionPerformed(e); // Mettre à jour l'affichage
                    errorLabel.setVisible(false);
                    resetInputs();
                    accessPointSupprimerButton.setEnabled(false);
                }
            } catch (NumberFormatException ex) {
                errorLabel.setVisible(true);
            }
        });

        sliderPuissanceDbm.addChangeListener(l -> {
            controleur.setSelectedPuissance(sliderPuissanceDbm.getValue());
        });

        frequencyComboBox.addItemListener(l -> {
            Frequence frequence = (Frequence)frequencyComboBox.getSelectedItem();
            controleur.setSelectedFrequence(frequence);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(errorLabel);
        bottomPanel.setBackground(Color.WHITE);

        // Add all panels to main panel
        this.add(sliderPanel);
        this.add(comboPanel);
        this.add(textFieldPanel1);
        this.add(textFieldPanel2);
        this.add(buttonPanel);
        this.add(bottomPanel);
    }

    private void resetInputs(){
        xAxisField.setText("");
        yAxisField.setText("");
        sliderPuissanceDbm.setValue(15);
        accessPointSupprimerButton.setEnabled(false);
    }
}
