package org.example.ui.containers;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {

    private JLabel infosLabel;

    public InfoPanel() {
        infosLabel = new JLabel("dBm: N/A | X: N/A | Y: N/A");
        infosLabel.setPreferredSize(new Dimension(400, 20));
        setLayout(new BorderLayout());
        add(infosLabel, BorderLayout.SOUTH);
    }

    public void updateStatus(String status) {
        infosLabel.setText(status);
    }
}
