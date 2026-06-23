package puzzle8.view;

import puzzle8.model.Puntuacion;
import puzzle8.util.ManejadorArchivos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Diálogo que muestra el ranking de puntuaciones ordenado de mayor a menor.
 * Hereda de JDialog.
 */
public class DialogoPuntuaciones extends JDialog {

    public DialogoPuntuaciones(JFrame padre) {
        super(padre, "🏆 Ranking de Jugadores", true);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(15, 20, 40));
        setPreferredSize(new Dimension(520, 400));

        // Título
        JLabel titulo = new JLabel("TABLA DE PUNTUACIONES", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(new Color(255, 200, 50));
        titulo.setBorder(new EmptyBorder(15, 10, 5, 10));
        add(titulo, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"#", "Jugador", "Puntos", "Fecha"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Puntuacion> lista = ManejadorArchivos.cargarOrdenadas();
        if (lista.isEmpty()) {
            model.addRow(new Object[]{"—", "Sin registros", "—", "—"});
        } else {
            for (int i = 0; i < lista.size(); i++) {
                Puntuacion p = lista.get(i);
                String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : String.valueOf(i + 1);
                model.addRow(new Object[]{medal, p.getAlias(), p.getPuntos(), p.getFecha()});
            }
        }

        JTable tabla = new JTable(model);
        tabla.setBackground(new Color(20, 30, 60));
        tabla.setForeground(new Color(220, 235, 255));
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabla.setRowHeight(30);
        tabla.setGridColor(new Color(40, 60, 100));
        tabla.setShowGrid(true);
        tabla.getTableHeader().setBackground(new Color(30, 50, 100));
        tabla.getTableHeader().setForeground(new Color(150, 180, 255));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabla.setSelectionBackground(new Color(50, 80, 160));

        // Centrar columnas
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c = 0; c < cols.length; c++) tabla.getColumnModel().getColumn(c).setCellRenderer(center);
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);
        tabla.getColumnModel().getColumn(2).setMaxWidth(80);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new EmptyBorder(0, 15, 0, 15));
        scroll.getViewport().setBackground(new Color(20, 30, 60));
        add(scroll, BorderLayout.CENTER);

        // Botón cerrar
        JButton cerrar = new JButton("Cerrar");
        cerrar.setBackground(new Color(40, 60, 120));
        cerrar.setForeground(Color.WHITE);
        cerrar.setFont(new Font("SansSerif", Font.BOLD, 13));
        cerrar.setFocusPainted(false);
        cerrar.setBorderPainted(false);
        cerrar.addActionListener(e -> dispose());
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.setBackground(new Color(15, 20, 40));
        sur.setBorder(new EmptyBorder(0, 0, 10, 0));
        sur.add(cerrar);
        add(sur, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }
}