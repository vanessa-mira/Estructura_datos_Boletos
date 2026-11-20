package com.example.boletos.model.models;

public enum TipoPasajero {
    GENERAL("General", 0.0),
    ESTUDIANTE("Estudiante", 0.20),
    NINO("Niño", 0.50),
    ADULTO_MAYOR("Adulto mayor", 0.50);

    private final String etiqueta;
    private final double descuento;

    TipoPasajero(String etiqueta, double descuento) {
        this.etiqueta = etiqueta;
        this.descuento = descuento;
    }

    public String getEtiqueta() { return etiqueta; }
    /** Descuento como fraccion (0.20 = 20%). */
    public double getDescuento() { return descuento; }
    public int getPorcentaje() { return (int) Math.round(descuento * 100); }
    public String getNombre() { return name(); }
}
