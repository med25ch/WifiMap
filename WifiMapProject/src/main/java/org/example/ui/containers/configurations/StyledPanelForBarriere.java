package org.example.ui.containers.configurations;

import org.example.domain.Controleur;
import org.example.domain.barrieres.BarriereDTO;
import org.example.domain.barrieres.Material;
import org.example.domain.barrieres.Orientation;
import org.example.domain.helpers.ServiceLocator;
import org.example.domain.pointacces.Frequence;
import org.example.domain.utils.HeatMapExceptions;
import org.example.domain.utils.HeatMapLog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

import static org.example.domain.utils.MapUtils.roundBasedOnFraction;
import static org.example.ui.UIUtils.createStyledButton;

public class StyledPanelForBarriere extends JPanel {

    private HeatMapLog heatMapLog;
    private final Controleur controleur;
    private JLabel epaisseurLabel;
    private JLabel errorLabel;
    private JComboBox<Orientation> orientationComboBox;
    private JComboBox<Material> materialComboBox;
    private JButton deleteButton;
    JTextField x1Field;
    JTextField y1Field;
    JTextField x2Field;
    JTextField y2Field;
    JSlider epaisseurJslider;

    public StyledPanelForBarriere(String title, ActionListener mapPanelUpdateAction) {

        this.controleur = ServiceLocator.getInstance().getService(Controleur.class);
        this.heatMapLog = ServiceLocator.getInstance().getService(HeatMapLog.class);
        initPanel(title, mapPanelUpdateAction);
    }

    private void initPanel(String title, ActionListener mapPanelUpdateAction) {
        // Panel Configuration
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(34, 133, 225), 2, true), title, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,new Font("SansSerif", Font.BOLD, 13), null),
                new EmptyBorder(10, 10, 10, 10)
        ));
        setBackground(Color.WHITE);


        // Largeur slider
        JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        epaisseurLabel = new JLabel("Largeur en (cm):");
        epaisseurJslider = new JSlider(JSlider.HORIZONTAL, 30, 60, 30);
        epaisseurJslider.setMajorTickSpacing(5);
        epaisseurJslider.setPaintTicks(true);
        epaisseurJslider.setPaintLabels(true);
        sliderPanel.add(epaisseurLabel);
        sliderPanel.add(epaisseurJslider);

        // Material
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel materialLabel = new JLabel("Matériaux:           ");
        materialComboBox = new JComboBox<>(Material.values());
        comboPanel.add(materialLabel);
        comboPanel.add(materialComboBox);

        // Orientation
        JPanel comboPanelOrientation = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel orientationLabel = new JLabel("Orientation:         ");
        orientationComboBox = new JComboBox<>(Orientation.values());
        comboPanelOrientation.add(orientationLabel);
        comboPanelOrientation.add(orientationComboBox);


        JPanel textFieldPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel xAxisLabel1 = new JLabel("Point D : (x,y)      ");
        x1Field = new JTextField(5);
        y1Field = new JTextField(5);
        textFieldPanel1.add(xAxisLabel1);
        textFieldPanel1.add(x1Field);
        textFieldPanel1.add(y1Field);

        JPanel textFieldPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel yAxisLabel3 = new JLabel("Point F : (x,y)       ");
        x2Field = new JTextField(5);
        y2Field = new JTextField(5);
        textFieldPanel2.add(yAxisLabel3);
        textFieldPanel2.add(x2Field);
        textFieldPanel2.add(y2Field);

        //Apply / delete
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton applyButton = createStyledButton("Appliquer");
        deleteButton = createStyledButton("Supprimer");
        buttonPanel.add(applyButton);
        buttonPanel.add(deleteButton);

        // Error label
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        errorLabel = new JLabel("Attention : Veuillez vérifier les entrées!");
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);
        bottomPanel.add(errorLabel);


        updateFields();

        //Apply white bg :
        sliderPanel.setBackground(Color.WHITE);
        comboPanel.setBackground(Color.WHITE);
        comboPanelOrientation.setBackground(Color.WHITE);
        textFieldPanel1.setBackground(Color.WHITE);
        textFieldPanel2.setBackground(Color.WHITE);
        buttonPanel.setBackground(Color.WHITE);
        bottomPanel.setBackground(Color.WHITE);

        this.add(sliderPanel);
        this.add(comboPanel);
        this.add(comboPanelOrientation);
        this.add(textFieldPanel1);
        this.add(textFieldPanel2);
        this.add(buttonPanel);
        this.add(bottomPanel);


        orientationComboBox.addActionListener(e -> {
            updateFields();
        });

        applyButton.addActionListener(e -> {
            try {
                updateFields();

                //Updating selected barrier
                if(controleur.isSelectionModeBarriere()){
                    heatMapLog.logFine("Selection mode active");
                    var epaisseur = (double) epaisseurJslider.getValue();
                    var material = (Material) materialComboBox.getSelectedItem();
                    var orientation = (Orientation) orientationComboBox.getSelectedItem();
                    var x1 = roundBasedOnFraction(Double.parseDouble(x1Field.getText()));
                    var x2 = roundBasedOnFraction(Double.parseDouble(x2Field.getText()));
                    var y1 = roundBasedOnFraction(Double.parseDouble(y1Field.getText()));
                    var y2 = roundBasedOnFraction(Double.parseDouble(y2Field.getText()));

                    //Validate if starPoint and endPoint are in the map range
                    if(controleur.isPointInsideMap(x1, y1) && controleur.isPointInsideMap(x2, y2)){
                        controleur.updateSelectedElement(x1, y1, x2, y2,material,orientation,epaisseur);
                    }else {
                        throw new HeatMapExceptions.PointOutOfMapRangeException("Attention : un point est hors carte!");
                    }

                }else {
                    //Adding new barrier
                    heatMapLog.logFine("Creation mode active");
                    var barriereDTO = getBarriereDTO();
                    var isDuplicate = controleur.isBarriereExists(barriereDTO);
                    var isInsideMap = controleur.isPointInsideMap(barriereDTO.xStart(), barriereDTO.yEnd())
                            && controleur.isPointInsideMap(barriereDTO.xEnd(), barriereDTO.yEnd());
                    if(!isDuplicate){
                        if(isInsideMap){
                            controleur.addBarriere(barriereDTO);
                        }else {
                            throw new HeatMapExceptions.PointOutOfMapRangeException("Attention : un point est hors carte!");
                        }
                    }else {
                        throw new HeatMapExceptions.ExistingBarriereException("Attention : Barrière correspondante existante!");
                    }
                }

                errorLabel.setVisible(false);
                mapPanelUpdateAction.actionPerformed(e);
                resetFields();

            } catch (NumberFormatException ex) {
                errorLabel.setText("Attention : Veuillez vérifier les entrées!");
                errorLabel.setVisible(true);
            } catch (HeatMapExceptions.ExistingBarriereException | HeatMapExceptions.PointOutOfMapRangeException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        deleteButton.setEnabled(false); // Initially disabled


        controleur.addActionListener(e -> {
            var barrier = controleur.getSelectedElement();
            if(barrier instanceof BarriereDTO myBarrier) {
                epaisseurJslider.setValue((int) Math.round(myBarrier.largeur()));
                x1Field.setText(String.valueOf(myBarrier.xStart()));
                y1Field.setText(String.valueOf(myBarrier.yStart()));
                x2Field.setText(String.valueOf(myBarrier.xEnd()));
                y2Field.setText(String.valueOf(myBarrier.yEnd()));
                materialComboBox.setSelectedItem(myBarrier.materiel());
                orientationComboBox.setSelectedItem(myBarrier.orientation());
                deleteButton.setEnabled(true);
            } else {
                resetFields();
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                if(controleur.isSelectionModeBarriere()){
                    controleur.supprimerSelectedElement();
                    mapPanelUpdateAction.actionPerformed(e);
                    deleteButton.setEnabled(false);
                    resetFields();
                }
            } catch (NumberFormatException ex) {
                errorLabel.setVisible(true);
            }
        });

        materialComboBox.addItemListener(l -> {
            Material material = (Material) materialComboBox.getSelectedItem();
            controleur.setSelectedMateriel(material);
        });

        epaisseurJslider.addChangeListener(l -> {
            controleur.setSelectedEpaisseur(epaisseurJslider.getValue());
        });
    }

    private BarriereDTO getBarriereDTO() {
        var epaisseur = epaisseurJslider.getValue();
        var material = (Material) materialComboBox.getSelectedItem();
        var orientation = (Orientation) orientationComboBox.getSelectedItem();
        var x1 = roundBasedOnFraction(Double.parseDouble(x1Field.getText()));
        var x2 = roundBasedOnFraction(Double.parseDouble(x2Field.getText()));
        var y1 = roundBasedOnFraction(Double.parseDouble(y1Field.getText()));
        var y2 = roundBasedOnFraction(Double.parseDouble(y2Field.getText()));
        return new BarriereDTO(null, material, epaisseur,orientation,x1,y1,x2,y2);
    }


    private void updateFields() {
        var selectedOrientation = (Orientation) orientationComboBox.getSelectedItem();
        if(selectedOrientation == Orientation.HORIZONTAL) {
            y2Field.setText(y1Field.getText()); // Auto-fill yEnd
            y2Field.setEnabled(false);
            x2Field.setEnabled(true);
        }else {
            x2Field.setText(x1Field.getText()); // Auto-fill xEnd
            x2Field.setEnabled(false);
            y2Field.setEnabled(true);
        }

        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    private void resetFields() {
        x1Field.setText("");
        y1Field.setText("");
        x2Field.setText("");
        y2Field.setText("");
        epaisseurJslider.setValue(30);
        errorLabel.setVisible(false);
        deleteButton.setEnabled(false);
    }

}
