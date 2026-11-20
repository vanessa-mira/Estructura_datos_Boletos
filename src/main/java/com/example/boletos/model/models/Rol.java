package com.example.boletos.model.models;

public enum Rol {
    ADMINISTRADOR(1, "Administrador"), VENDEDOR(2, "Vendedor");

    private final int id;
    private final String etiqueta;

    Rol(int id, String etiqueta) { this.id = id; this.etiqueta = etiqueta; }

    public int getId() { return id; }
    public String getEtiqueta() { return etiqueta; }

    public static Rol deId(int id) { return id == 1 ? ADMINISTRADOR : VENDEDOR; }
}
