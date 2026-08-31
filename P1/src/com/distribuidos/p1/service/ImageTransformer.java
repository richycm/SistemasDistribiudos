package com.distribuidos.p1.service;

import com.distribuidos.p1.model.FilterType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Servicio encargado de la manipulación y transformación de imágenes.
 * Implementa algoritmos de procesamiento digital a nivel de píxel.
 */
public class ImageTransformer {

    /**
     * Procesa una imagen de origen aplicando el filtro especificado y guardándola en el destino.
     */
    public void processImage(File inputFile, File outputFile, FilterType filterType) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputFile);
        if (originalImage == null) {
            throw new IOException("El archivo no es una imagen válida o compatible: " + inputFile.getName());
        }

        BufferedImage transformedImage = applyFilter(originalImage, filterType);

        // Determinamos la extensión adecuada para guardar
        String fileName = outputFile.getName().toLowerCase();
        String format = "png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            format = "jpg";
        } else if (fileName.endsWith(".bmp")) {
            format = "bmp";
        }

        // Si es JPG y la imagen tiene canal alfa o tipo inadecuado, la convertimos a RGB estándar
        if (format.equals("jpg") && transformedImage.getType() != BufferedImage.TYPE_INT_RGB) {
            BufferedImage rgbImage = new BufferedImage(
                    transformedImage.getWidth(),
                    transformedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            rgbImage.createGraphics().drawImage(transformedImage, 0, 0, null);
            transformedImage = rgbImage;
        }

        ImageIO.write(transformedImage, format, outputFile);
    }

    /**
     * Aplica el filtro seleccionado a la imagen en memoria.
     */
    public BufferedImage applyFilter(BufferedImage src, FilterType filterType) {
        int width = src.getWidth();
        int height = src.getHeight();

        return switch (filterType) {
            case GRAYSCALE -> applyGrayscale(src, width, height);
            case SEPIA -> applySepia(src, width, height);
            case INVERT -> applyInvert(src, width, height);
            case EDGE_DETECTION -> applySobelEdgeDetection(src, width, height);
        };
    }

    /**
     * Convierte a escala de grises usando la fórmula perceptual estándar de luminancia ITU-R BT.601:
     * Y = 0.299*R + 0.587*G + 0.114*B
     */
    private BufferedImage applyGrayscale(BufferedImage src, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                int grayRgb = (gray << 16) | (gray << 8) | gray;
                result.setRGB(x, y, grayRgb);
            }
        }
        return result;
    }

    /**
     * Aplica un filtro cálido sepia con ponderaciones cromáticas estándar.
     */
    private BufferedImage applySepia(BufferedImage src, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

                int sr = Math.min(255, tr);
                int sg = Math.min(255, tg);
                int sb = Math.min(255, tb);

                int sepiaRgb = (sr << 16) | (sg << 8) | sb;
                result.setRGB(x, y, sepiaRgb);
            }
        }
        return result;
    }

    /**
     * Invierte los colores de la imagen (efecto negativo).
     */
    private BufferedImage applyInvert(BufferedImage src, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = src.getRGB(x, y);
                int r = 255 - ((rgb >> 16) & 0xFF);
                int g = 255 - ((rgb >> 8) & 0xFF);
                int b = 255 - (rgb & 0xFF);

                int invRgb = (r << 16) | (g << 8) | b;
                result.setRGB(x, y, invRgb);
            }
        }
        return result;
    }

    /**
     * Detección de bordes mediante operador Sobel (Convolución 3x3).
     */
    private BufferedImage applySobelEdgeDetection(BufferedImage src, int width, int height) {
        BufferedImage gray = applyGrayscale(src, width, height);
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        int[][] gx = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };

        int[][] gy = {
                {-1, -2, -1},
                { 0,  0,  0},
                { 1,  2,  1}
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int sumX = 0;
                int sumY = 0;

                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int pixelVal = gray.getRGB(x + kx, y + ky) & 0xFF;
                        sumX += gx[ky + 1][kx + 1] * pixelVal;
                        sumY += gy[ky + 1][kx + 1] * pixelVal;
                    }
                }

                int magnitude = (int) Math.sqrt((sumX * sumX) + (sumY * sumY));
                magnitude = Math.min(255, Math.max(0, magnitude));

                int edgeRgb = (magnitude << 16) | (magnitude << 8) | magnitude;
                result.setRGB(x, y, edgeRgb);
            }
        }

        return result;
    }
}
