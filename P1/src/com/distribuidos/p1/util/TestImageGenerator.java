package com.distribuidos.p1.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Generador automático de imágenes sintéticas de prueba.
 * Permite crear lotes de imágenes coloridas con diferentes resoluciones y patrones geométricos
 * para realizar pruebas de rendimiento y benchmarking de concurrencia inmediatamente.
 */
public class TestImageGenerator {

    /**
     * Genera un lote de imágenes en la carpeta indicada.
     *
     * @param targetDirectory Directorio de destino.
     * @param count           Cantidad de imágenes a generar.
     * @param width           Ancho en píxeles.
     * @param height          Alto en píxeles.
     */
    public static void generateSampleImages(File targetDirectory, int count, int width, int height) throws IOException {
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Renderizado con calidad
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Gradiente de fondo
            Color color1 = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            Color color2 = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            GradientPaint gradient = new GradientPaint(0, 0, color1, width, height, color2);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);

            // Figuras geométricas aleatorias
            for (int shape = 0; shape < 15; shape++) {
                g2d.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 180));
                int shapeType = random.nextInt(3);
                int sx = random.nextInt(width - 50);
                int sy = random.nextInt(height - 50);
                int sw = 30 + random.nextInt(120);
                int sh = 30 + random.nextInt(120);

                if (shapeType == 0) {
                    g2d.fillOval(sx, sy, sw, sh);
                } else if (shapeType == 1) {
                    g2d.fillRect(sx, sy, sw, sh);
                } else {
                    g2d.fillRoundRect(sx, sy, sw, sh, 20, 20);
                }
            }

            // Texto identificador
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2d.drawString("Sistemas Distribuidos P1", 30, 45);
            g2d.drawString(String.format("Muestra #%03d - %dx%d", i, width, height), 30, 80);

            g2d.dispose();

            File outFile = new File(targetDirectory, String.format("sample_img_%03d.png", i));
            ImageIO.write(image, "png", outFile);
        }
    }

    public static void main(String[] args) throws IOException {
        int count = args.length > 0 ? Integer.parseInt(args[0]) : 10;
        File dir = new File("muestras_prueba");
        System.out.println("Generando " + count + " imágenes de prueba en " + dir.getAbsolutePath() + "...");
        generateSampleImages(dir, count, 640, 480);
        System.out.println("Completado exitosamente.");
    }
}
