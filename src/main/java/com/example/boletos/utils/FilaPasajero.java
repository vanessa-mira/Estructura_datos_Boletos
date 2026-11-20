package com.example.boletos.utils;

/** Datos de un pasajero en el formulario de compra (para volver a mostrarlos si hay error). */
public class FilaPasajero {
    private final int asiento;
    private final String nombre;
    private final String edad;
    private final String tipo;

    public FilaPasajero(int asiento, String nombre, String edad, String tipo) {
        this.asiento = asiento;
        this.nombre = nombre;
        this.edad = edad;
        this.tipo = tipo;
    }

    public int getAsiento() { return asiento; }
    public String getNombre() { return nombre; }
    public String getEdad() { return edad; }
    public String getTipo() { return tipo; }
}
