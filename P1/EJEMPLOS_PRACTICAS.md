# 🧪 Catálogo Completo de Ejemplos y Experimentos de Práctica
## Asignatura: Sistemas Distribuidos

Este documento contiene todos los **escenarios de prueba, experimentos de laboratorio, benchmarks de rendimiento, comparativas teóricas y propuestas de proyectos** que se pueden utilizar para la **Práctica 1 (Hilos y Concurrencia)** y futuras prácticas de la materia.

---

## 📑 Tabla de Contenidos
1. [Ejemplos y Experimentos para la Práctica 1 (Hilos y Concurrencia)](#1-ejemplos-y-experimentos-para-la-práctica-1-hilos-y-concurrencia)
   - [Experimento 1: Benchmark Base (Secuencial vs Multihilo)](#experimento-1-benchmark-base-secuencial-vs-multihilo)
   - [Experimento 2: Escalabilidad de Hilos y Ley de Amdahl](#experimento-2-escalabilidad-de-hilos-y-ley-de-amdahl)
   - [Experimento 3: Complejidad del Filtro (CPU-Bound vs I/O-Bound)](#experimento-3-complejidad-del-filtro-cpu-bound-vs-io-bound)
   - [Experimento 4: Estrés con Lotes Masivos (10 vs 50 vs 100 vs 200 imágenes)](#experimento-4-estrés-con-lotes-masivos)
   - [Experimento 5: Prueba de Interrupción y Parada Segura (Graceful Cancellation)](#experimento-5-prueba-de-interrupción-y-parada-segura)
   - [Experimento 6: Comparativa Cruzada Java (JVM Native Threads) vs Python (GIL)](#experimento-6-comparativa-cruzada-java-vs-python)
2. [Plantilla de Tablas para Reportes de Laboratorio](#2-plantilla-de-tablas-para-reportes-de-laboratorio)
3. [Comandos de Ejecución Rápida y Generación de Datos](#3-comandos-de-ejecución-rápida-y-generación-de-datos)
4. [Banco de Ideas y Ejemplos para Prácticas Futuras de Sistemas Distribuidos](#4-banco-de-ideas-y-ejemplos-para-prácticas-futuras)
   - [Sockets TCP / UDP Multihilo](#ejemplo-p2-sockets-tcp-udp-multihilo)
   - [Invocación de Métodos Remotos (Java RMI)](#ejemplo-p3-invocación-de-métodos-remotos-java-rmi)
   - [Algoritmos de Elección y Sincronización Distribuida](#ejemplo-p4-algoritmos-de-elección-y-sincronización)
   - [Arquitectura MapReduce / Master-Worker](#ejemplo-p5-procesamiento-distribuido-master-worker)

---

## 1. Ejemplos y Experimentos para la Práctica 1 (Hilos y Concurrencia)

### Experimento 1: Benchmark Base (Secuencial vs Multihilo)
**Objetivo:** Demostrar la ganancia en tiempo de ejecución al pasar de 1 hilo a $N$ hilos (núcleos lógicos del procesador).

* **Configuración:**
  - Lote: **50 imágenes** generadas automáticamente con `✨ Generar 25 Imágenes de Prueba` (generar 2 veces o ingresar 50).
  - Filtro: **Escala de Grises (Luminancia)**.
* **Procedimiento:**
  1. Ejecutar en modo **Secuencial (1 Hilo)** y registrar el tiempo $T_{\text{secuencial}}$.
  2. Ejecutar en modo **Paralelo (Multihilo)** con el número recomendado de núcleos (ej. 8 o 16 hilos) y registrar $T_{\text{paralelo}}$.
  3. Comprobar el **Speedup** calculado:
     $$S = \frac{T_{\text{secuencial}}}{T_{\text{paralelo}}}$$
  4. Comprobar la **Eficiencia**:
     $$E = \frac{S}{N}$$
* **Resultado Esperado:** Un Speedup significativo (e.g., $3.5\times$ a $7.0\times$ en procesadores modernos de 8 a 16 núcleos).

---

### Experimento 2: Escalabilidad de Hilos y Ley de Amdahl
**Objetivo:** Observar cómo varía el rendimiento conforme aumentamos el número de hilos desde 1 hasta sobrepasar los núcleos físicos/lógicos, identificando el punto de saturación y la sobrecarga por cambio de contexto (*Context Switching*).

* **Configuración:**
  - Lote: **50 imágenes**.
  - Filtro: **Detección de Bordes (Sobel)**.
* **Pasos de Prueba:**
  | Prueba | Número de Hilos | Propósito |
  | :--- | :--- | :--- |
  | **P2.1** | 1 Hilo | Línea base secuencial |
  | **P2.2** | 2 Hilos | Paralelismo dual básico |
  | **P2.3** | 4 Hilos | Aprovechamiento de núcleos físicos |
  | **P2.4** | 8 Hilos | Aprovechamiento de multithreading / Hyper-Threading |
  | **P2.5** | 16 Hilos | Saturación máxima del procesador |
  | **P2.6** | 32 Hilos | Sobreasignación (*Over-threading*): observar degradación por overhead de contexto |
  | **P2.7** | 64 Hilos | Estrés de planificación de la JVM y SO |

* **Preguntas para el reporte:**
  - ¿A partir de cuántos hilos el Speedup deja de crecer linealmente?
  - ¿Por qué usar 64 hilos en un CPU de 8 núcleos no duplica la velocidad respecto a 8 hilos? (Explicar contención de I/O de disco y sobrecarga de planificación).

---

### Experimento 3: Complejidad del Filtro (CPU-Bound vs I/O-Bound)
**Objetivo:** Comprobar que los algoritmos con mayor demanda matemática (CPU-Bound) obtienen mayor aceleración con hilos que las tareas dominadas por lectura y escritura de disco (I/O-Bound).

* **Filtros a Comparar:**
  1. **Escala de Grises (`GRAYSCALE`):** Carga ligera (cálculo simple de luminosidad $0.299R + 0.587G + 0.114B$).
  2. **Inversión de Colores (`INVERT`):** Carga ligera ($255 - R, 255 - G, 255 - B$).
  3. **Efecto Sepia (`SEPIA`):** Carga media (transformación matricial de 3 canales con `clamp`).
  4. **Detección de Bordes Sobel (`EDGE_DETECTION`):** Carga pesada (convolución de matriz $3\times3$ con gradientes horizontales y verticales $G_x, G_y$, cálculo de raíz cuadrada de magnitudes para cada píxel).

* **Tabla de Comparativa de Filtros (con 50 imágenes y CPU Cores):**
  | Filtro | Tiempo Secuencial (s) | Tiempo Multihilo (s) | Speedup ($S$) | Eficiencia ($E$) |
  | :--- | :--- | :--- | :--- | :--- |
  | Grayscale | | | | |
  | Invert | | | | |
  | Sepia | | | | |
  | Sobel | | | | |

* **Conclusión Teórica:** El filtro **Sobel** mostrará el mayor Speedup relativo porque el tiempo dedicado a cómputo en CPU opaca el tiempo de I/O de disco.

---

### Experimento 4: Estrés con Lotes Masivos
**Objetivo:** Evaluar la estabilidad de la memoria de la máquina virtual de Java (JVM Heap) y el Throughput (Imágenes procesadas por segundo) bajo diferentes tamaños de carga.

* **Escenarios:**
  - **Lote Pequeño:** 10 imágenes (evalúa la sobrecarga inicial de arranque de hilos).
  - **Lote Mediano:** 50 imágenes (flujo estándar de trabajo).
  - **Lote Grande:** 100 imágenes (estabilidad sostenida).
  - **Lote Masivo:** 250 - 500 imágenes (prueba de resistencia del recolector de basura de Java y límites de I/O).
* **Métrica a analizar:**
  - Throughput: $\text{Imágenes por Segundo} = \frac{\text{Total Imágenes}}{\text{Tiempo en Segundos}}$.

---

### Experimento 5: Prueba de Interrupción y Parada Segura
**Objetivo:** Demostrar cómo se implementa la cancelación asíncrona de hilos sin causar bloqueos mutuos (*Deadlocks*) ni dejar recursos huérfanos.

* **Procedimiento:**
  1. Generar un lote de **100 imágenes**.
  2. Iniciar el procesamiento con el filtro **Sobel**.
  3. A mitad del progreso (aproximadamente al 40%), hacer clic en el botón rojo **`⏹ Cancelar`**.
  4. Observar en la consola de logs cómo el motor interrumpe los hilos activos mediante `ExecutorService.shutdownNow()`, bloquea la admisión de nuevas tareas y restaura la UI al estado listo.

---

### Experimento 6: Comparativa Cruzada Java vs Python
**Objetivo:** Justificar ante el profesor por qué Java ofrece un rendimiento superior en procesamiento concurrente intensivo de CPU frente a Python con `threading`.

* **Tabla Teórico-Práctica:**
  | Aspecto | Python (`threading`) | Java (`ExecutorService`) |
  | :--- | :--- | :--- |
  | **Intérprete / Runtime** | CPython con **GIL (Global Interpreter Lock)** | JVM con **Hilos Nativos del SO** |
  | **Paralelismo real en CPU** | ❌ Limitado a 1 solo núcleo para código Python puro | ✔️ Verdadero paralelismo multi-núcleo |
  | **Estructuras de Sincronización** | `threading.Lock` | `AtomicInteger`, `ConcurrentLinkedQueue`, `CountDownLatch` |
  | **Gestión de Memoria** | Reference Counting + GC cíclico | Generational Garbage Collection optimizado para alto Throughput |
  | **Rendimiento relativo (Sobel)** | Línea base ($1.0\times$) | Hasta $5\times - 12\times$ más rápido |

---

## 2. Plantilla de Tablas para Reportes de Laboratorio

Puedes copiar directamente estas tablas en tu reporte de Word, LaTeX o Markdown:

### Tabla 1: Características del Entorno de Pruebas
| Componente | Especificación |
| :--- | :--- |
| **Procesador (CPU)** | *Ej. AMD Ryzen 7 5700X / Intel Core i7-12700H* |
| **Núcleos Físicos / Lógicos** | *Ej. 8 núcleos / 16 hilos* |
| **Memoria RAM** | *Ej. 16 GB DDR4 / DDR5* |
| **Almacenamiento** | *Ej. SSD NVMe M.2* |
| **Sistema Operativo** | Windows 11 Pro 64-bit |
| **Versión de Java (JDK)** | *Ej. OpenJDK 21 / Oracle JDK 17* |

### Tabla 2: Resultados Experimentales de Benchmarking
| ID Prueba | Filtro Aplicado | Cantidad de Imágenes | Hilos Configurados | Tiempo Total (s) | Throughput (img/s) | Speedup ($S$) | Eficiencia ($E$) |
| :---: | :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| 1 | Escala de Grises | 50 | 1 (Secuencial) | | | $1.00\times$ | $100.0\%$ |
| 2 | Escala de Grises | 50 | 2 | | | | |
| 3 | Escala de Grises | 50 | 4 | | | | |
| 4 | Escala de Grises | 50 | 8 | | | | |
| 5 | Escala de Grises | 50 | 16 (Todos los núcleos) | | | | |
| 6 | Sobel (Bordes) | 50 | 1 (Secuencial) | | | $1.00\times$ | $100.0\%$ |
| 7 | Sobel (Bordes) | 50 | 16 (Todos los núcleos) | | | | |
| 8 | Efecto Sepia | 50 | 16 (Todos los núcleos) | | | | |

---

## 3. Comandos de Ejecución Rápida y Generación de Datos

### Ejecutar la Aplicación Principal
```bash
# Desde la carpeta raíz del proyecto
cd P1
run.bat
```

O manualmente:
```bash
javac -d bin src/com/distribuidos/p1/model/*.java src/com/distribuidos/p1/service/*.java src/com/distribuidos/p1/util/*.java src/com/distribuidos/p1/gui/*.java src/com/distribuidos/p1/Main.java
java -cp bin com.distribuidos.p1.Main
```

### Generar Lotes Personalizados de Imágenes desde Terminal
Si necesitas generar rápidamente 100 imágenes sintéticas sin abrir la interfaz:
```bash
# Sintaxis: java -cp bin com.distribuidos.p1.util.TestImageGenerator <CANTIDAD>
java -cp bin com.distribuidos.p1.util.TestImageGenerator 100
```
Las imágenes se guardarán automáticamente en la carpeta `P1/muestras_prueba/`.

---

## 4. Banco de Ideas y Ejemplos para Prácticas Futuras

A continuación se presentan propuestas arquitectónicas y ejemplos de código conceptual para las siguientes prácticas de la materia:

### Ejemplo P2: Sockets TCP / UDP Multihilo
* **Tema:** Comunicación cliente-servidor orientada a conexión vs no orientada a conexión.
* **Escenario:** Servidor de subasta distribuida o sala de chat en tiempo real con pool de hilos (`ExecutorService`) donde cada cliente conectado es atendido por un hilo de trabajo independiente.
* **Componentes:**
  - `ServerWorker`: Lee mensajes entrantes y los retransmite (*broadcast*).
  - `ClientHandler`: Controla la autenticación y protocolo de comandos (ej. `/join`, `/bid <monto>`, `/exit`).

### Ejemplo P3: Invocación de Métodos Remotos (Java RMI)
* **Tema:** Transparencia de acceso y ubicación en llamadas a procedimientos remotos.
* **Escenario:** Sistema Bancario Distribuido o Calculadora de Matrices Concurrente.
* **Componentes:**
  - `RemoteAccountService extends Remote`: Interfaz remota con métodos `transfer(from, to, amount)`, `getBalance(id)`.
  - `RmiRegistry`: Registro central en el puerto 1099 para localizar stubs remotos.

### Ejemplo P4: Algoritmos de Elección y Sincronización
* **Tema:** Tolerancia a fallos y coordinación de nodos.
* **Escenario:** Simulación del **Algoritmo de Elección Bully (El Matón)** o **Algoritmo de Anillo (Ring Algorithm)** para elegir un nodo líder cuando el coordinador principal se desconecta.
* **Componentes:**
  - Nodos con identificadores enteros ($ID_1, ID_2, \dots, ID_N$).
  - Detección de latidos (*Heartbeat*) y mensaje de `ELECTION` / `COORDINATOR`.

### Ejemplo P5: Procesamiento Distribuido Master-Worker
* **Tema:** Computación paralela distribuida en clúster (inspirada en MapReduce / Spark).
* **Escenario:** Procesamiento distribuido de imágenes o conteo de palabras sobre red local:
  - **Master Node:** Divide el lote de 500 imágenes en paquetes de 50 y los reparte por TCP/HTTP a 3 computadoras de compañeros (Workers).
  - **Worker Nodes:** Procesan el lote asignado y devuelven las imágenes transformadas al Master para consolidar las estadísticas finales.

---

> [!TIP]
> **Recomendación para tu defensa de práctica:**
> Enfatiza siempre la diferencia entre **Concurrencia** (estructurar tareas para avanzar simultáneamente) y **Paralelismo** (ejecutar múltiples tareas al mismo milisegundo en núcleos físicos distintos), respaldando tu explicación con las fórmulas de **Speedup** y **Eficiencia**.
