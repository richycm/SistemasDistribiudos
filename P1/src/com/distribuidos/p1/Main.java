package com.distribuidos.p1;

import com.distribuidos.p1.gui.MainWindow;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación Práctica 1 (Sistemas Distribuidos).
 */
public class Main {

    public static void main(String[] args) {
        // Establecer apariencia nativa del sistema para una mejor experiencia visual
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Si falla, se usa el LookAndFeel estándar de Java
        }

        // Ejecutar en el Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
