package puzzle8.controller;

import puzzle8.model.Nodo;
import puzzle8.model.Puntuacion;
import puzzle8.model.Tablero;
import puzzle8.util.AlgoritmoAStar;
import puzzle8.util.ManejadorArchivos;

import java.util.List;
import java.util.Stack;

/*Controlador del juego
 Coordina el modelo (Tablero, Puntuacion) con la vista (PanelJuego)
 Implementa la lógica de los modos Manual e Inteligente
 Usa Stack (pila) para el historial de movimientos (deshacer) */
public class ControladorJuego {

    //Modos de juego
    public enum Modo { MANUAL, INTELIGENTE }

    //Estado
    private Tablero tableroActual;
    private Tablero tableroMeta;
    private Modo    modoActual;
    private String  aliasJugador;

    //Puntaje de la partida actual
    private int puntosPartida;

    //Historial de movimientos (pila = LIFO)
    private final Stack<Tablero> historial;

    //Solución A* en modo inteligente
    private List<Tablero> solucion;
    private int indicePaso;

    //Referencia al solucionador
    private final AlgoritmoAStar aStar;

    //Callbacks para notificar a la vista
    private Runnable onActualizarVista;
    private Runnable onJuegoTerminado;

    public ControladorJuego() {
        historial = new Stack<>();
        aStar     = new AlgoritmoAStar();
    }

    //Inicializacion

    /*Inicia una nueva partida en modo Manual
      El tablero se genera aleatoriamente
      @param alias alias del jugador*/
    public void iniciarModoManual(String alias) {
        this.aliasJugador  = alias;
        this.modoActual    = Modo.MANUAL;
        this.tableroActual = Tablero.generarAleatorio();
        this.tableroMeta   = Tablero.metaClasica();
        this.puntosPartida = 0;
        historial.clear();
        solucion   = null;
        indicePaso = 0;
        notificarVista();
    }

    /*Inicia una nueva partida en modo Inteligente
     * @param alias  alias del jugador
     * @param inicio tablero de inicio configurado por el usuario
     * @param meta   tablero meta configurado por el usuario*/
    public void iniciarModoInteligente(String alias, Tablero inicio, Tablero meta) {
        this.aliasJugador  = alias;
        this.modoActual    = Modo.INTELIGENTE;
        this.tableroActual = inicio;
        this.tableroMeta   = meta;
        this.puntosPartida = 0;
        historial.clear();
        solucion   = null;
        indicePaso = 0;
        // Resolver con A*
        solucion = aStar.resolver(inicio, meta);
        notificarVista();
    }

    //Acciones del jugador
    /*Modo Manual: mueve la pieza en (fila, col) si es válido
     * @return true si el movimiento fue exitoso
     */
    public boolean moverPieza(int fila, int col) {
        if (modoActual != Modo.MANUAL) return false;
        if (!tableroActual.esAdyacente(fila, col)) return false;

        historial.push(tableroActual.clone()); //guardar estado para deshacer
        tableroActual.moverPieza(fila, col);
        puntosPartida--;  //cada movimiento resta 1 punto

        if (tableroActual.esMeta(tableroMeta)) {
            int bonus = 100 - Math.max(0, historial.size() - 20) * 2;
            puntosPartida += bonus;
            guardarPuntuacion();
            if (onJuegoTerminado != null) onJuegoTerminado.run();
        }
        notificarVista();
        return true;
    }

    /*Modo Manual: deshace el último movimiento (usando la pila)
     @return true si había algo que deshacer*/
    public boolean deshacerMovimiento() {
        if (historial.isEmpty()) return false;
        tableroActual = historial.pop();
        puntosPartida -= 5; // penalización por deshacer
        notificarVista();
        return true;
    }
    /*Modo Manual: sugiere el siguiente movimiento óptimo
      @return el tablero siguiente recomendado (o null)*/
    public Tablero sugerirMovimiento() {
        return aStar.sugerirMovimiento(tableroActual, tableroMeta);
    }
    /*Modo Inteligente: avanza un paso en la solución
     * @return true si se avanzó correctamente*/
    public boolean avanzarPaso() {
        if (solucion == null || indicePaso >= solucion.size() - 1) return false;
        indicePaso++;
        tableroActual = solucion.get(indicePaso);
        if (tableroActual.esMeta(tableroMeta)) {
            puntosPartida = Math.max(50, 500 - indicePaso * 5);
            guardarPuntuacion();
            if (onJuegoTerminado != null) onJuegoTerminado.run();
        }
        notificarVista();
        return true;
    }
    /*Modo Inteligente: retrocede un paso en la solución*/
    public boolean retrocederPaso() {
        if (indicePaso <= 0) return false;
        indicePaso--;
        tableroActual = solucion.get(indicePaso);
        notificarVista();
        return true;
    }

    /*Reinicia el tablero al estado inicial de la partida actual */
    public void reiniciarPartida() {
        if (modoActual == Modo.MANUAL) {
            iniciarModoManual(aliasJugador);
        } else {
            tableroActual = solucion.get(0);
            indicePaso    = 0;
            notificarVista();
        }
    }

    //Persistencia
    private void guardarPuntuacion() {
        if (puntosPartida > 0 && aliasJugador != null && !aliasJugador.isBlank()) {
            ManejadorArchivos.guardarPuntuacion(new Puntuacion(aliasJugador, puntosPartida));
        }
    }

    //getters de estado

    public Tablero getTableroActual()  { return tableroActual; }
    public Tablero getTableroMeta()    { return tableroMeta; }
    public Modo    getModo()           { return modoActual; }
    public int     getPuntosPartida()  { return puntosPartida; }
    public String  getAliasJugador()   { return aliasJugador; }
    public int     getMovimientos()    { return historial.size(); }
    public List<Tablero> getSolucion() { return solucion; }
    public int     getIndicePaso()     { return indicePaso; }
    public int     getTotalPasos()     { return solucion != null ? solucion.size() - 1 : 0; }
    public boolean tieneSolucion()     { return solucion != null; }
    public List<Nodo> getCaminoAStar() { return aStar.getCamino(); }
    public int getNodosExplorados()    { return aStar.getNodosExplorados(); }

    //Descripcion del movimiento en el paso actual (modo inteligente)
    public String getDescripcionPasoActual() {
        List<Nodo> camino = aStar.getCamino();
        if (camino == null || indicePaso >= camino.size()) return "";
        return camino.get(indicePaso).getMovimiento();
    }

    //Callbacks de vista

    public void setOnActualizarVista(Runnable r) { this.onActualizarVista = r; }
    public void setOnJuegoTerminado(Runnable r)  { this.onJuegoTerminado  = r; }

    private void notificarVista() {
        if (onActualizarVista != null) onActualizarVista.run();
    }
}
