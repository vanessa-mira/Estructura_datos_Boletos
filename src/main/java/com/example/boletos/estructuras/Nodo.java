package com.example.boletos.estructuras;

/** Nodo generico para las estructuras dinamicas (lista enlazada y cola). */
public class Nodo<T> {
    T dato;
    Nodo<T> siguiente;

    Nodo(T dato) {
        this.dato = dato;
    }
}
