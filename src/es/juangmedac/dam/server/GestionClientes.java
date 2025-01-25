package es.juangmedac.dam.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hilo que gestiona la carrera para un cliente/jinete concreto, usando int.
 */
public class GestionClientes extends Thread {

    private Servidor servidor;
    private Socket socket;
    private int idCamello;

    public GestionClientes(Servidor servidor, Socket socket, int idCamello) {
        this.servidor = servidor;
        this.socket = socket;
        this.idCamello = idCamello;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            // 1) Enviamos la lista de jinetes
            String nombres = servidor.getNombresJinetes();
            out.writeUTF(nombres);
            out.flush();

            // 2) Bucle de carrera: mientras no sea fin
            while (!servidor.isFinCarrera()) {
                // Calcular un avance aleatorio (int)
                int avance = calcularAvanceAleatorio();
                servidor.realizarAvance(idCamello, avance);

                // Obtenemos los avances (int[]) y enviamos 5 ints:
                //   Avance camello 0
                //   Avance camello 1
                //   Avance camello 2
                //   Avance camello 3
                //   Control (0 para "sigue la carrera")
                int[] todos = servidor.getAvances();
                out.writeInt(todos[0]);
                out.writeInt(todos[1]);
                out.writeInt(todos[2]);
                out.writeInt(todos[3]);
                out.writeInt(0);  // 0 => carrera en curso

                out.flush();

                Thread.sleep(1500);
            }

            // 3) Fin de la carrera: enviamos posiciones finales
            //   (de nuevo 4 ints + 1 int de control = -1)
            int[] posiciones = servidor.getPosicionesFinales(idCamello);
            out.writeInt(posiciones[0]);
            out.writeInt(posiciones[1]);
            out.writeInt(posiciones[2]);
            out.writeInt(posiciones[3]);
            out.writeInt(-1);  // -1 => fin de carrera

            out.flush();

            System.out.println("Jinete " + idCamello + " ha enviado posiciones finales.");

        } catch (Exception e) {
            Logger.getLogger(GestionClientes.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private int calcularAvanceAleatorio() {
        int n = (int) (Math.random() * 100);
        if (n <= 15) return 1;
        else if (n <= 30) return 2;
        else if (n <= 40) return 3;
        else if (n <= 48) return 4;
        else if (n <= 56) return 5;
        else if (n <= 63) return 6;
        else if (n <= 70) return 7;
        else if (n <= 80) return 8;
        else if (n <= 90) return 9;
        else return 10;
    }
}
