package com.example.boletos.model.dao;

import com.example.boletos.DB.BaseDatos;
import com.example.boletos.model.models.Ruta;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class RutaDao implements Dao<Ruta, Integer> {
    private final BaseDatos db = BaseDatos.getInstance();

    @Override
    public boolean create(Ruta ruta) {
        synchronized (BaseDatos.LOCK) {
            ruta.setIdRuta(db.siguienteIdRuta());
            db.rutas.agregar(ruta);
            return true;
        }
    }

    @Override
    public List<Ruta> getAll() {
        synchronized (BaseDatos.LOCK) {
            return db.rutas.toList();
        }
    }

    @Override
    public Ruta getById(Integer id) {
        synchronized (BaseDatos.LOCK) {
            return db.rutas.buscar(r -> r.getIdRuta() == id);
        }
    }

    @Override
    public boolean update(Ruta nueva) {
        synchronized (BaseDatos.LOCK) {
            Ruta actual = getById(nueva.getIdRuta());
            if (actual == null) return false;
            actual.setOrigen(nueva.getOrigen());
            actual.setDestino(nueva.getDestino());
            actual.setDuracion(nueva.getDuracion());
            actual.setPrecioBase(nueva.getPrecioBase());
            return true;
        }
    }

    /** No se puede eliminar una ruta que tiene corridas programadas. */
    @Override
    public boolean delete(Integer id) {
        synchronized (BaseDatos.LOCK) {
            if (enUso(id)) return false;
            return db.rutas.eliminar(r -> r.getIdRuta() == id);
        }
    }

    public boolean enUso(int idRuta) {
        synchronized (BaseDatos.LOCK) {
            return db.corridas.buscar(c -> c.getRuta().getIdRuta() == idRuta) != null;
        }
    }

    /** Ciudades (origen y destino) sin repetir, para las sugerencias del buscador. */
    public List<String> getCiudades() {
        synchronized (BaseDatos.LOCK) {
            TreeSet<String> ciudades = new TreeSet<>();
            for (Ruta r : db.rutas) {
                ciudades.add(r.getOrigen());
                ciudades.add(r.getDestino());
            }
            return new ArrayList<>(ciudades);
        }
    }
}
