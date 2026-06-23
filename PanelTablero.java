package puzzle8.view;

import puzzle8.controller.ControladorJuego;
import puzzle8.model.Tablero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel gráfico que muestra el tablero del 8-puzzle.
 * Hereda de JPanel (herencia de componente Swing).
 * Maneja clics del ratón para mover piezas en modo manual.
 */
public class PanelTablero extends JPanel {

    // ─── Constantes visuales ──────────────────────────────────
    private static final int CELDA     = 100;
    private static final int MARGEN    = 8;
    private static final int ARCO      = 18;
    private static final Color C_FONDO = new Color(15, 20, 40);
    private static final Color C_PIEZA = new Color(30, 80, 160);
    private static final Color C_HOVER = new Color(50, 120, 220);
    private static final Color C_VACIO = new Color(10, 14, 28);
    private static final Color C_TEXTO = new Color(220, 235, 255);
    private static final Color C_SUGER = new Color(255, 180, 30, 80);
    private static final Font  F_NUM   = new Font("SansSerif", Font.BOLD, 36);

    private final ControladorJuego ctrl;
    private int hoverFila = -1;
    private int hoverCol  = -1;
    private Tablero sugerencia;       // si no null, resalta esa celda
    private boolean mostrarMeta;

    /**
     * Constructor: recibe el controlador y configura el panel.
     */
    public PanelTablero(ControladorJuego controlador) {
        this.ctrl = controlador;
        int dim = CELDA * 3 + MARGEN * 4;
        setPreferredSize(new Dimension(dim, dim));
        setBackground(C_FONDO);
        setOpaque(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Listener de clic para mover piezas
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col  = (e.getX() - MARGEN) / (CELDA + MARGEN);
                int fila = (e.getY() - MARGEN) / (CELDA + MARGEN);
                if (fila >= 0 && fila < 3 && col >= 0 && col < 3) {
                    ctrl.moverPieza(fila, col);
                    sugerencia = null;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverFila = hoverCol = -1;
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int c = (e.getX() - MARGEN) / (CELDA + MARGEN);
                int f = (e.getY() - MARGEN) / (CELDA + MARGEN);
                if (f != hoverFila || c != hoverCol) {
                    hoverFila = (f >= 0 && f < 3) ? f : -1;
                    hoverCol  = (c >= 0 && c < 3) ? c : -1;
                    repaint();
                }
            }
        });
    }

    /** Indica si se debe mostrar el tablero meta (en modo inteligente). */
    public void setMostrarMeta(boolean v) {
        mostrarMeta = v;
        repaint();
    }

    /** Resalta la celda que corresponde a la sugerencia. */
    public void mostrarSugerencia(Tablero siguiente) {
        this.sugerencia = siguiente;
        repaint();
    }

    // ─── Pintura ──────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Tablero t = mostrarMeta ? ctrl.getTableroMeta() : ctrl.getTableroActual();
        if (t == null) { g2.dispose(); return; }

        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                dibujarCelda(g2, t, fila, col);
            }
        }
        g2.dispose();
    }

    private void dibujarCelda(Graphics2D g2, Tablero t, int fila, int col) {
        int x = MARGEN + col * (CELDA + MARGEN);
        int y = MARGEN + fila * (CELDA + MARGEN);
        int valor = t.getValor(fila, col);

        RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, CELDA, CELDA, ARCO, ARCO);

        if (valor == 0) {
            // Celda vacía
            g2.setColor(C_VACIO);
            g2.fill(rect);
        } else {
            // Determinar color
            Color base = C_PIEZA;
            boolean esHover    = (fila == hoverFila && col == hoverCol);
            boolean esSuger    = sugerencia != null && sugerencia.getFilaVacio() == fila && sugerencia.getColVacio() == col;

            if (esSuger) {
                // Resaltar con overlay dorado
                g2.setColor(base);
                g2.fill(rect);
                g2.setColor(C_SUGER);
                g2.fill(rect);
            } else if (esHover && ctrl.getTableroActual().esAdyacente(fila, col)) {
                g2.setColor(C_HOVER);
                g2.fill(rect);
            } else {
                // Gradiente sutil
                GradientPaint gp = new GradientPaint(
                        x, y, base.brighter(),
                        x, y + CELDA, base.darker());
                g2.setPaint(gp);
                g2.fill(rect);
            }

            // Borde suave
            g2.setColor(new Color(255, 255, 255, 40));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(rect);

            // Número
            g2.setColor(C_TEXTO);
            g2.setFont(F_NUM);
            FontMetrics fm = g2.getFontMetrics();
            String txt = String.valueOf(valor);
            int tx = x + (CELDA - fm.stringWidth(txt)) / 2;
            int ty = y + (CELDA + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(txt, tx, ty);
        }
    }
}