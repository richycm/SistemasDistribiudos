package com.distribuidos.p1.service;

import com.distribuidos.p1.model.FilterType;
import com.distribuidos.p1.model.ProcessingStats;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Motor de procesamiento concurrente y secuencial de imágenes.
 * Utiliza un ThreadPool gestionado por ExecutorService para distribuir la carga de trabajo
 * entre los hilos disponibles del procesador.
 */
public class BatchProcessingEngine {

    private final ImageTransformer transformer;
    private ExecutorService currentExecutor;
    private volatile boolean isRunning = false;

    public BatchProcessingEngine() {
        this.transformer = new ImageTransformer();
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Cancela la ejecución actual si está en curso.
     */
    public void cancel() {
        if (currentExecutor != null && !currentExecutor.isShutdown()) {
            currentExecutor.shutdownNow();
        }
        isRunning = false;
    }

    /**
     * Ejecuta el procesamiento de una lista de imágenes en segundo plano.
     *
     * @param imageFiles   Lista de archivos de imágenes a transformar.
     * @param outputDir    Directorio donde se guardarán las imágenes procesadas.
     * @param filterType   Filtro a aplicar.
     * @param threadCount  Cantidad de hilos a usar (1 para secuencial, >1 para multihilo).
     * @param listener     Listener para reportar progreso y estado.
     */
    public void startProcessing(
            List<File> imageFiles,
            File outputDir,
            FilterType filterType,
            int threadCount,
            ProgressListener listener
    ) {
        if (isRunning) {
            listener.onError("Ya hay un proceso en ejecución.", null);
            return;
        }

        if (imageFiles == null || imageFiles.isEmpty()) {
            listener.onError("No se encontraron imágenes para procesar.", null);
            return;
        }

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            listener.onError("No se pudo crear la carpeta de destino: " + outputDir.getAbsolutePath(), null);
            return;
        }

        isRunning = true;
        int total = imageFiles.size();
        String mode = (threadCount == 1) ? "Secuencial (1 Hilo)" : "Paralelo (" + threadCount + " Hilos)";

        // Hilo orquestador general para no congelar la UI de Swing
        Thread orchestrator = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            AtomicInteger processedCounter = new AtomicInteger(0);

            // Pool de hilos con nombres personalizados
            AtomicInteger threadIndex = new AtomicInteger(1);
            currentExecutor = Executors.newFixedThreadPool(threadCount, r -> {
                Thread t = new Thread(r, "WorkerThread-" + threadIndex.getAndIncrement());
                t.setDaemon(true);
                return t;
            });

            // Creamos las tareas para cada imagen
            for (File file : imageFiles) {
                currentExecutor.submit(() -> {
                    String threadName = Thread.currentThread().getName();
                    long fileStartTime = System.currentTimeMillis();
                    File destFile = new File(outputDir, "proc_" + file.getName());

                    try {
                        transformer.processImage(file, destFile, filterType);
                        long fileDuration = System.currentTimeMillis() - fileStartTime;
                        int count = processedCounter.incrementAndGet();

                        String msg = String.format("[%s] Procesó: %s (%d ms)", threadName, file.getName(), fileDuration);
                        listener.onProgress(count, total, msg);

                    } catch (Exception ex) {
                        int count = processedCounter.incrementAndGet();
                        String errMsg = String.format("[%s] ERROR en %s: %s", threadName, file.getName(), ex.getMessage());
                        listener.onProgress(count, total, errMsg);
                    }
                });
            }

            // Esperamos que todas las tareas del pool terminen
            currentExecutor.shutdown();
            try {
                if (!currentExecutor.awaitTermination(30, TimeUnit.MINUTES)) {
                    currentExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                currentExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            } finally {
                isRunning = false;
                long totalTime = System.currentTimeMillis() - startTime;
                ProcessingStats stats = new ProcessingStats(
                        mode,
                        threadCount,
                        total,
                        totalTime,
                        filterType
                );
                listener.onComplete(stats);
            }
        }, "Batch-Orchestrator");

        orchestrator.setDaemon(true);
        orchestrator.start();
    }
}
