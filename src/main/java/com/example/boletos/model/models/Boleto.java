package com.example.boletos.model.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Boleto {
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String folio;
    private Corrida corrida;
    private int asiento;
    private final Pasajero pasajero;
    private final double precioPagado;
    private EstadoBoleto estado = EstadoBoleto.ACTIVO;
    private final LocalDateTime fechaVenta;
    private final String vendedor;
    private double reembolso;

    public Boleto(String folio, Corrida corrida, int asiento, Pasajero pasajero,
                  double precioPagado, String vendedor) {
        this.folio = folio;
        this.corrida = corrida;
        this.asiento = asiento;
        this.pasajero = pasajero;
        this.precioPagado = precioPagado;
        this.vendedor = vendedor;
        this.fechaVenta = LocalDateTime.now();
    }

    public String getFolio() { return folio; }
    public Corrida getCorrida() { return corrida; }
    public int getAsiento() { return asiento; }
    public Pasajero getPasajero() { return pasajero; }
    public double getPrecioPagado() { return precioPagado; }
    public EstadoBoleto getEstado() { return estado; }
    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public String getFechaVentaTexto() { return fechaVenta.format(FECHA_HORA); }
    public String getVendedor() { return vendedor; }
    public double getReembolso() { return reembolso; }

    public boolean isVigente() { return estado != EstadoBoleto.CANCELADO; }

    public void mover(Corrida nuevaCorrida, int nuevoAsiento) {
        this.corrida = nuevaCorrida;
        this.asiento = nuevoAsiento;
        this.estado = EstadoBoleto.MODIFICADO;
    }

    public void cancelar(double reembolso) {
        this.estado = EstadoBoleto.CANCELADO;
        this.reembolso = reembolso;
    }
}
