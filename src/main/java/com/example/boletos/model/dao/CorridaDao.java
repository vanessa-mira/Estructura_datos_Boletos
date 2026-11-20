package com.example.boletos.model.dao;

import com.example.boletos.DB.BaseDatos;
import com.example.boletos.estructuras.ListaSimple;
import com.example.boletos.model.models.Corrida;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CorridaDao implements Dao<Corrida, Integer> {
    private final BaseDatos db = BaseDatos.getInstance();

    @Override
    public boolean create(Corrida corrida) {
        synchronized (BaseDatos.LOCK) {
            corrida.setId(db.siguienteIdCorrida());
            db.corridas.agregar(corrida);
            return true;
        }
    }

    /** Todas las corridas ordenadas por fecha y hora de salida. */
    @Override
    public List<Corrida> getAll() {
        synchronized (BaseDatos.LOCK) {
            List<Corrida> lista = db.corridas.toList();
            lista.sort(Comparator.comparing(Corrida::getSalida));
            return lista;
        }
    }

    @Override
    public Corrida getById(Integer id) {
        synchronized (BaseDatos.LOCK) {
            return db.corridas.buscar(c -> c.getId() == id);
        }
    }

    /** Solo permite cambiar el estado cancelada/no cancelada. */
    @Override
    public boolean update(Corrida nueva) {
        synchronized (BaseDatos.LOCK) {
            Corrida actual = getById(nueva.getId());
            if (actual == null) return false;
            actual.setCancelada(nueva.isCancelada());
            return true;
        }
    }

    /** Una corrida solo se elimina si no tiene boletos vigentes. */
    @Override
    public boolean delete(Integer id) {
        synchronized (BaseDatos.LOCK) {
            Corrida c = getById(id);
            if (c == null || c.getOcupados() > 0) return false;
            return db.corridas.eliminar(x -> x == c);
        }
    }

    /** Corridas que todavia se pueden vender, en orden de salida (maximo "limite"). */
    public List<Corrida> getProximas(int limite) {
        List<Corrida> proximas = new ArrayList<>();
        for (Corrida c : getAll()) {
            if (c.isVendible() && proximas.size() < limite) proximas.add(c);
        }
        return proximas;
    }

    /**
     * Funcion 1: consultar corridas disponibles. Los filtros vacios se ignoran.
     * orden: "precio" o cualquier otro valor (por fecha y hora de salida).
     */
    public List<Corrida> buscar(String origen, String destino, LocalDate fecha, LocalTime horaDesde, String orden) {
        synchronized (BaseDatos.LOCK) {
            ListaSimple<Corrida> encontradas = db.corridas.filtrar(c ->
                    c.isVendible()
                            && contiene(c.getRuta().getOrigen(), origen)
                            && contiene(c.getRuta().getDestino(), destino)
                            && (fecha == null || c.getFecha().equals(fecha))
                            && (horaDesde == null || !c.getHora().isBefore(horaDesde)));
            List<Corrida> resultado = encontradas.toList();
            if ("precio".equals(orden)) {
                resultado.sort(Comparator.comparingDouble((Corrida c) -> c.getRuta().getPrecioBase())
                        .thenComparing(Corrida::getSalida));
            } else {
                resultado.sort(Comparator.comparing(Corrida::getSalida));
            }
            return resultado;
        }
    }

    /** true si el autobus ya tiene otra corrida a esa fecha y hora. */
    public boolean hayChoque(String numeroAutobus, LocalDate fecha, LocalTime hora) {
        synchronized (BaseDatos.LOCK) {
            return db.corridas.buscar(c -> !c.isCancelada()
                    && c.getAutobus().getNumeroEconomico().equals(numeroAutobus)
                    && c.getFecha().equals(fecha) && c.getHora().equals(hora)) != null;
        }
    }

    private boolean contiene(String texto, String filtro) {
        return filtro == null || filtro.isBlank() || normalizar(texto).contains(normalizar(filtro));
    }

    /** Minusculas y sin acentos, para que "mexico" encuentre "México". */
    public static String normalizar(String texto) {
        return Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
