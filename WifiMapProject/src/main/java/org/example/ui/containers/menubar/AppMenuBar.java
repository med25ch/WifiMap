package org.example.ui.containers.menubar;

import org.example.domain.Controleur;
import org.example.domain.helpers.ServiceLocator;
import org.example.ui.containers.MainWindow;
import org.example.domain.savemanager.SaveManager;
import org.example.ui.map.ExportMapToPng;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class AppMenuBar extends JMenuBar {

    private final MainWindow mainWindow;

    private final JMenuItem saveProject;
    private final JMenuItem saveProjectAs;

    private final JMenuItem newProject;
    private final JMenuItem openProject;
    private final JMenuItem exportImage;

    public AppMenuBar(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        JMenu fileMenu = new JMenu("Fichier");
        JMenu editMenu = new JMenu("Édition");

        newProject = new JMenuItem("Nouveau projet");
        openProject = new JMenuItem("Ouvrir un projet");
        saveProject = new JMenuItem("Enregistrer le projet");
        saveProjectAs = new JMenuItem("Enregistrer le projet sous");
        exportImage = new JMenuItem("Exporter l'image");

        exportImage.setEnabled(false);
        saveProject.setEnabled(false);
        saveProjectAs.setEnabled(false);

        int shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(); // ctrl devient cmd si mac

        fileMenu.add(newProject);
        fileMenu.add(openProject);
        fileMenu.addSeparator();

        saveProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutKeyMask));
        saveProjectAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutKeyMask | InputEvent.SHIFT_DOWN_MASK));

        fileMenu.add(saveProject);
        fileMenu.add(saveProjectAs);
        fileMenu.addSeparator();
        fileMenu.add(exportImage);

        JMenuItem undoAction = new JMenuItem("Annuler");
        undoAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcutKeyMask));

        undoAction.addActionListener(e -> {
            Controleur controleur = ServiceLocator.getInstance().getService(Controleur.class);
            controleur.undo();
        });

        JMenuItem redoAction = new JMenuItem("Rétablir");
        redoAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcutKeyMask | InputEvent.SHIFT_DOWN_MASK));

        redoAction.addActionListener(e -> {
            Controleur controleur = ServiceLocator.getInstance().getService(Controleur.class);
            controleur.redo();
        });

        editMenu.add(undoAction);
        editMenu.add(redoAction);

        add(fileMenu);
        add(editMenu);

        newProject.addActionListener(e -> new NewProjectDialog(mainWindow, event -> {
            String[] parts = event.getActionCommand().split(",");
            int width = Integer.parseInt(parts[0]);
            int height = Integer.parseInt(parts[1]);
            String name = parts[2];
            mainWindow.createMap(width, height, name, true);
            saveProject.setEnabled(false);
            saveProjectAs.setEnabled(true);
            exportImage.setEnabled(true);
        }));

        saveProject.addActionListener(e -> {
            Controleur controleur = ServiceLocator.getInstance().getService(Controleur.class);
            String fullPath = SaveManager.getLastLoadPath();
            controleur.saveProject(fullPath);
        });

        saveProjectAs.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                Controleur controleur = ServiceLocator.getInstance().getService(Controleur.class);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Sauvegarder le projet");
                fileChooser.setSelectedFile(new File(controleur.getProjectName()));
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SER files", "ser"));

                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selected = fileChooser.getSelectedFile();
                    if (!selected.getName().toLowerCase().endsWith(".ser")) {
                        selected = new File(selected.getAbsolutePath() + ".ser");
                    }
                    mainWindow.saveProjectAs(selected);
                    saveProject.setEnabled(true);
                }
            });
        });


        openProject.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Ouvrir un projet");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers SER", "ser"));

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    mainWindow.openProject(file);
                    saveProject.setEnabled(true);
                    saveProjectAs.setEnabled(true);
                    exportImage.setEnabled(true);
                }
            });
        });

        exportImage.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                ExportMapToPng export = new ExportMapToPng();
                JFileChooser fileChooser = new JFileChooser();

                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setFileFilter(new FileNameExtensionFilter("images", "png"));

                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        export.export(fileChooser.getSelectedFile());
                    } catch (IOException ex) {
                        System.err.printf("Erreur lors de l'export de l'image %s\n", ex.getMessage());
                    }
                }
            });
        });
    }


    public JMenuItem getSaveProject() {
        return saveProject;
    }

    public JMenuItem getSaveProjectAs() {
        return saveProjectAs;
    }

    public JMenuItem getNewProject() {
        return newProject;
    }

    public JMenuItem getOpenProject() {
        return openProject;
    }

    public JMenuItem getExportImage() {
        return exportImage;
    }
}