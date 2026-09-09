# Práctica 2: Cliente-Servidor con Sockets (Proyecto NetBeans)

Proyecto Java con estructura estándar de **Apache NetBeans** para la comunicación Cliente-Servidor mediante Sockets TCP/IP estándar (`java.net.*`, `java.io.*`). No requiere librerías externas, Maven ni descargas adicionales.

---

## Estructura del Proyecto NetBeans

```text
Codigo/
├── build.xml                      <- Script Ant de compilación de NetBeans
├── manifest.mf                    <- Manifiesto del proyecto
├── nbproject/                     <- Configuración del proyecto de NetBeans
│   ├── project.xml                <- Definición y nombre del proyecto en NetBeans
│   └── project.properties         <- Propiedades del proyecto (JDK, Main Class, etc.)
├── src/                           <- Paquete de fuentes estándar de NetBeans
│   └── com/
│       └── mycompany/
│           └── p2sistemasdistribuidos/
│               ├── P2SistemasDistribuidos.java  <- Entrada principal (F6) con menú de selección
│               ├── Servidor.java                <- Servidor multihilo de sockets
│               └── Cliente.java                 <- Cliente interactivo de consola
├── run_servidor.bat               <- Ejecutable directo para el Servidor (Windows)
├── run_cliente.bat                <- Ejecutable directo para el Cliente (Windows)
└── README.md
```

---

## Cómo Abrirlo y Ejecutarlo en NetBeans

1. Abre **Apache NetBeans**.
2. Ve al menú superior: **File** -> **Open Project...** (o `Ctrl + Shift + O`).
3. Navega hasta la carpeta `Codigo` de la Práctica 2 y selecciónala (verás el ícono de taza de café de NetBeans con el nombre **`P2SistemasDistribuidos`**).
4. Haz clic en **Open Project**.

### Formas de Ejecución en NetBeans

#### Opción 1: Ejecución individual por archivos (Recomendada)
Para tener el servidor y el cliente corriendo al mismo tiempo en pestañas separadas de la consola de NetBeans:
1. En el panel izquierdo *Projects*, despliega `Source Packages` -> `com.mycompany.p2sistemasdistribuidos`.
2. Haz **clic derecho** sobre **`Servidor.java`** -> **Run File** (o `Shift + F6`).
3. Haz **clic derecho** sobre **`Cliente.java`** -> **Run File** (o `Shift + F6`).

#### Opción 2: Menú Principal (F6)
- Presiona **F6** o el botón verde **Run Project**.
- Te aparecerá un menú en la consola de salida de NetBeans para elegir si deseas iniciar el Servidor o el Cliente.

---

## Ejecución Rápida sin NetBeans (Windows)

Si no deseas abrir NetBeans:
1. Haz doble clic en **`run_servidor.bat`** (abrirá la ventana del servidor a la escucha).
2. Haz doble clic en **`run_cliente.bat`** (abrirá la ventana del cliente conectada al servidor).

---

## Uso y Comandos

- Una vez conectado el cliente, escribe cualquier mensaje y presiona `Enter`. El servidor recibirá el mensaje, lo mostrará en su consola y te responderá con una confirmación.
- Para desconectar el cliente limpiamente, escribe:
  ```text
  salir
  ```
- El servidor es multihilo: puedes conectar tantos clientes como desees simultáneamente.
