# Práctica 1: Simulador del Barbero Dormilón (Productor - Consumidor)

**Asignatura:** Sistemas Distribuidos  
**Alumno:** Ricardo Carmona Martínez  
**Tema:** Concurrencia, Exclusión Mutua, Sección Crítica y Buffer Acotado

---

## 1. Descripción del Proyecto

Esta aplicación implementa una interfaz gráfica moderna interactiva en **Java Swing** para modelar el problema clásico de sincronización del **Barbero Dormilón** (*Sleeping Barber Problem*):

- **Consumidor (Barbero):** 
  - Si la sala de espera está vacía, entra en estado de **espera pasiva** con `wait()`, liberando el monitor sin consumir ciclos de CPU.
  - Al recibir una notificación `notifyAll()`, atiende al siguiente cliente en turno y simula el corte de cabello.
- **Productores (Clientes):**
  - Cada cliente nuevo arriba en su propio hilo (`Thread`).
  - Si hay sillas disponibles en el **Buffer Acotado** (Sala de espera de 5 sillas), toma asiento y notifica al barbero dormido.
  - Si todas las sillas están ocupadas (**Buffer Overflow**), el cliente se retira inmediatamente sin ser atendido.
- **Monitor de Sincronización:**
  - Clase `Barberia` con métodos `synchronized`, garantizando exclusión mutua en las secciones críticas (`llegar` y `atenderSiguienteCliente`).

---

## 2. Instrucciones de Ejecución

### Opción A: Ejecución con doble clic en Windows (Recomendada)
Haz doble clic sobre el archivo:
```text
run.bat
```

### Opción B: Abrir en un IDE (NetBeans, IntelliJ, Eclipse, VS Code)
Abrir la carpeta del proyecto o directamente el archivo:
```text
src/com/mycompany/p1sistemasdistribuidos/P1SistemasDistribuidos.java
```
Y presionar **Run** / **Ejecutar**.

### Opción C: Compilación y ejecución desde consola
```bash
javac -encoding UTF-8 P1SistemasDistribuidos.java
java P1SistemasDistribuidos
```

---

## 3. Funcionalidades del Simulador

1. **Botón "+ Arribar 1 Cliente":** Genera un cliente de manera manual.
2. **Botón "Iniciar Tráfico Automático":** Dispara un generador concurrente en segundo plano con intervalos aleatorios para observar el comportamiento ante sobrecarga de buffer.
3. **Consola en Tiempo Real:** Muestra timestamps detallados del ciclo de vida de los hilos (`WAIT`, `PRODUCTOR`, `BARBERO`, `OVERFLOW`).
4. **Métricas en Vivo:** Contador de clientes atendidos, rechazados por buffer lleno y estado visual de cada silla.
