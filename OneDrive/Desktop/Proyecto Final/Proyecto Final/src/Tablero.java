package puzzle8.model;

import java.util.Arrays;

/**
 * Clase que representa el estado del tablero del 8-puzzle.
 * Encapsula la cuadrícula 3x3 y las operaciones válidas sobre ella.
 */
public class Tablero implements Cloneable {

    public static final int TAMANIO = 3;
    private int[][] cuadricula;
    private int filaVacio;
    private int colVacio;

    /**
     * Constructor: crea tablero con configuración dada.
     * @param cuadricula matriz 3x3 con valores del 0 al 8 (0 = espacio vacío)
     */
    public Tablero(int[][] cuadricula) {
        this.cuadricula = new int[TAMANIO][TAMANIO];
        for (int i = 0; i < TAMANIO; i++) {
            for (int j = 0; j < TAMANIO; j++) {
                this.cuadricula[i][j] = cuadricula[i][j];
                if (cuadricula[i][j] == 0) {
                    filaVacio = i;
                    colVacio = j;
                }
            }
        }
    }

    /**
     * Obtiene el valor en una posición del tablero.
     */
    public int getValor(int fila, int col) {
        return cuadricula[fila][col];
    }

    public int getFilaVacio() { return filaVacio; }
    public int getColVacio()  { return colVacio; }

    /**
     * Intenta mover la pieza adyacente al espacio vacío.
     * @param fila fila de la pieza a mover
     * @param col  columna de la pieza a mover
     * @return true si el movimiento fue válido
     */
    public boolean moverPieza(int fila, int col) {
        if (!esAdyacente(fila, col)) return false;
        cuadricula[filaVacio][colVacio] = cuadricula[fila][col];
        cuadricula[fila][col] = 0;
        filaVacio = fila;
        colVacio = col;
        return true;
    }

    /**
     * Verifica si una celda es adyacente al espacio vacío.
     */
    public boolean esAdyacente(int fila, int col) {
        return (Math.abs(fila - filaVacio) + Math.abs(col - colVacio)) == 1;
    }

    /**
     * Verifica si el tablero es igual a otro (por contenido).
     */
    public boolean esIgual(Tablero otro) {
        return Arrays.deepEquals(this.cuadricula, otro.cuadricula);
    }

    /**
     * Verifica si el tablero actual es el estado meta dado.
     */
    public boolean esMeta(Tablero meta) {
        return esIgual(meta);
    }

    /**
     * Calcula la heurística de Distancia Manhattan respecto al estado meta.
     * h(n) = Σ |fila_actual - fila_meta| + |col_actual - col_meta|
     */
    public int distanciaManhattan(Tablero meta) {
        int distancia = 0;
        for (int i = 0; i < TAMANIO; i++) {
            for (int j = 0; j < TAMANIO; j++) {
                int valor = cuadricula[i][j];
                if (valor == 0) continue;
                // Buscar la posición de 'valor' en el tablero meta
                for (int mi = 0; mi < TAMANIO; mi++) {
                    for (int mj = 0; mj < TAMANIO; mj++) {
                        if (meta.cuadricula[mi][mj] == valor) {
                            distancia += Math.abs(i - mi) + Math.abs(j - mj);
                        }
                    }
                }
            }
        }
        return distancia;
    }

    /**
     * Cuenta piezas mal colocadas (Hamming distance) respecto a la meta.
     */
    public int piezasMalColocadas(Tablero meta) {
        int count = 0;
        for (int i = 0; i < TAMANIO; i++)
            for (int j = 0; j < TAMANIO; j++)
                if (cuadricula[i][j] != 0 && cuadricula[i][j] != meta.cuadricula[i][j])
                    count++;
        return count;
    }

    /**
     * Genera un tablero aleatorio válido (solucionable).
     */
    public static Tablero generarAleatorio() {
        int[] vals = {0,1,2,3,4,5,6,7,8};
        // Fisher-Yates shuffle
        java.util.Random rnd = new java.util.Random();
        for (int i = 8; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = vals[i]; vals[i] = vals[j]; vals[j] = tmp;
        }
        int[][] mat = new int[TAMANIO][TAMANIO];
        for (int i = 0; i < 9; i++) mat[i/3][i%3] = vals[i];
        Tablero t = new Tablero(mat);
        // Si no es solucionable, intercambiar dos piezas no vacías
        if (!t.esSolucionable()) {
            if (mat[0][0] != 0 && mat[0][1] != 0) { int tmp = mat[0][0]; mat[0][0] = mat[0][1]; mat[0][1] = tmp; }
            else { int tmp = mat[2][1]; mat[2][1] = mat[2][2]; mat[2][2] = tmp; }
            t = new Tablero(mat);
        }
        return t;
    }

    /**
     * Estado meta clásico (1-8 seguido del espacio).
     */
    public static Tablero metaClasica() {
        return new Tablero(new int[][]{{1,2,3},{4,5,6},{7,8,0}});
    }

    /**
     * Verifica si el tablero tiene solución usando la paridad de inversiones.
     */
    public boolean esSolucionable() {
        int[] lineal = new int[9];
        int idx = 0;
        for (int i = 0; i < TAMANIO; i++)
            for (int j = 0; j < TAMANIO; j++)
                lineal[idx++] = cuadricula[i][j];
        int inversiones = 0;
        for (int i = 0; i < 9; i++)
            for (int j = i + 1; j < 9; j++)
                if (lineal[i] != 0 && lineal[j] != 0 && lineal[i] > lineal[j])
                    inversiones++;
        return inversiones % 2 == 0;
    }

    /**
     * Crea una copia profunda del tablero.
     */
    @Override
    public Tablero clone() {
        try {
            Tablero copia = (Tablero) super.clone();
            copia.cuadricula = new int[TAMANIO][TAMANIO];
            for (int i = 0; i < TAMANIO; i++)
                copia.cuadricula[i] = this.cuadricula[i].clone();
            return copia;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tablero)) return false;
        return Arrays.deepEquals(cuadricula, ((Tablero) obj).cuadricula);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(cuadricula);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] fila : cuadricula) {
            for (int v : fila) sb.append(v == 0 ? " " : v).append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }
}