package com.example.boletos.model.dao;

import com.example.boletos.DB.BaseDatos;
import com.example.boletos.model.models.Autobus;

import java.util.List;

public class AutobusDao implements Dao<Autobus, String> {
    private final BaseDatos db = BaseDatos.getInstance();

    /** false si ya existe un autobus con ese numero economico. */
    @Override
    public boolean create(Autobus autobus) {
        synchronized (BaseDatos.LOCK) {
            if (getById(autobus.getNumeroEconomico()) != null) return false;
            db.autobuses.agregar(autobus);
            return true;
        }
    }

    @Override
    public List<Autobus> getAll() {
        synchronized (BaseDatos.LOCK) {
            return db.autobuses.toList();
        }
    }

    @Override
    public Autobus getById(String numeroEconomico) {
        synchronized (BaseDatos.LOCK) {
            return db.autobuses.buscar(a -> a.getNumeroEconomico().equalsIgnoreCase(numeroEconomico.trim()));
        }
    }

    /** Solo cambia las placas (la capacidad no se toca si el autobus ya tiene corridas). */
    @Override
    public boolean update(Autobus nuevo) {
        synchronized (BaseDatos.LOCK) {
            Autobus actual = getById(nuevo.getNumeroEconomico());
            if (actual == null) return false;
            actual.setPlacas(nuevo.getPlacas());
            return true;
        }
    }

    @Override
    public boolean delete(String numeroEconomico) {
        synchronized (BaseDatos.LOCK) {
            Autobus a = getById(numeroEconomico);
            if (a == null) return false;
            if (db.corridas.buscar(c -> c.getAutobus() == a) != null) return false;
            return db.autobuses.eliminar(x -> x == a);
        }
    }
}
