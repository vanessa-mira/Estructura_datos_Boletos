package com.example.boletos.model.dao;

import com.example.boletos.DB.BaseDatos;
import com.example.boletos.model.models.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/** Boletos: compra, consulta, modificacion y cancelacion. Usa la matriz de asientos y la cola de espera. */
public class BoletoDao implements Dao<Boleto, String> {
    private final BaseDatos db = BaseDatos.getInstance();
    private final CorridaDao corridaDao = new CorridaDao();

    @Override
    public boolean create(Boleto boleto) {
        synchronized (BaseDatos.LOCK) {
            if (getById(boleto.getFolio()) != null) return false;
            db.boletos.agregar(boleto);
            return true;
        }
    }

    @Override
    public List<Boleto> getAll() {
        synchronized (BaseDatos.LOCK) {
            return db.boletos.toList();
        }
    }

    /** Funcion 4 (por folio): recorre la lista enlazada de boletos. */
    @Override
    public Boleto getById(String folio) {
        if (folio == null) return null;
        String f = folio.trim();
        synchronized (BaseDatos.LOCK) {
            return db.boletos.buscar(b -> b.getFolio().equalsIgnoreCase(f));
        }
    }

    @Override
    public boolean update(Boleto boleto) {
        synchronized (BaseDatos.LOCK) {
            return getById(boleto.getFolio()) != null; // los boletos se modifican con modificar()
        }
    }

    /** Elimina el boleto de la lista y libera su asiento si seguia vigente. */
    @Override
    public boolean delete(String folio) {
        synchronized (BaseDatos.LOCK) {
            Boleto b = getById(folio);
            if (b == null) return false;
            if (b.isVigente()) b.getCorrida().getMapa().liberar(b.getAsiento());
            return db.boletos.eliminar(x -> x == b);
        }
    }

    /** Funcion 4 (por nombre). */
    public List<Boleto> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return new ArrayList<>();
        String n = CorridaDao.normalizar(nombre);
        synchronized (BaseDatos.LOCK) {
            return db.boletos.filtrar(b -> CorridaDao.normalizar(b.getPasajero().getNombre()).contains(n)).toList();
        }
    }

    /**
     * Funciones 2 y 3: compra uno o varios boletos de una corrida. Es "todo o nada":
     * si algun asiento ya esta ocupado no se vende ninguno (evita la sobreventa).
     * @throws IllegalStateException con el mensaje a mostrar al usuario
     */
    public List<Boleto> comprar(int idCorrida, List<Integer> asientos, List<Pasajero> pasajeros, String vendedor) {
        synchronized (BaseDatos.LOCK) {
            Corrida corrida = corridaDao.getById(idCorrida);
            if (corrida == null || !corrida.isVendible()) {
                throw new IllegalStateException("La corrida ya no está disponible.");
            }
            if (asientos.isEmpty() || asientos.size() != pasajeros.size()) {
                throw new IllegalStateException("Debes elegir un asiento por cada pasajero.");
            }
            if (new HashSet<>(asientos).size() != asientos.size()) {
                throw new IllegalStateException("No puedes repetir asientos.");
            }
            for (int asiento : asientos) {
                if (!corrida.getMapa().estaLibre(asiento)) {
                    throw new IllegalStateException("El asiento " + asiento
                            + " ya fue ocupado por otra persona. Elige otro.");
                }
            }
            List<Boleto> vendidos = new ArrayList<>();
            for (int i = 0; i < asientos.size(); i++) {
                vendidos.add(db.nuevoBoleto(corrida, asientos.get(i), pasajeros.get(i), vendedor));
            }
            return vendidos;
        }
    }

    /** Funcion 5: cambia el boleto a otro asiento y/o a otra corrida de la misma ruta. */
    public void modificar(String folio, int idCorrida, int nuevoAsiento) {
        synchronized (BaseDatos.LOCK) {
            Boleto boleto = getById(folio);
            if (boleto == null || !boleto.isVigente()) {
                throw new IllegalStateException("El boleto no existe o ya está cancelado.");
            }
            if (!boleto.getCorrida().isVendible()) {
                throw new IllegalStateException("Este boleto ya no se puede modificar (el viaje ya salió o se canceló).");
            }
            Corrida destino = corridaDao.getById(idCorrida);
            if (destino == null || !destino.isVendible()
                    || destino.getRuta().getIdRuta() != boleto.getCorrida().getRuta().getIdRuta()) {
                throw new IllegalStateException("La corrida elegida no es válida para este boleto.");
            }
            if (destino == boleto.getCorrida() && nuevoAsiento == boleto.getAsiento()) {
                throw new IllegalStateException("Elegiste el mismo asiento y la misma corrida.");
            }
            if (!destino.getMapa().estaLibre(nuevoAsiento)) {
                throw new IllegalStateException("El asiento " + nuevoAsiento + " no está disponible.");
            }
            Corrida origen = boleto.getCorrida();
            origen.getMapa().liberar(boleto.getAsiento());
            destino.getMapa().ocupar(nuevoAsiento, boleto.getFolio());
            boleto.mover(destino, nuevoAsiento);
            if (origen != destino) {
                atenderListaEspera(origen);
            }
        }
    }

    /**
     * Reglas de reembolso: 100% si faltan 24 horas o mas, 50% si faltan 2 horas
     * o mas, y 0% si falta menos.
     */
    public static double calcularReembolso(Boleto boleto) {
        long horas = Duration.between(LocalDateTime.now(), boleto.getCorrida().getSalida()).toHours();
        if (horas >= 24) return boleto.getPrecioPagado();
        if (horas >= 2) return Math.round(boleto.getPrecioPagado() * 50.0) / 100.0;
        return 0;
    }

    /**
     * Funcion 6: cancela el boleto, libera el asiento en la matriz y, si hay alguien en la
     * lista de espera, le asigna ese asiento al primero de la cola.
     */
    public ResultadoCancelacion cancelar(String folio) {
        synchronized (BaseDatos.LOCK) {
            Boleto boleto = getById(folio);
            if (boleto == null || !boleto.isVigente()) {
                throw new IllegalStateException("El boleto no existe o ya estaba cancelado.");
            }
            if (!boleto.getCorrida().isVendible()) {
                throw new IllegalStateException("Este boleto ya no se puede cancelar (el viaje ya salió o se canceló).");
            }
            double reembolso = calcularReembolso(boleto);
            boleto.getCorrida().getMapa().liberar(boleto.getAsiento());
            boleto.cancelar(reembolso);
            Boleto nuevo = atenderListaEspera(boleto.getCorrida());
            return new ResultadoCancelacion(boleto, nuevo);
        }
    }

    /** Si hay asiento libre y gente en espera, vende el asiento al primero de la cola. */
    private Boleto atenderListaEspera(Corrida corrida) {
        if (corrida.getListaEspera().estaVacia() || corrida.getMapa().libres() == 0) {
            return null;
        }
        Pasajero siguiente = corrida.getListaEspera().desencolar();
        int asiento = corrida.getMapa().primerLibre();
        return db.nuevoBoleto(corrida, asiento, siguiente, "Lista de espera");
    }
}
