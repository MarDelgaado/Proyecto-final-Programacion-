package puzzle8.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase que representa el registro de puntuación de un jugador.
 * Contiene alias, puntos y fecha de la partida.
 */
public class Puntuacion implements Comparable<Puntuacion> {

    private String alias;
    private int    puntos;
    private String fecha;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructor para nueva puntuación (fecha automática = ahora).
     */
    public Puntuacion(String alias, int puntos) {
        this.alias  = alias;
        this.puntos = puntos;
        this.fecha  = LocalDateTime.now().format(FMT);
    }

    /**
     * Constructor para cargar puntuación desde archivo.
     */
    public Puntuacion(String alias, int puntos, String fecha) {
        this.alias  = alias;
        this.puntos = puntos;
        this.fecha  = fecha;
    }

    public String getAlias()  { return alias; }
    public int    getPuntos() { return puntos; }
    public String getFecha()  { return fecha; }

    public void sumarPuntos(int extra) {
        this.puntos += extra;
        this.fecha  = LocalDateTime.now().format(FMT);
    }

    /** Serialización a CSV para archivo de texto. */
    public String toCSV() {
        return alias + "," + puntos + "," + fecha;
    }

    /** Crea Puntuacion a partir de línea CSV. */
    public static Puntuacion fromCSV(String linea) {
        String[] partes = linea.split(",", 3);
        if (partes.length < 3) return null;
        try {
            return new Puntuacion(partes[0].trim(),
                    Integer.parseInt(partes[1].trim()),
                    partes[2].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Orden descendente por puntos (mayor primero). */
    @Override
    public int compareTo(Puntuacion otro) {
        return Integer.compare(otro.puntos, this.puntos);
    }

    @Override
    public String toString() {
        return String.format("%-15s %6d pts  [%s]", alias, puntos, fecha);
    }
}