package puzzle8.view;

import puzzle8.model.Tablero;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialogo para que el usuario configure manualmente un tablero 3x3.
 * Hereda de JDialog.
 */
public class DialogoConfigurarTablero extends JDialog {

    private final JTextField[][] campos = new JTextField[3][3];
    private Tablero resultado;
    private final String titulo;

    /**
     * @param padre   
     * @param titulo  
     * @param defecto 
     */
    public DialogoConfigurarTablero(JFrame padre, String titulo, Tablero defecto) {
        super(padre, titulo, true);
        this.titulo = titulo;
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(15, 20, 40));

        // Instrucciones
        JLabel lbl = new JLabel("Ingresa los dígitos 0-8 sin repetir (0 = espacio vacío):",
                SwingConstants.CENTER);
        lbl.setForeground(new Color(180, 200, 255));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setBorder(new EmptyBorder(10, 10, 0, 10));
        add(lbl, BorderLayout.NORTH);

        // Cuadrícula de entrada
        JPanel grid = new JPanel(new GridLayout(3, 3, 6, 6));
        grid.setBackground(new Color(15, 20, 40));
        grid.setBorder(new EmptyBorder(10, 20, 10, 20));

        Font fCampo = new Font("SansSerif", Font.BOLD, 26);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                campos[i][j] = new JTextField(1);
                campos[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                campos[i][j].setFont(fCampo);
                campos[i][j].setBackground(new Color(30, 40, 80));
                campos[i][j].setForeground(Color.WHITE);
                campos[i][j].setCaretColor(Color.WHITE);
                campos[i][j].setPreferredSize(new Dimension(70, 70));
                if (defecto != null) {
                    int v = defecto.getValor(i, j);
                    campos[i][j].setText(v == 0 ? "0" : String.valueOf(v));
                }
                grid.add(campos[i][j]);
            }
        }
        add(grid, BorderLayout.CENTER);

        // Botones
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btns.setBackground(new Color(15, 20, 40));
        JButton ok     = boton("Aceptar",  new Color(40, 120, 80));
        JButton cancel = boton("Cancelar", new Color(100, 30, 30));

        ok.addActionListener(e -> {
            resultado = leerTablero();
            if (resultado != null) dispose();
        });
        cancel.addActionListener(e -> dispose());

        btns.add(ok);
        btns.add(cancel);
        add(btns, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }

    private JButton boton(String texto, Color bg) {
        JButton b = new JButton(texto);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(110, 36));
        return b;
    }

    private Tablero leerTablero() {
        int[][] mat = new int[3][3];
        boolean[] usados = new boolean[9];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String txt = campos[i][j].getText().trim();
                int v;
                try {
                    v = Integer.parseInt(txt);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Valor inválido en (" + (i+1) + "," + (j+1) + "): '" + txt + "'",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                if (v < 0 || v > 8) {
                    JOptionPane.showMessageDialog(this,
                            "Valor fuera de rango en (" + (i+1) + "," + (j+1) + "): " + v,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                if (usados[v]) {
                    JOptionPane.showMessageDialog(this,
                            "El valor " + v + " está repetido.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                usados[v] = true;
                mat[i][j] = v;
            }
        }
        Tablero t = new Tablero(mat);
        if (!t.esSolucionable()) {
            JOptionPane.showMessageDialog(this,
                    "Este tablero no es solucionable.\nVerifica la configuración.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return t;
    }

    /** @return el tablero configurado, o null si el usuario canceló */
    public Tablero getResultado() { return resultado; }
}
