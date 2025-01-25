package es.juangmedac.dam.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ClienteVentanaCarrera extends JFrame {

    private String[] nombresJinetes; // Array para los nombres de los camellos
    private int[] posicionesFinales; // Posiciones finales de los camellos

    private JProgressBar[] barras;  // Barras de progreso para cada camello
    private JLabel[] etiquetasNombres; // Etiquetas para los nombres de los camellos
    private JLabel etiquetaJugador;   // Etiqueta para el nombre del jugador
    private JButton botonPodio;       // Botón para ver el podio

    public ClienteVentanaCarrera(String nombreJugador) {
        super("Carrera de Camellos - Jugador: " + nombreJugador); // Título con el nombre del jugador

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null); // Centrar la ventana
        initComponents(nombreJugador);
    }

    private void initComponents(String nombreJugador) {
        // Layout principal
        setLayout(new BorderLayout());

        // Etiqueta superior con el nombre del jugador
        etiquetaJugador = new JLabel("Jugador: " + nombreJugador, SwingConstants.CENTER);
        etiquetaJugador.setFont(new Font("Arial", Font.BOLD, 16));
        add(etiquetaJugador, BorderLayout.NORTH);

        // Panel central con barras de progreso y nombres de los camellos
        JPanel panelBarras = new JPanel(new GridLayout(4, 2, 5, 5)); // 4 filas, 2 columnas
        barras = new JProgressBar[4];
        etiquetasNombres = new JLabel[4];

        for (int i = 0; i < 4; i++) {
            etiquetasNombres[i] = new JLabel("Camello " + (i + 1), SwingConstants.CENTER);
            barras[i] = new JProgressBar(0, 100);
            barras[i].setStringPainted(true); // Mostrar porcentaje en las barras

            // Añadimos etiqueta y barra al panel
            panelBarras.add(etiquetasNombres[i]);
            panelBarras.add(barras[i]);
        }

        add(panelBarras, BorderLayout.CENTER);

        // Botón para ver el podio
        botonPodio = new JButton("Ver Podio");
        botonPodio.addActionListener((ActionEvent e) -> {
            if (posicionesFinales != null && nombresJinetes != null) {
                ClienteVentanaPodio podio = new ClienteVentanaPodio(posicionesFinales, nombresJinetes);
                podio.setLocationRelativeTo(this);
                podio.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Aún no hay posiciones finales disponibles.",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        add(botonPodio, BorderLayout.SOUTH);

        // Tamaño inicial de la ventana
        setSize(600, 400);
    }

    /**
     * Establece los nombres de los camellos en las etiquetas.
     * @param nombres Cadena de nombres separada por comas.
     */
    public void setNombresJinetes(String nombres) {
        this.nombresJinetes = nombres.split(",");
        for (int i = 0; i < nombresJinetes.length && i < etiquetasNombres.length; i++) {
            etiquetasNombres[i].setText(nombresJinetes[i]);
        }
    }

    /**
     * Actualiza los avances (4 int) en las barras de progreso.
     */
    public void avance(int[] avances) {
        for (int i = 0; i < 4; i++) {
            int val = Math.min(avances[i], 100); // Limitamos el valor a 100
            barras[i].setValue(val);
        }
    }

    /**
     * Establece las posiciones finales de los camellos.
     */
    public void setPosicionesFinales(int[] posiciones) {
        this.posicionesFinales = posiciones;
    }

    /**
     * Método main para probar la ventana por sí sola.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteVentanaCarrera ventana = new ClienteVentanaCarrera("Jugador 1");
            ventana.setNombresJinetes("Camello 1,Camello 2,Camello 3,Camello 4");
            ventana.setVisible(true);
        });
    }
}
