package com.example.boletos.estructuras;

/**
 * TAD Mapa de asientos, implementado con una matriz (arreglo bidimensional).
 *
 * El camion se acomoda en filas de 4 asientos (2 a cada lado del pasillo).
 * Cada celda guarda el folio del boleto que ocupa el asiento, o null si esta libre.
 * Los asientos se numeran 1..capacidad de izquierda a derecha y de arriba abajo.
 *
 * Operaciones: estaLibre, ocupar, liberar, folioDe, libres, ocupados, primerLibre.
 */
public class MapaAsientos {
    public static final int COLUMNAS = 4;

    private final String[][] celdas;
    private final int capacidad;
    private final int filas;

    public MapaAsientos(int capacidad) {
        if (capacidad < 1) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0");
        }
        this.capacidad = capacidad;
        this.filas = (capacidad + COLUMNAS - 1) / COLUMNAS;
        this.celdas = new String[filas][COLUMNAS];
    }

    public int getCapacidad() {
        return capacidad;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return COLUMNAS;
    }

    /** Numero de asiento (1..capacidad) de una celda; 0 si la celda no existe en el camion. */
    public int numeroDe(int fila, int columna) {
        int n = fila * COLUMNAS + columna + 1;
        return n <= capacidad ? n : 0;
    }

    private boolean valido(int asiento) {
        return asiento >= 1 && asiento <= capacidad;
    }

    public boolean estaLibre(int asiento) {
        return valido(asiento) && celdas[(asiento - 1) / COLUMNAS][(asiento - 1) % COLUMNAS] == null;
    }

    /** Marca el asiento como ocupado. Devuelve false si no existe o ya estaba ocupado. */
    public boolean ocupar(int asiento, String folio) {
        if (!estaLibre(asiento)) {
            return false;
        }
        celdas[(asiento - 1) / COLUMNAS][(asiento - 1) % COLUMNAS] = folio;
        return true;
    }

    /** Libera el asiento. Devuelve false si no existia o ya estaba libre. */
    public boolean liberar(int asiento) {
        if (!valido(asiento) || estaLibre(asiento)) {
            return false;
        }
        celdas[(asiento - 1) / COLUMNAS][(asiento - 1) % COLUMNAS] = null;
        return true;
    }

    public String folioDe(int asiento) {
        return valido(asiento) ? celdas[(asiento - 1) / COLUMNAS][(asiento - 1) % COLUMNAS] : null;
    }

    public int ocupados() {
        int total = 0;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                if (celdas[f][c] != null) {
                    total++;
                }
            }
        }
        return total;
    }

    public int libres() {
        return capacidad - ocupados();
    }

    /** Primer asiento libre o -1 si el camion esta lleno. */
    public int primerLibre() {
        for (int n = 1; n <= capacidad; n++) {
            if (estaLibre(n)) {
                return n;
            }
        }
        return -1;
    }
}
