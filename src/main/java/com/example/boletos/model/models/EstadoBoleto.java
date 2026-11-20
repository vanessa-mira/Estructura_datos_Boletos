package com.example.boletos.model.models;

public enum EstadoBoleto {
    ACTIVO("Activo"), MODIFICADO("Modificado"), CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoBoleto(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}
