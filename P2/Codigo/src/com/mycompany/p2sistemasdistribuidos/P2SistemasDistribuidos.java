package com.mycompany.p2sistemasdistribuidos;

import java.util.Scanner;

/**
 * Clase principal de entrada del proyecto para NetBeans.
 * Permite ejecutar el Servidor o el Cliente directamente desde un menú interactivo al presionar F6,
 * o bien ejecutando cada clase individualmente (Clic derecho -> Run File / Shift+F6).
 *
 * Sistemas Distribuidos | Ricardo Carmona Martínez
 */
public class P2SistemasDistribuidos {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=================================================");
        System.out.println("      P2: SISTEMAS DISTRIBUIDOS - SOCKETS        ");
        System.out.println("=================================================");
        System.out.println("Selecciona el componente que deseas ejecutar:");
        System.out.println("  1. Iniciar Servidor");
        System.out.println("  2. Iniciar Cliente");
        System.out.println("  3. Salir");
        System.out.println("-------------------------------------------------");
        System.out.println("TIP: En NetBeans también puedes hacer clic derecho");
        System.out.println("     sobre Servidor.java o Cliente.java y elegir");
        System.out.println("     'Run File' (Shift + F6) para abrirlos en pestañas.");
        System.out.println("-------------------------------------------------");
        System.out.print("Ingresa tu opción (1-3): ");

        String opcion = scanner.nextLine().trim();

        switch (opcion) {
            case "1":
                System.out.println("\nLanzando Servidor...\n");
                Servidor.main(new String[]{});
                break;
            case "2":
                System.out.println("\nLanzando Cliente...\n");
                Cliente.main(new String[]{});
                break;
            case "3":
                System.out.println("Saliendo del programa.");
                break;
            default:
                System.out.println("Opción no válida. Ejecución terminada.");
                break;
        }
    }
}
