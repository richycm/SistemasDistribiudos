package com.distribuidos.p1.gui;

import com.distribuidos.p1.model.FilterType;
import com.distribuidos.p1.model.ProcessingStats;
import com.distribuidos.p1.service.BatchProcessingEngine;
import com.distribuidos.p1.service.ProgressListener;
import com.distribuidos.p1.util.TestImageGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Ventana Principal de la aplicación (Swing).
 * Proporciona controles interactivos, visualización en tiempo real,
 * historial de benchmarking y generación de datos de prueba.
 */
public class MainWindow extends JFrame implements ProgressListener {

    private final BatchProcessingEngine engine;
    private final int cpuCores;

    // Componentes de la interfaz
    private JTextField txtSourceFolder;
    private JLabel lblImageCount;
    private JComboBox<FilterType> cmbFilter;
    private JRadioButton rbSequential;
    private JRadioButton rbParallel;
    private JSpinner spinnerThreads;
    private JButton btnSelectFolder;
    private JButton btnGenerateTestImages;
    private JButton btnStart;
    private JButton btnCancel;
    private JProgressBar progressBar;
    private JLabel lblStatus;
    private JTextArea txtLogs;
    private JTable tableHistory;
    private DefaultTableModel historyModel;

    // Estado interno
    private List<File> currentImages = new ArrayList<>();
    private Long lastSequentialTimeMillis = null;

    public MainWindow() {
        this.engine = new BatchProcessingEngine();
        this.cpuCores = Runtime.getRuntime().availableProcessors();

        initUI();
    }

    private void initUI() {
        setTitle("Sistemas Distribuidos - Práctica 1: Hilos y Concurrencia");
        setSize(950, 720);
        setMinimumSize(new Dimension(850, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Contenedor principal con fondo limpio
        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        rootPanel.setBackground(new Color(245, 247, 250));

        // 1. Panel Superior: Encabezado y resumen del sistema
        rootPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Panel Central: Configuración, Acciones y Logs
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setOpaque(false);
        controlsPanel.add(createFolderSection());
        controlsPanel.add(Box.createVerticalStrut(8));
        controlsPanel.add(createConfigSection());
        controlsPanel.add(Box.createVerticalStrut(8));
        controlsPanel.add(createActionSection());

        centerPanel.add(controlsPanel, BorderLayout.NORTH);
        centerPanel.add(createProgressAndLogSection(), BorderLayout.CENTER);

        rootPanel.add(centerPanel, BorderLayout.CENTER);

        // 3. Panel Inferior: Historial de Benchmarking y Speedup
        rootPanel.add(createHistorySection(), BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(30, 41, 59));
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblTitle = new JLabel("Práctica 1: Procesamiento Concurrente y Multihilo de Imágenes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblHardware = new JLabel(String.format("Procesador: %d Núcleos / Hilos Lógicos detectados | JVM: Java %s",
                cpuCores, System.getProperty("java.version")));
        lblHardware.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHardware.setForeground(new Color(203, 213, 225));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblHardware, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFolderSection() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "1. Origen de Imágenes",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(51, 65, 85)
        ));

        txtSourceFolder = new JTextField();
        txtSourceFolder.setEditable(false);
        txtSourceFolder.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSourceFolder.setBackground(Color.WHITE);

        lblImageCount = new JLabel("Imágenes detectadas: 0");
        lblImageCount.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblImageCount.setForeground(new Color(100, 116, 139));

        JPanel leftSub = new JPanel(new BorderLayout(5, 5));
        leftSub.setOpaque(false);
        leftSub.add(txtSourceFolder, BorderLayout.CENTER);
        leftSub.add(lblImageCount, BorderLayout.SOUTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnPanel.setOpaque(false);

        btnSelectFolder = new JButton("📁 Seleccionar Carpeta");
        btnSelectFolder.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSelectFolder.setFocusPainted(false);
        btnSelectFolder.addActionListener(e -> chooseFolder());

        btnGenerateTestImages = new JButton("✨ Generar 25 Imágenes de Prueba");
        btnGenerateTestImages.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnGenerateTestImages.setFocusPainted(false);
        btnGenerateTestImages.addActionListener(e -> generateTestImagesPrompt());

        btnPanel.add(btnSelectFolder);
        btnPanel.add(btnGenerateTestImages);

        panel.add(leftSub, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createConfigSection() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "2. Parámetros de Procesamiento y Concurrencia",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(51, 65, 85)
        ));

        // Subpanel Filtro
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterPanel.setOpaque(false);
        JLabel lblF = new JLabel("Filtro Digital:");
        lblF.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmbFilter = new JComboBox<>(FilterType.values());
        cmbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(lblF);
        filterPanel.add(cmbFilter);

        // Subpanel Modo de Ejecución
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        modePanel.setOpaque(false);

        rbSequential = new JRadioButton("Secuencial (1 Hilo)");
        rbParallel = new JRadioButton("Paralelo (Multihilo)", true);
        rbSequential.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rbParallel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rbSequential.setOpaque(false);
        rbParallel.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        group.add(rbSequential);
        group.add(rbParallel);

        JLabel lblTh = new JLabel("Hilos:");
        lblTh.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        SpinnerNumberModel threadModel = new SpinnerNumberModel(cpuCores, 1, 64, 1);
        spinnerThreads = new JSpinner(threadModel);
        spinnerThreads.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ((JSpinner.DefaultEditor) spinnerThreads.getEditor()).getTextField().setColumns(3);

        rbSequential.addActionListener(e -> spinnerThreads.setEnabled(false));
        rbParallel.addActionListener(e -> spinnerThreads.setEnabled(true));

        modePanel.add(rbSequential);
        modePanel.add(rbParallel);
        modePanel.add(lblTh);
        modePanel.add(spinnerThreads);

        panel.add(filterPanel);
        panel.add(modePanel);
        return panel;
    }

    private JPanel createActionSection() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);

        btnStart = new JButton("▶ Iniciar Procesamiento");
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnStart.setBackground(new Color(22, 163, 74));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFocusPainted(false);
        btnStart.setEnabled(false);
        btnStart.addActionListener(e -> startExecution());

        btnCancel = new JButton("⏹ Cancelar");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setBackground(new Color(239, 68, 68));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setEnabled(false);
        btnCancel.addActionListener(e -> cancelExecution());

        btnPanel.add(btnStart);
        btnPanel.add(btnCancel);

        lblStatus = new JLabel("Estado: Esperando selección de carpeta");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(new Color(71, 85, 105));

        panel.add(btnPanel, BorderLayout.WEST);
        panel.add(lblStatus, BorderLayout.EAST);
        return panel;
    }

    private JPanel createProgressAndLogSection() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        // Barra de progreso superior
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        progressBar.setPreferredSize(new Dimension(progressBar.getPreferredSize().width, 24));
        progressBar.setForeground(new Color(37, 99, 235));

        // Consola de logs
        txtLogs = new JTextArea();
        txtLogs.setEditable(false);
        txtLogs.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtLogs.setBackground(new Color(15, 23, 42));
        txtLogs.setForeground(new Color(226, 232, 240));
        txtLogs.setMargin(new Insets(6, 6, 6, 6));

        JScrollPane scrollLogs = new JScrollPane(txtLogs);
        scrollLogs.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        panel.add(progressBar, BorderLayout.NORTH);
        panel.add(scrollLogs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHistorySection() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(getWidth(), 150));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "3. Historial de Rendimiento y Comparativa (Speedup & Eficiencia)",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(51, 65, 85)
        ));

        String[] columns = {"Hora", "Modo", "Hilos", "Imágenes", "Filtro", "Tiempo (s)", "Img / Seg", "Speedup (S)", "Eficiencia (E)"};
        historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHistory = new JTable(historyModel);
        tableHistory.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tableHistory.setRowHeight(20);
        tableHistory.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));

        JScrollPane scrollTable = new JScrollPane(tableHistory);
        panel.add(scrollTable, BorderLayout.CENTER);
        return panel;
    }

    // --- Controladores de Eventos ---

    private void chooseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Selecciona la carpeta con imágenes");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadFolder(chooser.getSelectedFile());
        }
    }

    private void loadFolder(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;

        txtSourceFolder.setText(dir.getAbsolutePath());
        File[] files = dir.listFiles((d, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".bmp");
        });

        currentImages.clear();
        if (files != null) {
            currentImages.addAll(Arrays.asList(files));
        }

        lblImageCount.setText(String.format("Imágenes detectadas: %d archivos", currentImages.size()));
        boolean hasImages = !currentImages.isEmpty();
        btnStart.setEnabled(hasImages);

        if (hasImages) {
            lblStatus.setText("Listo para procesar " + currentImages.size() + " imágenes");
            appendLog(String.format("Carpeta cargada: %s (%d imágenes encontradas).", dir.getName(), currentImages.size()));
        } else {
            lblStatus.setText("No se encontraron imágenes en la carpeta seleccionada.");
            appendLog("Aviso: No se encontraron imágenes (.png, .jpg, .bmp) en la carpeta.");
        }
    }

    private void generateTestImagesPrompt() {
        String input = JOptionPane.showInputDialog(
                this,
                "¿Cuántas imágenes de prueba deseas generar?",
                "Generador Rápido de Pruebas",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null || input.trim().isEmpty()) return;

        try {
            int count = Integer.parseInt(input.trim());
            if (count <= 0 || count > 500) {
                JOptionPane.showMessageDialog(this, "Por favor ingresa un número entre 1 y 500.", "Número inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            File sampleDir = new File(System.getProperty("user.dir"), "muestras_prueba");
            TestImageGenerator.generateSampleImages(sampleDir, count, 640, 480);
            appendLog(String.format("Generadas con éxito %d imágenes en: %s", count, sampleDir.getAbsolutePath()));
            loadFolder(sampleDir);
            JOptionPane.showMessageDialog(this, "Se han generado " + count + " imágenes en:\n" + sampleDir.getAbsolutePath(), "Generación Exitosa", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Debes ingresar un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al generar imágenes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startExecution() {
        if (currentImages.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay imágenes para procesar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File sourceDir = new File(txtSourceFolder.getText());
        File destDir = new File(sourceDir, "imgProcesadas_Java");

        FilterType filter = (FilterType) cmbFilter.getSelectedItem();
        int threads = rbSequential.isSelected() ? 1 : (Integer) spinnerThreads.getValue();

        setUIProcessing(true);
        progressBar.setValue(0);
        progressBar.setString("0 / " + currentImages.size() + " (0%)");
        lblStatus.setText(String.format("Procesando con %d hilo(s)...", threads));

        appendLog(String.format("=== INICIO DE PROCESAMIENTO: Modo=%s | Hilos=%d | Filtro=%s | Total=%d ===",
                rbSequential.isSelected() ? "Secuencial" : "Paralelo", threads, filter.getDisplayName(), currentImages.size()));

        engine.startProcessing(currentImages, destDir, filter, threads, this);
    }

    private void cancelExecution() {
        engine.cancel();
        setUIProcessing(false);
        lblStatus.setText("Proceso cancelado por el usuario.");
        appendLog("=== PROCESO CANCELADO ===");
    }

    private void setUIProcessing(boolean processing) {
        btnStart.setEnabled(!processing);
        btnCancel.setEnabled(processing);
        btnSelectFolder.setEnabled(!processing);
        btnGenerateTestImages.setEnabled(!processing);
        cmbFilter.setEnabled(!processing);
        rbSequential.setEnabled(!processing);
        rbParallel.setEnabled(!processing);
        spinnerThreads.setEnabled(!processing && rbParallel.isSelected());
    }

    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            txtLogs.append(message + "\n");
            txtLogs.setCaretPosition(txtLogs.getDocument().getLength());
        });
    }

    // --- Métodos de la interfaz ProgressListener (Callbacks) ---

    @Override
    public void onProgress(int current, int total, String message) {
        SwingUtilities.invokeLater(() -> {
            int pct = (int) (((double) current / total) * 100);
            progressBar.setValue(pct);
            progressBar.setString(String.format("%d / %d (%d%%)", current, total, pct));
            appendLog(message);
        });
    }

    @Override
    public void onComplete(ProcessingStats stats) {
        SwingUtilities.invokeLater(() -> {
            setUIProcessing(false);
            lblStatus.setText(String.format("Completado en %.3f s (%.1f img/s)",
                    stats.getExecutionTimeSeconds(), stats.getImagesPerSecond()));

            appendLog(String.format("=== FINALIZADO: %d imágenes procesadas en %.3f seg (%.2f img/seg) ===",
                    stats.getTotalImages(), stats.getExecutionTimeSeconds(), stats.getImagesPerSecond()));

            // Registramos tiempo secuencial base si aplica
            if (stats.getThreadCount() == 1) {
                lastSequentialTimeMillis = stats.getExecutionTimeMillis();
            }

            // Calculamos Speedup y Eficiencia
            String speedupStr = "-";
            String efficiencyStr = "-";

            if (lastSequentialTimeMillis != null && lastSequentialTimeMillis > 0) {
                double speedup = stats.calculateSpeedup(lastSequentialTimeMillis);
                double efficiency = stats.calculateEfficiency(lastSequentialTimeMillis);
                speedupStr = String.format("%.2fx", speedup);
                efficiencyStr = String.format("%.1f%%", efficiency * 100);
            }

            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            historyModel.addRow(new Object[]{
                    timeStamp,
                    stats.getMode(),
                    stats.getThreadCount(),
                    stats.getTotalImages(),
                    stats.getFilterApplied().name(),
                    String.format("%.3f", stats.getExecutionTimeSeconds()),
                    String.format("%.1f", stats.getImagesPerSecond()),
                    speedupStr,
                    efficiencyStr
            });

            // Mensaje modal resumen
            String summaryMsg = String.format(
                    "Resultados del Procesamiento:\n\n" +
                    "• Modo: %s\n" +
                    "• Hilos de ejecución: %d\n" +
                    "• Imágenes procesadas: %d\n" +
                    "• Tiempo total: %.3f segundos\n" +
                    "• Rendimiento: %.2f imágenes/segundo\n" +
                    "• Factor de Aceleración (Speedup): %s\n" +
                    "• Eficiencia: %s",
                    stats.getMode(),
                    stats.getThreadCount(),
                    stats.getTotalImages(),
                    stats.getExecutionTimeSeconds(),
                    stats.getImagesPerSecond(),
                    speedupStr,
                    efficiencyStr
            );

            JOptionPane.showMessageDialog(this, summaryMsg, "Procesamiento Finalizado", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    @Override
    public void onError(String errorMessage, Throwable throwable) {
        SwingUtilities.invokeLater(() -> {
            setUIProcessing(false);
            lblStatus.setText("Error en el procesamiento.");
            appendLog("ERROR: " + errorMessage);
            JOptionPane.showMessageDialog(this, errorMessage, "Error de Ejecución", JOptionPane.ERROR_MESSAGE);
        });
    }
}
