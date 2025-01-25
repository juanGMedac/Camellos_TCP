package es.juangmedac.dam.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Servidor que gestiona la carrera de camellos vía TCP, usando enteros (int)
 * para avances y el control de fin de carrera.
 */
public class Servidor {

    private final int NUM_MAX_JINETES = 4;
    private ArrayList<Socket> socketsClientes;

    // Datos de la carrera (ahora en int)
    private int contadorPosicionFinal;
    private int[] posicionesFinales;  // posiciones finales de los jinetes (1..4)
    private int[] avances;           // avances de los jinetes (0..100)
    private int numJinetesListos;
    private int numJinetesAcabados;
    private boolean finCarrera;
    private String nombresJinetes;

    public void ejecutarServidor() {
        // Inicializamos valores
        contadorPosicionFinal = 1;
        numJinetesAcabados = 0;
        finCarrera = false;
        nombresJinetes = "";

        // Creamos arrays para 4 jinetes
        posicionesFinales = new int[NUM_MAX_JINETES];
        avances = new int[NUM_MAX_JINETES];
        numJinetesListos = 0;

        socketsClientes = new ArrayList<>();

        System.out.println("Comienza la ejecución del servidor (TCP con int). Escuchando en puerto 5555...");

        try (ServerSocket serverSocket = new ServerSocket(5555)) {

            // Esperamos a los 4 clientes
            while (socketsClientes.size() < NUM_MAX_JINETES) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("¡Nuevo cliente conectado! " + socketCliente.getInetAddress());

                // Leemos el nombre que envía el cliente
                DataInputStream in = new DataInputStream(socketCliente.getInputStream());
                DataOutputStream out = new DataOutputStream(socketCliente.getOutputStream());

                String nombreJinete = in.readUTF();  // leemos String con el nombre
                if (!nombresJinetes.isEmpty()) {
                    nombresJinetes += ",";
                }
                nombresJinetes += nombreJinete;

                // Enviamos acuse de recibo
                out.writeUTF("aceptado");

                // Guardamos el socket
                socketsClientes.add(socketCliente);
            }

            // Creamos hilos de gestión para los 4 clientes
            for (int i = 0; i < NUM_MAX_JINETES; i++) {
                Socket s = socketsClientes.get(i);
                GestionClientes hilo = new GestionClientes(this, s, i);
                hilo.start();
            }

            // Esperamos a que terminen la carrera
            while (numJinetesAcabados < NUM_MAX_JINETES) {
                synchronized (this) {
                    wait();
                }
                numJinetesListos = 0;
                synchronized (this) {
                    notifyAll();
                }
            }

            // Fin de la carrera
            finCarrera = true;

            // Sincronización final
            synchronized (this) {
                wait();
            }
            synchronized (this) {
                notifyAll();
            }

            System.out.println("Fin de la carrera. Servidor cerrando.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Métodos sincronizados para la carrera =====

    /**
     * Avanza el camello indicado (idCamello) en la cantidad "avance".
     */
    public synchronized void realizarAvance(int idCamello, int avance) {
        if (avances[idCamello] < 100) {
            avances[idCamello] += avance;
            if (avances[idCamello] >= 100) {
                avances[idCamello] = 100;
                numJinetesAcabados++;
            }
        }
        numJinetesListos++;

        if (numJinetesListos == NUM_MAX_JINETES) {
            notifyAll();
        } else {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Devuelve los avances de los 4 jinetes en un array de int.
     */
    public synchronized int[] getAvances() {
        return avances;
    }

    /**
     * Asigna posición final al jinete y devuelve el array con posiciones de todos.
     */
    public synchronized int[] getPosicionesFinales(int idCamello) {
        posicionesFinales[idCamello] = contadorPosicionFinal;
        contadorPosicionFinal++;

        if (contadorPosicionFinal == NUM_MAX_JINETES + 1) {
            notifyAll();
        } else {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return posicionesFinales;
    }

    public synchronized boolean isFinCarrera() {
        return finCarrera;
    }

    public synchronized String getNombresJinetes() {
        return nombresJinetes;
    }
}
