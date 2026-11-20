package com.example.boletos.controller;

import com.example.boletos.model.dao.UsuarioDao;
import com.example.boletos.model.models.Usuario;
import com.example.boletos.utils.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Inicio de sesion del personal (vendedor y administrador). */
@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    private final UsuarioDao dao = new UsuarioDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("destino", Util.texto(request.getParameter("destino")));
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario usuario = dao.login(request.getParameter("usuario"), request.getParameter("contra"));
        String destino = Util.texto(request.getParameter("destino"));

        if (usuario == null) {
            request.setAttribute("error", "Usuario o contraseña incorrectos.");
            request.setAttribute("destino", destino);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        request.getSession().invalidate();
        HttpSession session = request.getSession(true);
        session.setAttribute("usuario", usuario);
        session.setAttribute("idRol", usuario.getIdRol());

        boolean destinoSeguro = destino.startsWith("/") && !destino.startsWith("//") && !destino.contains("\\");
        response.sendRedirect(request.getContextPath() + (destinoSeguro ? destino : "/taquilla"));
    }
}
