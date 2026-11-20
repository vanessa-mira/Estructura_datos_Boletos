package com.example.boletos.utils;

import com.example.boletos.estructuras.MapaAsientos;
import com.example.boletos.model.models.Corrida;

import java.util.ArrayList;
import java.util.List;

/** Una celda del mapa del camion, lista para dibujarse en la JSP. */
public class AsientoVista {
    private final int numero;     // 0 = no existe (ultima fila incompleta)
    private final boolean ocupado;
    private final boolean actual; // asiento del boleto que se esta modificando

    private AsientoVista(int numero, boolean ocupado, boolean actual) {
        this.numero = numero;
        this.ocupado = ocupado;
        this.actual = actual;
    }

    public int getNumero() { return numero; }
    public boolean isOcupado() { return ocupado; }
    public boolean isActual() { return actual; }
    public boolean isExiste() { return numero > 0; }

    /** Recorre la MATRIZ del camion y la convierte en filas de asientos. */
    public static List<List<AsientoVista>> construir(Corrida corrida, int asientoActual) {
        MapaAsientos mapa = corrida.getMapa();
        List<List<AsientoVista>> filas = new ArrayList<>();
        for (int f = 0; f < mapa.getFilas(); f++) {
            List<AsientoVista> fila = new ArrayList<>();
            for (int c = 0; c < mapa.getColumnas(); c++) {
                int n = mapa.numeroDe(f, c);
                boolean esActual = n > 0 && n == asientoActual;
                fila.add(new AsientoVista(n, n > 0 && !mapa.estaLibre(n), esActual));
            }
            filas.add(fila);
        }
        return filas;
    }
}
