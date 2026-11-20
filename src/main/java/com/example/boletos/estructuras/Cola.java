package com.example.boletos.estructuras;

import java.util.ArrayList;
import java.util.List;

/**
 * TAD Cola (FIFO) implementada con nodos enlazados.
 * Se usa para la lista de espera de una corrida llena: el primero que
 * se formo es el primero en recibir un asiento cuando se libera uno.
 *
 * Operaciones: encolar, desencolar, frente, estaVacia, tamanio, toList.
 */
public class Cola<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;

    /** Agrega al final de la cola. O(1). */
    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (fin == null) {
            frente = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        tamanio++;
    }

    /** Saca y devuelve el primero de la cola, o null si esta vacia. O(1). */
    public T desencolar() {
        if (frente == null) {
            return null;
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    /** Consulta el primero sin sacarlo. */
    public T frente() {
        return frente == null ? null : frente.dato;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    public int tamanio() {
        return tamanio;
    }

    public List<T> toList() {
        List<T> lista = new ArrayList<>(tamanio);
        for (Nodo<T> actual = frente; actual != null; actual = actual.siguiente) {
            lista.add(actual.dato);
        }
        return lista;
    }
}
