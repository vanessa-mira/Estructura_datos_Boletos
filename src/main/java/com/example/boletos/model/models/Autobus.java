package com.example.boletos.model.models;

public class Autobus {
    private String numeroEconomico;
    private String placas;
    private int capacidad;

    public Autobus() {}

    public Autobus(String numeroEconomico, String placas, int capacidad) {
        this.numeroEconomico = numeroEconomico; this.placas = placas; this.capacidad = capacidad;
    }

    public String getNumeroEconomico() { return numeroEconomico; }
    public void setNumeroEconomico(String numeroEconomico) { this.numeroEconomico = numeroEconomico; }
    public String getPlacas() { return placas; }
    public void setPlacas(String placas) { this.placas = placas; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
}
