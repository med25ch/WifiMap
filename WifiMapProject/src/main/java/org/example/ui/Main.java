package org.example.ui;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.domain.Controleur;
import org.example.domain.elementsfactory.ConcreteMapElementFactory;
import org.example.domain.helpers.ServiceLocator;
import org.example.domain.utils.HeatMapLog;
import org.example.ui.containers.MainWindow;
import org.example.ui.containers.MapContainerPanel;

public class Main {

    public static void main(String[] args) {

        FlatLightLaf.setup();

        ServiceLocator serviceLocator = ServiceLocator.getInstance();

        serviceLocator.setService(ConcreteMapElementFactory.class, new ConcreteMapElementFactory());
        serviceLocator.setService(Controleur.class, new Controleur());
        serviceLocator.setService(MapContainerPanel.class, new MapContainerPanel());
        serviceLocator.setService(HeatMapLog.class, new HeatMapLog());

        MainWindow mainWindow = new MainWindow();
    }
}
