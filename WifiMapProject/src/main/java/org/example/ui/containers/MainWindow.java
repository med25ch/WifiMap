package org.example.ui.containers;

import org.example.domain.Controleur;
import org.example.domain.helpers.ServiceLocator;
import org.example.ui.listeners.MapPanelUpdateAction;
import org.example.ui.containers.menubar.AppMenuBar;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.example.ui.UIUtils.loadIcon;

public class MainWindow extends JFrame {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel navBar;
    private JLabel navTitle;
    private AppMenuBar menuBar;


    private MapContainerPanel mapContainerPanel;
    private MapPanelUpdateAction mapPanelUpdateAction;

    public MainWindow() throws HeadlessException {

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setMinimumSize(new Dimension(900, 600));

        mainPanel = new JPanel(new BorderLayout());
        setupNavBar();
        setupMenuBar();

        frame.setJMenuBar(menuBar);
        frame.add(mainPanel);
        setupWelcomeScreen();

        frame.setVisible(true);
        frame.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public JFrame getFrame() {
        return this.frame;
    }

    private void setupNavBar() {
        navBar = new JPanel(new BorderLayout());
        navBar.setBackground(new Color(34, 133, 225));
        navBar.setPreferredSize(new Dimension(0, 55));

        navTitle = new JLabel("\uD83D\uDCF6 WifiMap - Couverture Thermique", JLabel.CENTER);
        navTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        navTitle.setForeground(Color.WHITE);
        navBar.add(navTitle, BorderLayout.CENTER);
    }

    private void setupMenuBar() {
        menuBar = new AppMenuBar(this);
    }


    public void openProject(File file) {
        Controleur controleur = ServiceLocator.getInstance().getService(Controleur.class);
        controleur.loadProjectFile(file.getParent() + File.separator + file.getName(), file.getName());
        createMap(controleur.getMapWidth(), controleur.getMapHeight(), controleur.getProjectName(), false);
        repaint();
    }

    public void saveProjectAs(File file) {
        Controleur controleur = ServiceLocator.getInstance().getService(Controleur.class);
        String fullPath = file.getParent() + File.separator + file.getName();
        controleur.saveProject(fullPath);
    }

    private void setupWelcomeScreen() {
        mainPanel.removeAll();
        JPanel accueilPanel = new JPanel();
        accueilPanel.setLayout(new BoxLayout(accueilPanel, BoxLayout.Y_AXIS));
        accueilPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        Color bgColor = new Color(240, 245, 250);
        Color textColor = new Color(50, 50, 50);

        mainPanel.setBackground(bgColor);
        accueilPanel.setBackground(bgColor);

        JLabel welcomeLabel = new JLabel("Bienvenue sur WifiMap ");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setForeground(textColor);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(bgColor);
        btnPanel.setLayout(new GridLayout(0, 1, 10, 10));
        btnPanel.setMaximumSize(new Dimension(300, 150));

        var pathNewProject = "/pictures/new_project.png";
        var openProject = "/pictures/open_project.png";

        btnPanel.add(makeStyledButton("Nouveau projet",pathNewProject, this.menuBar.getNewProject()));
        btnPanel.add(makeStyledButton("Ouvrir un projet",openProject, this.menuBar.getOpenProject()));

        accueilPanel.add(welcomeLabel);
        // Add some vertical space
        accueilPanel.add(Box.createRigidArea(new Dimension(0, 100))); // 10 pixels of vertical space
        accueilPanel.add(btnPanel);

        mainPanel.add(navBar, BorderLayout.NORTH);
        mainPanel.add(accueilPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JButton makeStyledButton(String text, String iconPath,JMenuItem actionItem) {
        ImageIcon icon =  loadIcon(iconPath,32,32);
        JButton button = new JButton(text, icon);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setIconTextGap(10);

        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBackground(new Color(34, 133, 225));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(250, 40));
        button.addActionListener(e -> actionItem.doClick());
        return button;
    }

    public void createMap(int width, int height, String name, boolean shouldClearOldProject) {
        mainPanel.removeAll();
        Controleur controleur = ServiceLocator.getInstance().getService(Controleur.class);
        if (shouldClearOldProject) controleur.clearProject();
        controleur.newProject(width, height, name);
        mapContainerPanel = ServiceLocator.getInstance().getService(MapContainerPanel.class);
        mapContainerPanel.setBounds();

        JScrollPane mapContainerScrollPane = new JScrollPane(mapContainerPanel);
        mapContainerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        mapContainerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mapPanelUpdateAction = new MapPanelUpdateAction(mapContainerPanel.getMapPanel());
        ConfigurationsContainer controlPanel = new ConfigurationsContainer(mapPanelUpdateAction);
        JScrollPane scrollPane = new JScrollPane(controlPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(navBar, BorderLayout.NORTH);
        mainPanel.add(mapContainerScrollPane, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.EAST);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
