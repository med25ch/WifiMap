package org.example.ui.containers.menubar;

import org.example.domain.utils.HeatMapExceptions;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewProjectDialog {
    private final JFrame parentFrame;
    private final ActionListener onCreateCallback;
    private final int MAX_WIDTH = 500;
    private final int MAX_HEIGHT = 500;
    private final int MIN_WIDTH = 50;
    private final int MIN_HEIGHT = 50;
    private JLabel errorLabel;

    public NewProjectDialog(JFrame parentFrame, ActionListener onCreateCallback) {
        this.parentFrame = parentFrame;
        this.onCreateCallback = onCreateCallback;
        openNewProjectDialog();
    }

    private void openNewProjectDialog() {
        JDialog dialog = createDialog();
        JPanel mainPanel = createMainPanel();

        JTextField nameField = new JTextField("Nouveau projet");
        JTextField widthField = new JTextField("500");
        JTextField heightField = new JTextField("500");

        mainPanel.add(createTitleLabel(), BorderLayout.NORTH);
        mainPanel.add(createFieldsPanel(nameField, widthField, heightField), BorderLayout.CENTER);

        errorLabel = createErrorLabel();
        JPanel bottomPanel = createBottomPanel(dialog, nameField, widthField, heightField);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    private JDialog createDialog() {
        JDialog dialog = new JDialog(parentFrame, "Nouveau projet", true);
        dialog.setSize(400, 330);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        return dialog;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("Nouveau projet", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        return titleLabel;
    }

    private JPanel createFieldsPanel(JTextField nameField, JTextField widthField, JTextField heightField) {
        JPanel fields = new JPanel(new GridLayout(3, 2, 10, 10));
        fields.add(new JLabel("Nom du projet :"));
        fields.add(nameField);
        fields.add(new JLabel("Largeur (m) :"));
        fields.add(widthField);
        fields.add(new JLabel("Hauteur (m) :"));
        fields.add(heightField);
        return fields;
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ", SwingConstants.CENTER); // Space to reserve height
        label.setForeground(Color.RED);
        label.setPreferredSize(new Dimension(0, 12));
        return label;
    }

    private JPanel createBottomPanel(JDialog dialog, JTextField nameField, JTextField widthField, JTextField heightField) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Annuler");
        styleButton(cancelButton, new Color(220, 53, 69));
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton createButton = new JButton("Créer");
        styleButton(createButton, new Color(40, 167, 69));
        createButton.addActionListener(e -> handleCreate(dialog, nameField, widthField, heightField));

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.add(errorLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        return bottomPanel;
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 40));
    }

    private void handleCreate(JDialog dialog, JTextField nameField, JTextField widthField, JTextField heightField) {
        try {
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());
            String name = nameField.getText().trim();

            if (width > MAX_WIDTH || height > MAX_HEIGHT) {
                throw new HeatMapExceptions.InvalidMapSizeException("Les dimensions maximales sont 500x500.");
            }
            if (width < MIN_WIDTH || height < MIN_HEIGHT) {
                throw new HeatMapExceptions.InvalidMapSizeException("Les dimensions minimales sont 50x50.");
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Le nom ne doit pas être vide.");
            }

            dialog.dispose();
            onCreateCallback.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, width + "," + height + "," + name));
        } catch (NumberFormatException ex) {
            errorLabel.setText("Veuillez entrer des nombres valides pour largeur et hauteur.");
        } catch (HeatMapExceptions.InvalidMapSizeException | IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }
}
