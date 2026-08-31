package com.distribuidos.p1.service;

import com.distribuidos.p1.model.ProcessingStats;

/**
 * Interfaz de comunicación (Callback) entre el motor de procesamiento y la interfaz gráfica / observadores.
 */
public interface ProgressListener {
    /**
     * Invocado cuando se procesa una imagen individual.
     * @param current Número de imágenes completadas hasta el momento.
     * @param total Total de imágenes a procesar.
     * @param message Detalle del evento o log generado por el hilo.
     */
    void onProgress(int current, int total, String message);

    /**
     * Invocado cuando todo el lote ha terminado de procesarse.
     * @param stats Resumen estadístico de la ejecución.
     */
    void onComplete(ProcessingStats stats);

    /**
     * Invocado en caso de error durante la ejecución.
     */
    void onError(String errorMessage, Throwable throwable);
}
