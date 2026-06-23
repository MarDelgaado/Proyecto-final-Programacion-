package puzzle8;

import puzzle8.view.VentanaPrincipal;
import javax.swing.*;

/*Clase principal del proyecto 8-Puzzle
  Programación III — ING. EN SISTEMAS COMPUTACIONALES
  Profesor: Eduardo Serna Pérez
  Punto de entrada de la aplicación
  Lanza la ventana principal en el hilo de despacho de Swing (EDT)
 */
public class Main {
    public static void main(String[] args) {
        //aplicar look and feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        //lanzar en el Event Dispatch Thread de Swing
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}