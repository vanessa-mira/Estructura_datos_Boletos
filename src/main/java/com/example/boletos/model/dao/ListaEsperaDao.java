package com.example.boletos.model.dao;

import com.example.boletos.DB.BaseDatos;
import com.example.boletos.model.models.Corrida;
import com.example.boletos.model.models.Pasajero;

import java.util.List;

/**
 * Lista de espera de una corrida llena. Usa la COLA (FIFO) de cada Corrida:
 * el primero en anotarse es el primero en recibir un asiento liberado.
 */
public class ListaEsperaDao {
    private final CorridaDao corridaDao = new CorridaDao();

    /**
     * Encola al pasajero al final de la fila. Solo si la corrida esta llena.
     * @return posicion del pasajero en la fila
     * @throws IllegalStateException con el mensaje a mostrar al usuario
     */
    public int encolar(int idCorrida, Pasajero pasajero) {
        synchronized (BaseDatos.LOCK) {
            Corrida corrida = corridaDao.getById(idCorrida);
            if (corrida == null || !corrida.isVendible()) {
                throw new IllegalStateException("La corrida ya no está disponible.");
            }
            if (!corrida.isLlena()) {
                throw new IllegalStateException("La corrida aún tiene asientos libres, no hace falta lista de espera.");
            }
            corrida.getListaEspera().encolar(pasajero);
            return corrida.getListaEspera().tamanio();
        }
    }

    /** Pasajeros en espera, del primero al ultimo (sin sacarlos de la cola). */
    public List<Pasajero> getAll(int idCorrida) {
        synchronized (BaseDatos.LOCK) {
            Corrida corrida = corridaDao.getById(idCorrida);
            return corrida == null ? List.of() : corrida.getPasajerosEnEspera();
        }
    }
}
