package com.example.boletos.model.models;

public class Usuario {
    private int id;
    private String nombre;
    private String usuario;
    private String contrasena;
    private Rol rol = Rol.VENDEDOR;

    public Usuario() {}

    public Usuario(int id, String nombre, String usuario, String contrasena, Rol rol) {
        this.id = id; this.nombre = nombre; this.usuario = usuario; this.contrasena = contrasena; this.rol = rol;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public int getIdRol() { return rol.getId(); }
    public boolean isAdministrador() { return rol == Rol.ADMINISTRADOR; }
}
