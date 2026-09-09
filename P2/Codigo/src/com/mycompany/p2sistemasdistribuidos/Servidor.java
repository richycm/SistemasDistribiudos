package com.mycompany.p2sistemasdistribuidos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Práctica 2: Servidor de Sockets Multihilo en Java Estándar.
 * Sistemas Distribuidos | Ricardo Carmona Martínez
 */
public class Servidor {
    private static final int PUERTO_POR_DEFECTO = 5000;
    private static final AtomicInteger contadorClientes = new AtomicInteger(0);
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        int puerto = PUERTO_POR_DEFECTO;
        if (args != null && args.length > 0) {
            try {
                puerto = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Puerto inválido. Usando puerto por defecto: " + PUERTO_POR_DEFECTO);
            }
        }

        System.out.println("=================================================");
        System.out.println("          SERVIDOR DE SOCKETS (NETBEANS)         ");
        System.out.println("=================================================");
        System.out.println("Iniciando servidor en el puerto: " + puerto);

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor a la escucha. Esperando clientes...\n");

            while (true) {
                // Espera la conexión de un nuevo cliente
                Socket clienteSocket = serverSocket.accept();
                int idCliente = contadorClientes.incrementAndGet();

                // Cada cliente se atiende en su propio hilo
                Thread hiloCliente = new Thread(new ManejadorCliente(clienteSocket, idCliente));
                hiloCliente.start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    /**
     * Hilo encargado de atender la comunicación con un cliente específico.
     */
    private static class ManejadorCliente implements Runnable {
        private final Socket socket;
        private final int idCliente;
        private final String direccionCliente;

        public ManejadorCliente(Socket socket, int idCliente) {
            this.socket = socket;
            this.idCliente = idCliente;
            this.direccionCliente = socket.getRemoteSocketAddress().toString();
        }

        @Override
        public void run() {
            String horaConexion = LocalTime.now().format(FORMATO_HORA);
            System.out.printf("[%s] [+] Cliente #%d conectado desde %s%n", horaConexion, idCliente, direccionCliente);

            try (
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
            ) {
                // Bienvenida inicial enviada al cliente
                salida.println("¡Bienvenido al servidor! Tu identificador es #" + idCliente);
                salida.println("Escribe tus mensajes. Escribe 'salir' para desconectarte.");

                String mensajeRecibido;
                while ((mensajeRecibido = entrada.readLine()) != null) {
                    String horaMensaje = LocalTime.now().format(FORMATO_HORA);
                    System.out.printf("[%s] [Cliente #%d]: %s%n", horaMensaje, idCliente, mensajeRecibido);

                    if ("salir".equalsIgnoreCase(mensajeRecibido.trim())) {
                        salida.println("¡Hasta luego! Conexión finalizada.");
                        break;
                    }

                    // Confirmación y respuesta al cliente
                    String respuesta = String.format("Servidor recibió: \"%s\" (a las %s)", mensajeRecibido, horaMensaje);
                    salida.println(respuesta);
                }

            } catch (IOException e) {
                System.out.printf("[-] Conexión interrumpida con el Cliente #%d: %s%n", idCliente, e.getMessage());
            } finally {
                cerrarSocket();
                String horaDesconexion = LocalTime.now().format(FORMATO_HORA);
                System.out.printf("[%s] [-] Cliente #%d desconectado.%n", horaDesconexion, idCliente);
            }
        }

        private void cerrarSocket() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error al cerrar socket del cliente #" + idCliente + ": " + e.getMessage());
            }
        }
    }
}
