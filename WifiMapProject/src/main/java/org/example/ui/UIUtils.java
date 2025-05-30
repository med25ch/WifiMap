package org.example.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URL;

public class UIUtils {

    // Méthode utilitaire pour créer des boutons avec un style avancé
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(34, 133, 225));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ajout d'effet au survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 100, 160));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(34, 133, 225));
            }
        });
        return button;
    }

    // Méthode utilitaire pour créer des palettes stylées
    public static JPanel createStyledPalette(String title) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(34, 133, 225), 2, true), title, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,new Font("SansSerif", Font.BOLD, 13), null),
                new EmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    public static JPanel createStyledPalettePointAccess(String title) {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(100, 150, 200), 2, true), title),
                new EmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    public static int roundToInt(double value) {
        return (int) Math.round(value);
    }

    public static ImageIcon loadIcon(String path, int width, int height) {

        URL imageUrl = UIUtils.class.getResource(path); // or use ClassLoader

        if (imageUrl == null) {
            System.err.println("Could not find resource: " + path);
            return null;
        }

        ImageIcon originalIcon = new ImageIcon(imageUrl);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

}
