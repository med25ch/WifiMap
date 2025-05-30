package org.example.ui.map;

import org.example.domain.Controleur;
import org.example.domain.barrieres.BarriereDTO;
import org.example.domain.helpers.ServiceLocator;
import org.example.domain.pointacces.PointAccesDTO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ExportMapToPng
{
    private Controleur controleur;
    public ExportMapToPng()
    {
        controleur = ServiceLocator.getInstance().getService(Controleur.class);
    }

    public void export(File file) throws IOException
    {
        int width = controleur.getMapWidth(), height = controleur.getMapHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        // Cellule
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.SrcOver);
        int[][] colorMatrix = controleur.getColorMatrix();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g.setColor(new Color(colorMatrix[x][y], true));
                g.fillRect(x, y, 1, 1);
            }
        }

        // Barrierer
        for (BarriereDTO barriere : controleur.getBarrieresDTOList()) {
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke((float) barriere.largeur() / 100));
            int yStart = barriere.yStart();
            int yEnd = barriere.yEnd();
            g.drawLine(barriere.xStart(), yStart, barriere.xEnd(), yEnd);
        }

        // Point d'accès
        g.setColor(Color.RED);
        for (PointAccesDTO pa : controleur.getPointsAccesDTOList()) {
            g.fillOval((int) (pa.x() - 2),(int) ((pa.y()) - 2), 5, 5);
        }

        // On remet l'image en place
        BufferedImage miror = new BufferedImage(width, height, image.getType());
        Graphics2D mirrorGraphic = miror.createGraphics();

        mirrorGraphic.drawImage(image, 0, 0, width, height, 0, height, width, 0, null);
        mirrorGraphic.dispose();

        // On gère l'enregistrement de l'image dans le disc
        if (file.isDirectory()) file = new File(file, controleur.getProjectName()+".png");
        if(!file.getAbsolutePath().endsWith(".png")) file = new File(file.getAbsolutePath()+".png");
        ImageIO.write(miror, "png", file);
        g.dispose();
    }
}
