package com.example.boletos.controller;

import com.example.boletos.model.dao.UsuarioDao;
import com.example.boletos.model.models.Rol;
import com.example.boletos.model.models.Usuario;
import com.example.boletos.utils.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** CRUD de usuarios: vendedores y administradores (solo administrador). POST: action=crear | eliminar. */
@WebServlet(name = "UsuarioServlet", value = "/usuario")
public class UsuarioServlet extends HttpServlet {
    private final UsuarioDao dao = new UsuarioDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("usuarios", dao.getAll());
        request.getRequestDispatcher("usuarios.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = Util.texto(request.getParameter("action"));

        if ("eliminar".equals(action)) {
            Usuario yo = (Usuario) request.getSession().getAttribute("usuario");
            int id = Util.entero(request.getParameter("id"), -1);
            if (id == yo.getId()) {
                Util.error(request, "No puedes eliminar tu propio usuario.");
            } else if (dao.delete(id)) {
                Util.ok(request, "Usuario eliminado.");
            }
        } else if ("crear".equals(action)) {
            String nombre = Util.texto(request.getParameter("nombre"));
            String usuario = Util.texto(request.getParameter("usuario"));
            String contra = request.getParameter("contra") == null ? "" : request.getParameter("contra");
            Rol rol = "ADMINISTRADOR".equals(request.getParameter("rol")) ? Rol.ADMINISTRADOR : Rol.VENDEDOR;
            if (nombre.isEmpty() || usuario.isEmpty() || contra.isEmpty()) {
                Util.error(request, "Todos los campos del usuario son obligatorios.");
            } else if (dao.create(new Usuario(0, nombre, usuario, contra, rol))) {
                Util.ok(request, "Usuario creado.");
            } else {
                Util.error(request, "Ese nombre de usuario ya existe.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/usuario");
    }
}
