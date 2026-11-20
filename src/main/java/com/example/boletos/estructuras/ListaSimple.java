package com.example.boletos.estructuras;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * TAD Lista simplemente enlazada.
 * Se usa para guardar rutas, autobuses, corridas, boletos y usuarios.
 *
 * Operaciones: agregar, buscar, filtrar, eliminar, tamanio, estaVacia, toList.
 */
public class ListaSimple<T> implements Iterable<T> {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;

    /** Inserta al final de la lista. */
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            cola.siguiente = nuevo;
        }
        cola = nuevo;
        tamanio++;
    }

    /** Devuelve el primer elemento que cumpla la condicion, o null. */
    public T buscar(Predicate<T> condicion) {
        for (Nodo<T> actual = cabeza; actual != null; actual = actual.siguiente) {
            if (condicion.test(actual.dato)) {
                return actual.dato;
            }
        }
        return null;
    }

    /** Devuelve una lista nueva con los elementos que cumplan la condicion. */
    public ListaSimple<T> filtrar(Predicate<T> condicion) {
        ListaSimple<T> resultado = new ListaSimple<>();
        for (Nodo<T> actual = cabeza; actual != null; actual = actual.siguiente) {
            if (condicion.test(actual.dato)) {
                resultado.agregar(actual.dato);
            }
        }
        return resultado;
    }

    /** Elimina el primer elemento que cumpla la condicion. true si se elimino. */
    public boolean eliminar(Predicate<T> condicion) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (condicion.test(actual.dato)) {
                if (anterior == null) {
                    cabeza = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                if (actual == cola) {
                    cola = anterior;
                }
                tamanio--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    /** Copia a java.util.List (las JSP con JSTL recorren List). */
    public List<T> toList() {
        List<T> lista = new ArrayList<>(tamanio);
        for (Nodo<T> actual = cabeza; actual != null; actual = actual.siguiente) {
            lista.add(actual.dato);
        }
        return lista;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }
}
