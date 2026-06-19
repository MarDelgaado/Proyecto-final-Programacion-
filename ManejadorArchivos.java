package puzzle8.util;

import puzzle8.model.Puntuacion;

import java.io.*;
import java.util.*;

/**
 * Manejo de archivos de texto para persistir puntuaciones.
 * Lee y escribe en formato CSV: alias,puntos,fecha
 * Utiliza LinkedList (estructura de datos) para almacenar registros.
 */
public class ManejadorArchivos {

    private static final String ARCHIVO = "puntuaciones.csv";

    /**
     * Guarda o actualiza la puntuación de un jugador.
     * Si el alias ya existe, acumula los puntos y actualiza la fecha.
     * @param nueva puntuación a registrar
     */
    public static void guardarPuntuacion(Puntuacion nueva) {
        LinkedList<Puntuacion> lista = cargarTodas();

        boolean encontrado = false;
        for (Puntuacion p : lista) {
            if (p.getAlias().equalsIgnoreCase(nueva.getAlias())) {
                p.sumarPuntos(nueva.getPuntos());
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            lista.add(nueva);
        }

        escribirArchivo(lista);
    }

    /**
     * Carga todas las puntuaciones del archivo.
     * @return LinkedList con todas las entradas (puede estar vacía)
     */
    public static LinkedList<Puntuacion> cargarTodas() {
        LinkedList<Puntuacion> lista = new LinkedList<>();
        File f = new File(ARCHIVO);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    Puntuacion p = Puntuacion.fromCSV(linea);
                    if (p != null) lista.add(p);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer puntuaciones: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Carga las puntuaciones ordenadas de mayor a menor.
     * @return lista ordenada descendentemente por puntos
     */
    public static List<Puntuacion> cargarOrdenadas() {
        List<Puntuacion> lista = new ArrayList<>(cargarTodas());
        Collections.sort(lista);  // usa compareTo de Puntuacion (mayor primero)
        return lista;
    }

    /**
     * Escribe la lista completa en el archivo CSV.
     */
    private static void escribirArchivo(LinkedList<Puntuacion> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Puntuacion p : lista) {
                pw.println(p.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar puntuaciones: " + e.getMessage());
        }
    }

    /**
     * Busca la puntuación de un jugador por alias.
     * @return Puntuacion si existe, null si no
     */
    public static Puntuacion buscarJugador(String alias) {
        for (Puntuacion p : cargarTodas()) {
            if (p.getAlias().equalsIgnoreCase(alias)) return p;
        }
        return null;
    }
}