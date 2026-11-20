package com.example.boletos.model.dao;

import java.util.List;

/** Mismo contrato que el proyecto de eventos: T = entidad, K = tipo de la llave. */
public interface Dao<T, K> {
    boolean create(T entidad);
    List<T> getAll();
    T getById(K id);
    boolean update(T entidad);
    boolean delete(K id);
}
