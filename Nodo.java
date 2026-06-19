package puzzle8.model;

/**
 * Nodo del árbol de búsqueda A*.
 * Implementa Comparable para ser usado en PriorityQueue.
 * Hereda de Object (base de POO) e implementa Comparable (polimorfismo).
 */
public class Nodo implements Comparable<Nodo> {

    private Tablero tablero;    // Estado del tablero en este nodo
    private Nodo padre;         // Nodo padre (para reconstruir el camino)
    private int g;              // Costo real desde el inicio
    private int h;              // Heurística (distancia Manhattan)
    private int f;              // f(n) = g(n) + h(n)
    private String movimiento;  // Descripción del movimiento que generó este nodo

    /**
     * Constructor del nodo raíz (sin padre).
     */
    public Nodo(Tablero tablero, Tablero meta) {
        this.tablero    = tablero;
        this.padre      = null;
        this.g          = 0;
        this.movimiento = "Estado inicial";
        calcularH(meta);
    }

    /**
     * Constructor para nodos sucesores.
     * @param tablero    estado del tablero
     * @param padre      nodo padre en el árbol
     * @param meta       tablero objetivo
     * @param movimiento descripción del movimiento realizado
     */
    public Nodo(Tablero tablero, Nodo padre, Tablero meta, String movimiento) {
        this.tablero    = tablero;
        this.padre      = padre;
        this.g          = padre.g + 1;
        this.movimiento = movimiento;
        calcularH(meta);
    }

    /** Calcula h(n) con Distancia Manhattan y actualiza f(n). */
    private void calcularH(Tablero meta) {
        this.h = tablero.distanciaManhattan(meta);
        this.f = g + h;
    }

    // ──── Getters ────────────────────────────────────────────
    public Tablero getTablero()  { return tablero; }
    public Nodo    getPadre()    { return padre; }
    public int     getG()        { return g; }
    public int     getH()        { return h; }
    public int     getF()        { return f; }
    public String  getMovimiento(){ return movimiento; }

    /**
     * Comparación por f(n) para PriorityQueue (min-heap).
     */
    @Override
    public int compareTo(Nodo otro) {
        return Integer.compare(this.f, otro.f);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Nodo)) return false;
        return tablero.equals(((Nodo) obj).tablero);
    }

    @Override
    public int hashCode() {
        return tablero.hashCode();
    }
}
