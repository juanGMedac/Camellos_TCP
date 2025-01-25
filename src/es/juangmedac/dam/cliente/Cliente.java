package es.juangmedac.dam.cliente;

import es.juangmedac.dam.gui.ClienteVentanaCarrera;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Cliente que recibe avances y posiciones finales como int.
 */
public class Cliente extends Thread {

    private String nombre;
    private boolean fin;

    public Cliente(String nombre) {
        this.nombre = nombre;
        this.fin = false;
    }

    @Override
    public void run() {
        try {
            // Conexión al servidor
            Socket socket = new Socket(InetAddress.getLocalHost(), 5555);
            System.out.println("[" + nombre + "] Conectado al servidor en puerto local: " + socket.getLocalPort());

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // 1) Enviamos nuestro nombre
            out.writeUTF(nombre);
            out.flush();

            // 2) Recibimos "aceptado"
            String respuesta = in.readUTF();
            if ("aceptado".equals(respuesta)) {
                System.out.println("[" + nombre + "] El servidor me ha aceptado en la carrera.");
            }

            // 3) Recibimos la lista de nombres
            String listaJinetes = in.readUTF();
            System.out.println("[" + nombre + "] Lista de jinetes: " + listaJinetes);

            // Mostramos la ventana
            ClienteVentanaCarrera ventana = new ClienteVentanaCarrera(nombre);
            ventana.setNombresJinetes(listaJinetes);
            ventana.setVisible(true);

            // 4) Bucle de recepción (5 ints): 4 avances + 1 control
            while (!fin) {
                int a0 = in.readInt();
                int a1 = in.readInt();
                int a2 = in.readInt();
                int a3 = in.readInt();
                int control = in.readInt();  // 0 => carrera en curso, -1 => fin

                // Actualizamos ventana
                if (control != -1) {
                    // Carrera en curso
                    // Montamos un array de 4 int para mandar a la ventana
                    int[] avances = {a0, a1, a2, a3};
                    ventana.avance(avances);
                } else {
                    // Fin de carrera
                    // Montamos un array de 4 int para posiciones finales
                    int[] posiciones = {a0, a1, a2, a3};
                    ventana.setPosicionesFinales(posiciones);
                    fin = true;
                }
            }

            socket.close();
            System.out.println("[" + nombre + "] Cliente finalizado.");

        } catch (Exception e) {
            System.out.println("Error en cliente " + nombre + ": " + e);
        }
    }
}
