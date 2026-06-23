package puzzle8.view;

import puzzle8.controller.ControladorJuego;
import puzzle8.model.Tablero;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Ventana principal del juego 8-Puzzle.
 * Hereda de JFrame y orquesta todos los paneles y diálogos.
 * Implementa el patrón MVC conectando vista ↔ controlador.
 */
public class VentanaPrincipal extends JFrame {

    // ─── Componentes ──────────────────────────────────────────
    private final ControladorJuego ctrl;
    private PanelTablero panelTablero;
    private PanelTablero panelMeta;

    // Info labels
    private JLabel lblModo;
    private JLabel lblPuntos;
    private JLabel lblMovimientos;
    private JLabel lblPaso;
    private JLabel lblInfo;
    private JLabel lblJugador;

    // Botones modo inteligente
    private JButton btnAnterior;
    private JButton btnSiguiente;
    private JButton btnAutoplay;

    // Timer para autoplay
    private Timer timerAuto;
    private boolean autoPlaying = false;

    // ─── Colores ──────────────────────────────────────────────
    private static final Color BG       = new Color(10, 14, 28);
    private static final Color PANEL_BG = new Color(15, 20, 40);
    private static final Color ACCENT   = new Color(60, 130, 200);
    private static final Color GOLD     = new Color(255, 200, 50);
    private static final Color TXT      = new Color(200, 220, 255);

    public VentanaPrincipal() {
        super("8-Puzzle — Programación III · UAA");
        ctrl = new ControladorJuego();

        // Callbacks del controlador → actualizar vista
        ctrl.setOnActualizarVista(this::actualizarVista);
        ctrl.setOnJuegoTerminado(this::mostrarFelicitaciones);

        construirUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Iniciar preguntando alias y modo
        SwingUtilities.invokeLater(this::mostrarMenuInicio);
    }

    // ─── Construcción de la UI ────────────────────────────────

    private void construirUI() {
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        // ── BARRA SUPERIOR ──
        JPanel barSup = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        barSup.setBackground(PANEL_BG);
        lblJugador   = infoLabel("👤 —");
        lblModo      = infoLabel("Modo: —");
        lblPuntos    = infoLabel("Puntos: 0");
        lblMovimientos = infoLabel("Movs: 0");
        barSup.add(lblJugador);
        barSup.add(sep());
        barSup.add(lblModo);
        barSup.add(sep());
        barSup.add(lblPuntos);
        barSup.add(sep());
        barSup.add(lblMovimientos);
        add(barSup, BorderLayout.NORTH);

        // ── CENTRO: tablero actual + (meta en inteligente) ──
        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        centro.setBackground(BG);

        // Tablero actual
        JPanel panActual = new JPanel(new BorderLayout(0, 8));
        panActual.setBackground(BG);
        JLabel titAct = sectionLabel("ESTADO ACTUAL");
        panelTablero = new PanelTablero(ctrl);
        panActual.add(titAct,       BorderLayout.NORTH);
        panActual.add(panelTablero, BorderLayout.CENTER);
        lblPaso = infoLabel("");
        lblPaso.setHorizontalAlignment(SwingConstants.CENTER);
        panActual.add(lblPaso,      BorderLayout.SOUTH);
        centro.add(panActual);

        // Tablero meta (oculto inicialmente)
        JPanel panMeta = new JPanel(new BorderLayout(0, 8));
        panMeta.setBackground(BG);
        JLabel titMeta = sectionLabel("ESTADO META");
        panelMeta = new PanelTablero(ctrl);
        panelMeta.setMostrarMeta(true);
        panMeta.add(titMeta,    BorderLayout.NORTH);
        panMeta.add(panelMeta,  BorderLayout.CENTER);
        panMeta.setVisible(false);
        centro.add(panMeta);

        add(centro, BorderLayout.CENTER);

        // ── SUR: controles y mensajes ──
        JPanel sur = new JPanel(new BorderLayout(0, 0));
        sur.setBackground(PANEL_BG);

        lblInfo = new JLabel("Selecciona un modo de juego.", SwingConstants.CENTER);
        lblInfo.setForeground(GOLD);
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblInfo.setBorder(new EmptyBorder(6, 10, 4, 10));

        // Botones de control
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.setBackground(PANEL_BG);

        // Botones modo manual
        JButton btnSugerir  = btn("💡 Sugerir",    new Color(180, 120, 0));
        JButton btnDeshacer = btn("↩ Deshacer",    new Color(80, 50, 130));
        JButton btnNuevo    = btn("🔄 Nuevo",       new Color(40, 100, 60));

        // Botones modo inteligente
        btnAnterior = btn("◀ Anterior", new Color(50, 80, 140));
        btnSiguiente= btn("▶ Siguiente",new Color(50, 80, 140));
        btnAutoplay = btn("▶▶ Autoplay",new Color(30, 100, 80));
        btnAnterior.setVisible(false);
        btnSiguiente.setVisible(false);
        btnAutoplay.setVisible(false);

        // Botones globales
        JButton btnPuntuaciones = btn("🏆 Rankings", new Color(100, 70, 10));
        JButton btnInicio       = btn("🏠 Menú",     new Color(60, 30, 80));

        // Acciones
        btnSugerir.addActionListener(e -> accionSugerir());
        btnDeshacer.addActionListener(e -> { ctrl.deshacerMovimiento(); });
        btnNuevo.addActionListener(e -> reiniciarActual());
        btnAnterior.addActionListener(e -> ctrl.retrocederPaso());
        btnSiguiente.addActionListener(e -> ctrl.avanzarPaso());
        btnAutoplay.addActionListener(e -> toggleAutoplay());
        btnPuntuaciones.addActionListener(e -> new DialogoPuntuaciones(this).setVisible(true));
        btnInicio.addActionListener(e -> mostrarMenuInicio());

        botones.add(btnSugerir);
        botones.add(btnDeshacer);
        botones.add(btnNuevo);
        botones.add(btnAnterior);
        botones.add(btnSiguiente);
        botones.add(btnAutoplay);
        botones.add(new JSeparator(SwingConstants.VERTICAL));
        botones.add(btnPuntuaciones);
        botones.add(btnInicio);

        sur.add(lblInfo,  BorderLayout.NORTH);
        sur.add(botones,  BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);
    }

    // ─── Menú de inicio ───────────────────────────────────────

    private void mostrarMenuInicio() {
        // Pedir alias
        String alias = JOptionPane.showInputDialog(this,
                "Ingresa tu alias (nombre de jugador):",
                "8-Puzzle — Identificación",
                JOptionPane.PLAIN_MESSAGE);
        if (alias == null || alias.isBlank()) alias = "Anónimo";

        // Elegir modo
        String[] opciones = {"🎮 Modo Manual", "🤖 Modo Inteligente"};
        int eleccion = JOptionPane.showOptionDialog(this,
                "Elige el modo de juego:",
                "8-Puzzle — Modo de juego",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);

        if (eleccion == 0) {
            iniciarManual(alias);
        } else {
            iniciarInteligente(alias);
        }
    }

    private void iniciarManual(String alias) {
        ctrl.iniciarModoManual(alias);
        setModoUI(false);
        setTitle("8-Puzzle — Manual — " + alias);
    }

    private void iniciarInteligente(String alias) {
        // Configurar estado inicial
        DialogoConfigurarTablero dInicio = new DialogoConfigurarTablero(
                this, "Estado Inicial", Tablero.generarAleatorio());
        dInicio.setVisible(true);
        Tablero inicio = dInicio.getResultado();
        if (inicio == null) { mostrarMenuInicio(); return; }

        // Configurar meta
        DialogoConfigurarTablero dMeta = new DialogoConfigurarTablero(
                this, "Estado Meta", Tablero.metaClasica());
        dMeta.setVisible(true);
        Tablero meta = dMeta.getResultado();
        if (meta == null) { mostrarMenuInicio(); return; }

        ctrl.iniciarModoInteligente(alias, inicio, meta);
        setModoUI(true);
        setTitle("8-Puzzle — Inteligente — " + alias);

        if (!ctrl.tieneSolucion()) {
            lblInfo.setText("⚠ No se encontró solución para esta configuración.");
        } else {
            lblInfo.setText("A* encontró solución en " + ctrl.getTotalPasos()
                    + " movimientos · " + ctrl.getNodosExplorados() + " nodos explorados.");
        }
        pack();
    }

    private void setModoUI(boolean inteligente) {
        // Mostrar/ocultar panel meta
        // Encontrar panel meta por índice
        Component centro = ((BorderLayout) getContentPane().getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        if (centro instanceof JPanel) {
            for (Component c : ((JPanel) centro).getComponents()) {
                if (c instanceof JPanel) {
                    // El segundo JPanel es el de la meta
                    Component[] hijos = ((JPanel) c).getComponents();
                    for (Component h : hijos) {
                        if (h == panelMeta) {
                            ((JPanel) c).setVisible(inteligente);
                        }
                    }
                }
            }
        }
        btnAnterior.setVisible(inteligente);
        btnSiguiente.setVisible(inteligente);
        btnAutoplay.setVisible(inteligente);
        pack();
        actualizarVista();
    }

    // ─── Acciones ─────────────────────────────────────────────

    private void accionSugerir() {
        if (ctrl.getModo() != ControladorJuego.Modo.MANUAL) return;
        Tablero sig = ctrl.sugerirMovimiento();
        if (sig != null) {
            panelTablero.mostrarSugerencia(sig);
            lblInfo.setText("💡 Mueve la pieza resaltada.");
        } else {
            lblInfo.setText("No se puede sugerir movimiento.");
        }
    }

    private void reiniciarActual() {
        detenerAutoplay();
        ctrl.reiniciarPartida();
        lblInfo.setText("Partida reiniciada.");
    }

    private void toggleAutoplay() {
        if (autoPlaying) {
            detenerAutoplay();
        } else {
            iniciarAutoplay();
        }
    }

    private void iniciarAutoplay() {
        if (!ctrl.tieneSolucion()) return;
        autoPlaying = true;
        btnAutoplay.setText("⏹ Detener");
        timerAuto = new Timer();
        timerAuto.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    boolean ok = ctrl.avanzarPaso();
                    if (!ok) detenerAutoplay();
                });
            }
        }, 600, 600);
    }

    private void detenerAutoplay() {
        autoPlaying = false;
        btnAutoplay.setText("▶▶ Autoplay");
        if (timerAuto != null) { timerAuto.cancel(); timerAuto = null; }
    }

    // ─── Actualizar UI ────────────────────────────────────────

    private void actualizarVista() {
        if (ctrl.getTableroActual() == null) return;

        lblJugador.setText("👤 " + ctrl.getAliasJugador());
        lblModo.setText("Modo: " + (ctrl.getModo() == ControladorJuego.Modo.MANUAL
                ? "Manual" : "Inteligente"));
        lblPuntos.setText("Puntos: " + ctrl.getPuntosPartida());
        lblMovimientos.setText("Movs: " + ctrl.getMovimientos());

        if (ctrl.getModo() == ControladorJuego.Modo.INTELIGENTE) {
            lblPaso.setText("Paso " + ctrl.getIndicePaso() + " / " + ctrl.getTotalPasos()
                    + "  —  " + ctrl.getDescripcionPasoActual());
        } else {
            lblPaso.setText("");
        }

        panelTablero.repaint();
        if (panelMeta != null) panelMeta.repaint();
    }

    private void mostrarFelicitaciones() {
        detenerAutoplay();
        int pts = ctrl.getPuntosPartida();
        JOptionPane.showMessageDialog(this,
                "🎉 ¡Puzzle resuelto!\n\n" +
                        "Jugador: " + ctrl.getAliasJugador() + "\n" +
                        "Puntos obtenidos: " + pts + "\n\n" +
                        "Los puntos han sido guardados.",
                "¡Ganaste!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ─── Fábrica de componentes ───────────────────────────────

    private JLabel infoLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(TXT);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        return l;
    }

    private JLabel sectionLabel(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setForeground(ACCENT);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        return l;
    }

    private JButton btn(String texto, Color bg) {
        JButton b = new JButton(texto);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(130, 32));
        return b;
    }

    private JSeparator sep() {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setPreferredSize(new Dimension(1, 20));
        s.setForeground(new Color(60, 80, 120));
        return s;
    }
}