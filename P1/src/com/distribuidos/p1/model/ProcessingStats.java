package com.distribuidos.p1.model;

/**
 * Contenedor inmutable para las estadísticas y métricas del procesamiento.
 */
public class ProcessingStats {
    private final String mode;
    private final int threadCount;
    private final int totalImages;
    private final long executionTimeMillis;
    private final double imagesPerSecond;
    private final FilterType filterApplied;

    public ProcessingStats(String mode, int threadCount, int totalImages, long executionTimeMillis, FilterType filterApplied) {
        this.mode = mode;
        this.threadCount = threadCount;
        this.totalImages = totalImages;
        this.executionTimeMillis = executionTimeMillis;
        this.filterApplied = filterApplied;
        this.imagesPerSecond = executionTimeMillis > 0 
                ? (totalImages * 1000.0) / executionTimeMillis 
                : 0.0;
    }

    public String getMode() {
        return mode;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getTotalImages() {
        return totalImages;
    }

    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public double getExecutionTimeSeconds() {
        return executionTimeMillis / 1000.0;
    }

    public double getImagesPerSecond() {
        return imagesPerSecond;
    }

    public FilterType getFilterApplied() {
        return filterApplied;
    }

    /**
     * Calcula el factor de aceleración (Speedup) respecto a un tiempo secuencial base.
     * Speedup S = T_secuencial / T_paralelo
     */
    public double calculateSpeedup(long sequentialTimeMillis) {
        if (executionTimeMillis == 0) return 1.0;
        return (double) sequentialTimeMillis / (double) executionTimeMillis;
    }

    /**
     * Calcula la eficiencia del paralelismo.
     * Eficiencia E = Speedup / N_hilos
     */
    public double calculateEfficiency(long sequentialTimeMillis) {
        if (threadCount == 0) return 0.0;
        return calculateSpeedup(sequentialTimeMillis) / threadCount;
    }
}
