package com.example.boletos.model.models;

import com.example.boletos.estructuras.Cola;
import com.example.boletos.estructuras.MapaAsientos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Viaje programado. Cada corrida tiene su propia MATRIZ de asientos
 * y su propia COLA de lista de espera.
 */
public class Corrida {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private int id;
    private final Ruta ruta;
    private final Autobus autobus;
    private final LocalDate fecha;
    private final LocalTime hora;
    private boolean cancelada;

    private final MapaAsientos mapa;
    private final Cola<Pasajero> listaEspera = new Cola<>();

    public Corrida(int id, Ruta ruta, Autobus autobus, LocalDate fecha, LocalTime hora) {
        this.id = id;
        this.ruta = ruta;
        this.autobus = autobus;
        this.fecha = fecha;
        this.hora = hora;
        this.mapa = new MapaAsientos(autobus.getCapacidad());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Ruta getRuta() { return ruta; }
    public Autobus getAutobus() { return autobus; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public MapaAsientos getMapa() { return mapa; }
    public Cola<Pasajero> getListaEspera() { return listaEspera; }
    public java.util.List<Pasajero> getPasajerosEnEspera() { return listaEspera.toList(); }
    public boolean isCancelada() { return cancelada; }
    public void setCancelada(boolean cancelada) { this.cancelada = cancelada; }

    public LocalDateTime getSalida() { return LocalDateTime.of(fecha, hora); }
    public String getFechaTexto() { return fecha.format(FECHA); }
    public String getHoraTexto() { return hora.format(HORA); }
    /** Valor yyyy-MM-dd para campos input type=date. */
    public String getFechaIso() { return fecha.toString(); }

    public int getLibres() { return mapa.libres(); }
    public int getOcupados() { return mapa.ocupados(); }
    public int getPorcentajeOcupacion() {
        return (int) Math.round(100.0 * mapa.ocupados() / mapa.getCapacidad());
    }
    public int getEnEspera() { return listaEspera.tamanio(); }
    public boolean isLlena() { return mapa.libres() == 0; }

    public String getEstado() {
        if (cancelada) return "Cancelada";
        if (getSalida().isBefore(LocalDateTime.now())) return "Finalizada";
        if (isLlena()) return "Llena";
        return "Disponible";
    }

    public boolean isVendible() {
        return !cancelada && getSalida().isAfter(LocalDateTime.now());
    }

    public String getEtiqueta() {
        return ruta.getNombre() + " · " + getFechaTexto() + " " + getHoraTexto();
    }
}
