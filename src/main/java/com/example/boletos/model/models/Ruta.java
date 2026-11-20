package com.example.boletos.model.models;

public class Ruta {
    private int idRuta;
    private String origen;
    private String destino;
    private String duracion;
    private double precioBase;

    public Ruta() {}

    public Ruta(int idRuta, String origen, String destino, String duracion, double precioBase) {
        this.idRuta = idRuta; this.origen = origen; this.destino = destino;
        this.duracion = duracion; this.precioBase = precioBase;
    }

    public int getIdRuta() { return idRuta; }
    public void setIdRuta(int idRuta) { this.idRuta = idRuta; }
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }
    public String getNombre() { return origen + " → " + destino; }

    /** Precio final para un tipo de pasajero (aplica el descuento). */
    public double precioPara(TipoPasajero tipo) {
        return Math.round(precioBase * (1 - tipo.getDescuento()) * 100.0) / 100.0;
    }
}
