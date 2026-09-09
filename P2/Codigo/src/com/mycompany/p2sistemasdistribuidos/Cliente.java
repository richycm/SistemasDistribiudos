package com.mycompany.p2sistemasdistribuidos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Práctica 2: Cliente de Sockets en Java Estándar.
 * Sistemas Distribuidos | Ricardo Carmona Martínez
 */
public class Cliente {
    private static final String HOST_POR_DEFECTO = "127.0.0.1";
    private static final int PUERTO_POR_DEFECTO = 5000;

    public static void main(String[] args) {
        String host = HOST_POR_DEFECTO;
        int puerto = PUERTO_POR_DEFECTO;

        // Opciones de configuración por argumentos: Cliente [host] [puerto]
        if (args != null && args.length > 0) {
            host = args[0];
        }
        if (args != null && args.length > 1) {
            try {
                puerto = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Puerto inválido. Usando puerto por defecto: " + PUERTO_POR_DEFECTO);
            }
        }

        System.out.println("=================================================");
        System.out.println("          CLIENTE DE SOCKETS (NETBEANS)          ");
        System.out.println("=================================================");
        System.out.printf("Intentando conectar con %s:%d...%n", host, puerto);

        try (
            Socket socket = new Socket(host, puerto);
            BufferedReader lectorServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter escritorServidor = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("¡Conectado exitosamente al servidor!");
            System.out.println("-------------------------------------------------\n");

            // Hilo receptor: procesa e imprime en tiempo real los mensajes del servidor
            Thread hiloEscucha = new Thread(() -> {
                try {
                    String lineaServidor;
                    while ((lineaServidor = lectorServidor.readLine()) != null) {
                        System.out.println("[Servidor] > " + lineaServidor);
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.out.println("\n[Aviso] Conexión terminada por el servidor.");
                    }
                }
            });
            hiloEscucha.setDaemon(true);
            hiloEscucha.start();

            // Hilo principal: lee la consola y envía mensajes al servidor
            String mensajeUsuario;
            while ((mensajeUsuario = teclado.readLine()) != null) {
                escritorServidor.println(mensajeUsuario);

                if ("salir".equalsIgnoreCase(mensajeUsuario.trim())) {
                    System.out.println("Cerrando conexión con el servidor...");
                    try {
                        Thread.sleep(300); // Permite recibir el mensaje final de despedida
                    } catch (InterruptedException ignored) {}
                    break;
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Error: No se encuentra el host " + host);
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            System.err.println("Asegúrate de iniciar el Servidor antes de conectar el Cliente.");
        }

        System.out.println("Cliente finalizado.");
    }
}
