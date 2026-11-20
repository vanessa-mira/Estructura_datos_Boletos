package com.example.boletos.model.models;

public class Pasajero {
    private String nombre;
    private int edad;
    private TipoPasajero tipo = TipoPasajero.GENERAL;

    public Pasajero() {}

    public Pasajero(String nombre, int edad, TipoPasajero tipo) {
        this.nombre = nombre; this.edad = edad; this.tipo = tipo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public TipoPasajero getTipo() { return tipo; }
    public void setTipo(TipoPasajero tipo) { this.tipo = tipo; }
}
