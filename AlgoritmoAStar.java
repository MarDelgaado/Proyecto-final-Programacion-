package puzzle8.util;

import puzzle8.model.Nodo;
import puzzle8.model.Tablero;

import java.util.*;

/*Implementacion del algoritmo A* para resolver el 8-puzzle. Se usa:
PriorityQueue (cola de prioridad / min-heap) como frontera abierta
HashSet como conjunto cerrado (estados ya explorados)
Heurística: Distancia Manhattan f(n) = g(n) + h(n) */
public class AlgoritmoAStar {
    private int nodosExplorados;
    private List<Nodo> camino;  //secuencia de nodos desde inicio hasta meta
    public AlgoritmoAStar() {
        nodosExplorados = 0;
        camino = new ArrayList<>();
    }

    /*Ejecuta A* desde el estado inicial hasta el estado meta
      @param inicio tablero inicial
      @param meta   tablero objetivo
      @return lista de tableros que forman el camino solución, o null si no hay solución */
    public List<Tablero> resolver(Tablero inicio, Tablero meta) {
        nodosExplorados = 0;
        camino.clear();

        //cola de prioridad ordenada por f(n)
        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
        // Conjunto de estados ya visitados
        HashSet<Tablero> cerrados = new HashSet<>();

        Nodo raiz = new Nodo(inicio, meta);
        abiertos.add(raiz);

        while (!abiertos.isEmpty()) {
            // Seleccionar el nodo con menor f(n)
            Nodo actual = abiertos.poll();
            nodosExplorados++;

            //llegamos a la meta?
            if (actual.getTablero().esMeta(meta)) {
                return reconstruirCamino(actual);
            }

            cerrados.add(actual.getTablero());

            //expandir sucesores (movimientos posibles)
            for (Nodo sucesor : generarSucesores(actual, meta)) {
                if (!cerrados.contains(sucesor.getTablero())) {
                    // Verificar si ya está en abiertos con mayor f(n)
                    boolean yaEstaConMenorF = false;
                    for (Nodo nAbierto : abiertos) {
                        if (nAbierto.getTablero().equals(sucesor.getTablero())
                                && nAbierto.getF() <= sucesor.getF()) {
                            yaEstaConMenorF = true;
                            break;
                        }
                    }
                    if (!yaEstaConMenorF) {
                        abiertos.add(sucesor);
                    }
                }
            }
        }

        return null; // Sin solución
    }

    /*Genera todos los nodos sucesores validos desde el nodo actual
     * Un sucesor es un estado resultante de mover el espacio vacio
     * en las 4 direcciones posibles (arriba, abajo, izquierda, derecha) */
    private List<Nodo> generarSucesores(Nodo nodoActual, Tablero meta) {
        List<Nodo> sucesores = new ArrayList<>();
        Tablero t = nodoActual.getTablero();
        int fv = t.getFilaVacio();
        int cv = t.getColVacio();

        int[][] deltas = {{-1,0},{1,0},{0,-1},{0,1}};
        String[] nombres = {"Arriba","Abajo","Izquierda","Derecha"};

        for (int d = 0; d < 4; d++) {
            int nf = fv + deltas[d][0];
            int nc = cv + deltas[d][1];
            if (nf >= 0 && nf < Tablero.TAMANIO && nc >= 0 && nc < Tablero.TAMANIO) {
                Tablero copia = t.clone();
                copia.moverPieza(nf, nc); // mueve pieza al espacio vacío
                String mov = "Mover " + t.getValor(nf, nc) + " → " + nombres[d];
                sucesores.add(new Nodo(copia, nodoActual, meta, mov));
            }
        }
        return sucesores;
    }

    /*reconstruye el camino desde la raiz hasta el nodo meta, recorre la cadena de padres e invierte la lista*/
    private List<Tablero> reconstruirCamino(Nodo nodoMeta) {
        LinkedList<Tablero> resultado = new LinkedList<>();
        camino.clear();
        Nodo actual = nodoMeta;
        while (actual != null) {
            resultado.addFirst(actual.getTablero());
            camino.add(0, actual);
            actual = actual.getPadre();
        }
        return new ArrayList<>(resultado);
    }

    /*Sugiere el siguiente movimiento desde el estado actual hacia la meta
     * Ejecuta A* y retorna el primer tablero sucesor en el camino */
    public Tablero sugerirMovimiento(Tablero actual, Tablero meta) {
        List<Tablero> sol = resolver(actual, meta);
        if (sol != null && sol.size() > 1) return sol.get(1);
        return null;
    }

    public int getNodosExplorados() { return nodosExplorados; }

    /** Retorna el camino completo como lista de Nodos (con info de movimiento). */
    public List<Nodo> getCamino() { return camino; }
}