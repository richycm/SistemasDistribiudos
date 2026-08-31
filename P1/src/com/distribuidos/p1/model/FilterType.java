package com.distribuidos.p1.model;

/**
 * Representa los diferentes tipos de filtros aplicables a las imágenes.
 */
public enum FilterType {
    GRAYSCALE("Escala de Grises (Luminancia)", "Convierte cada píxel a su equivalente en escala de grises usando pesos perceptuales."),
    SEPIA("Efecto Sepia (Vintage)", "Aplica una matriz de transformación de tonos cálidos/retro."),
    INVERT("Inversión de Colores (Negativo)", "Invierte los canales RGB (255 - valor)."),
    EDGE_DETECTION("Detección de Bordes (Sobel)", "Aplica un filtro convolucional para resaltar bordes y contornos.");

    private final String displayName;
    private final String description;

    FilterType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
