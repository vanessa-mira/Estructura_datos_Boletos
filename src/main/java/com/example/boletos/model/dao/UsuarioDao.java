package com.example.boletos.model.dao;

import com.example.boletos.DB.BaseDatos;
import com.example.boletos.model.models.Usuario;

import java.util.List;

public class UsuarioDao implements Dao<Usuario, Integer> {
    private final BaseDatos db = BaseDatos.getInstance();

    /** false si el nombre de usuario ya existe. */
    @Override
    public boolean create(Usuario usuario) {
        synchronized (BaseDatos.LOCK) {
            if (existe(usuario.getUsuario())) return false;
            usuario.setId(db.siguienteIdUsuario());
            db.usuarios.agregar(usuario);
            return true;
        }
    }

    @Override
    public List<Usuario> getAll() {
        synchronized (BaseDatos.LOCK) {
            return db.usuarios.toList();
        }
    }

    @Override
    public Usuario getById(Integer id) {
        synchronized (BaseDatos.LOCK) {
            return db.usuarios.buscar(u -> u.getId() == id);
        }
    }

    @Override
    public boolean update(Usuario nuevo) {
        synchronized (BaseDatos.LOCK) {
            Usuario actual = getById(nuevo.getId());
            if (actual == null) return false;
            actual.setNombre(nuevo.getNombre());
            actual.setContrasena(nuevo.getContrasena());
            actual.setRol(nuevo.getRol());
            return true;
        }
    }

    @Override
    public boolean delete(Integer id) {
        synchronized (BaseDatos.LOCK) {
            return db.usuarios.eliminar(u -> u.getId() == id);
        }
    }

    public boolean existe(String nombreUsuario) {
        synchronized (BaseDatos.LOCK) {
            return db.usuarios.buscar(u -> u.getUsuario().equalsIgnoreCase(nombreUsuario.trim())) != null;
        }
    }

    /** Devuelve el usuario si las credenciales son correctas, o null. */
    public Usuario login(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) return null;
        synchronized (BaseDatos.LOCK) {
            return db.usuarios.buscar(u -> u.getUsuario().equals(usuario.trim()) && u.getContrasena().equals(contrasena));
        }
    }
}
