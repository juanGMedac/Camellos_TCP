package es.juangmedac.dam.gui;

import javax.swing.*;
import java.awt.*;

public class ClienteVentanaPodio extends JFrame {

    public ClienteVentanaPodio(int[] posiciones, String[] nombres) {
        super("Podio - TCP (int)");

        // Si cierras el podio, no cierras todo el programa
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Redimensionable
        setResizable(true);

        // Layout con 4 filas, 1 columna
        setLayout(new GridLayout(4, 1, 5, 5));

        // Recibimos algo como [2, 4, 1, 3], lo que indica la posición final
        // de cada jinete i => 2, 4, 1, 3 (1..4 => 1er lugar, 2º lugar, etc.)
        // "nombres" es un array como ["Cliente 1", "Cliente 2", "Cliente 3", "Cliente 4"]

        String[] podioStrings = new String[4];
        for (int i = 0; i < 4; i++) {
            // lugar = 1..4 -> convertimos a índice 0..3
            int lugar = posiciones[i] - 1;
            // Guardamos en la posición "lugar" la cadena con el jinete i
            podioStrings[lugar] = (lugar + 1) + "º: " + nombres[i];
        }

        // Añadimos 4 etiquetas
        for (int i = 0; i < 4; i++) {
            JLabel label = new JLabel(podioStrings[i], SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            add(label);
        }

        // Tamaño inicial
        setSize(400, 300);

        // Centrar en pantalla (o lo harás relativeTo la ventana de carrera)
        setLocationRelativeTo(null);
    }

    // (Opcional) main para testear solo esta ventana
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Ejemplo: supongamos que las posiciones son [2,1,4,3]
            // y los nombres [Cliente 1, Cliente 2, Cliente 3, Cliente 4]
            int[] pos = {2, 1, 4, 3};
            String[] noms = {"Cliente 1", "Cliente 2", "Cliente 3", "Cliente 4"};
            new ClienteVentanaPodio(pos, noms).setVisible(true);
        });
    }
}
