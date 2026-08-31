# Práctica 1: Procesamiento Concurrente y Multihilo de Imágenes en Java

**Asignatura:** Sistemas Distribuidos  
**Profesor:** Chadwick Carreto Arellano  
**Autor:** Tu Nombre / Proyecto Personal  

---

## 1. Análisis de la Práctica 1 Original (Python vs Java)

La práctica original en Python implementa una aplicación de escritorio con **Tkinter** que toma un lote de imágenes y las convierte a escala de grises secuencialmente (1 hilo) o en paralelo (usando tantos hilos como núcleos de CPU detectados con `multiprocessing.cpu_count()`).

### Comparativa Técnica Clave: Python vs Java

| Característica | Python (`threading` original) | Java (`ExecutorService` / Nuestra implementación) |
| :--- | :--- | :--- |
| **Modelo de Hilos** | Afectado por el **GIL** (Global Interpreter Lock), lo cual limita el paralelismo real en operaciones intensivas en CPU (CPU-Bound). | **Hilos nativos del Sistema Operativo gestionados por la JVM** sin GIL; verdadero paralelismo multi-núcleo en CPU. |
| **Gestión de Concurrencia** | Creación manual de listas de hilos (`threading.Thread`) y monitoreo manual (`join()`). | **Pool de hilos administrado (`ExecutorService`)** con balanceo de carga automático y recolección de tareas. |
| **Sincronización** | `threading.Lock` para actualizar variables de progreso. | Variables atómicas de alto rendimiento (`AtomicInteger`) y desacoplamiento mediante **Callbacks** (`ProgressListener`). |
| **Filtros Soportados** | Solo Escala de Grises. | **Escala de Grises**, **Efecto Sepia**, **Inversión de Colores (Negativo)** y **Detección de Bordes (Sobel)**. |
| **Generador de Pruebas** | Requiere descargar o buscar imágenes manualmente. | **Generador integrado de imágenes de prueba sintéticas** (permite crear 25, 50, 100 imágenes con figuras geométricas en 1 segundo). |
| **Métricas de Rendimiento** | Solo tiempo total en segundos. | **Tiempo total (s/ms), Throughput (img/seg), Factor de Aceleración ($Speedup$) y Eficiencia ($Efficiency$)**. |

---

## 2. Arquitectura del Proyecto (`P1/src`)

El código fue diseñado siguiendo buenas prácticas de programación orientada a objetos (POO), desacoplando la lógica de negocio, concurrencia y la interfaz gráfica:

```
P1/
├── bin/                          # Clases compiladas (.class)
├── src/
│   └── com/distribuidos/p1/
│       ├── Main.java             # Clase principal (punto de entrada)
│       ├── model/
│       │   ├── FilterType.java      # Enum con los filtros disponibles
│       │   └── ProcessingStats.java # Modelo inmutable de estadísticas y métricas (Speedup/Eficiencia)
│       ├── service/
│       │   ├── ImageTransformer.java    # Algoritmos de procesamiento digital a nivel de píxel
│       │   ├── BatchProcessingEngine.java # Motor de orquestación concurrente con ThreadPool
│       │   └── ProgressListener.java     # Interfaz Observer/Callback para eventos en tiempo real
│       ├── util/
│       │   └── TestImageGenerator.java   # Generador automático de imágenes de prueba sintéticas
│       └── gui/
│           └── MainWindow.java           # Interfaz gráfica moderna en Java Swing
├── run.bat                       # Script para compilar y ejecutar con doble clic en Windows
└── README.md                     # Documentación y reporte técnico
```

---

## 3. Fundamentos Teóricos Implementados

### 3.1. Factor de Aceleración (Speedup $S$)
El **Speedup** mide cuánto más rápido se ejecuta el programa con $N$ hilos en comparación con 1 solo hilo (secuencial):

$$S = \frac{T_{\text{secuencial}}}{T_{\text{paralelo}}}$$

- Si $S > 1$, el paralelismo está aportando una ganancia de rendimiento real.
- En un sistema ideal con $N$ núcleos, el speedup teórico máximo es $S = N$ (aceleración lineal).

### 3.2. Eficiencia del Paralelismo ($E$)
Mide qué tan bien se están aprovechando los $N$ hilos/núcleos asignados:

$$E = \frac{S}{N} = \frac{T_{\text{secuencial}}}{N \times T_{\text{paralelo}}}$$

- **100% (1.0):** Aprovechamiento perfecto de los recursos.
- En la práctica suele estar entre el 60% y el 90% debido a la sobrecarga de I/O de disco (lectura/escritura de archivos) y cambio de contexto de hilos.

---

## 4. Instrucciones de Compilación y Ejecución

### Opción A: Usando el script incluido (Windows)
Haz doble clic sobre `run.bat` o ejecútalo desde la terminal:
```cmd
.\run.bat
```

### Opción B: Manualmente desde la terminal
1. Ubícate en la carpeta `P1`:
   ```bash
   cd P1
   ```
2. Compila el código fuente:
   ```bash
   javac -d bin src/com/distribuidos/p1/model/*.java src/com/distribuidos/p1/service/*.java src/com/distribuidos/p1/util/*.java src/com/distribuidos/p1/gui/*.java src/com/distribuidos/p1/Main.java
   ```
3. Ejecuta la aplicación:
   ```bash
   java -cp bin com.distribuidos.p1.Main
   ```

---

## 5. Guía de Uso Rápido para Pruebas y Capturas

1. **Generar datos de prueba**: Haz clic en el botón `✨ Generar 25 Imágenes de Prueba` (o el número que desees, e.g. 50 imágenes).
2. **Prueba Secuencial**:
   - Selecciona **Secuencial (1 Hilo)**.
   - Elige el filtro deseado (por ejemplo, *Escala de Grises* o *Sobel*).
   - Haz clic en `▶ Iniciar Procesamiento`.
   - Espera a que termine para registrar el tiempo base.
3. **Prueba Paralela (Multihilo)**:
   - Selecciona **Paralelo (Multihilo)**.
   - Ajusta el número de hilos (por defecto usa los núcleos lógicos de tu CPU, ej. 8, 12 o 16).
   - Haz clic en `▶ Iniciar Procesamiento`.
4. **Analizar la Tabla de Rendimiento**:
   - En la parte inferior verás la tabla comparativa donde se calcula automáticamente el **Speedup** y la **Eficiencia** frente a la prueba secuencial.
