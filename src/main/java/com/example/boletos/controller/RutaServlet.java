package com.example.boletos.controller;

import com.example.boletos.model.dao.RutaDao;
import com.example.boletos.model.models.Ruta;
import com.example.boletos.utils.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** CRUD de rutas (solo administrador). GET: lista / action=editar. POST: action=guardar | eliminar. */
@WebServlet(name = "RutaServlet", value = "/ruta")
public class RutaServlet extends HttpServlet {
    private final RutaDao dao = new RutaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("rutas", dao.getAll());
        if ("editar".equals(request.getParameter("action"))) {
            request.setAttribute("editar", dao.getById(Util.entero(request.getParameter("id"), -1)));
        }
        request.getRequestDispatcher("rutas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = Util.texto(request.getParameter("action"));

        if ("eliminar".equals(action)) {
            boolean ok = dao.delete(Util.entero(request.getParameter("id"), -1));
            if (ok) Util.ok(request, "Ruta eliminada.");
            else Util.error(request, "No se puede eliminar: la ruta tiene corridas programadas.");
        } else if ("guardar".equals(action)) {
            guardar(request);
        }
        response.sendRedirect(request.getContextPath() + "/ruta");
    }

    private void guardar(HttpServletRequest request) {
        int id = Util.entero(request.getParameter("id"), -1);
        String origen = Util.texto(request.getParameter("origen"));
        String destino = Util.texto(request.getParameter("destino"));
        String duracion = Util.texto(request.getParameter("duracion"));
        double precio;
        try {
            precio = Double.parseDouble(Util.texto(request.getParameter("precio")));
        } catch (NumberFormatException e) {
            Util.error(request, "El precio no es un número válido.");
            return;
        }
        if (origen.isEmpty() || destino.isEmpty() || duracion.isEmpty()) {
            Util.error(request, "Origen, destino y duración son obligatorios.");
        } else if (origen.equalsIgnoreCase(destino)) {
            Util.error(request, "El origen y el destino no pueden ser iguales.");
        } else if (precio <= 0) {
            Util.error(request, "El precio debe ser mayor a 0.");
        } else if (id > 0) {
            boolean ok = dao.update(new Ruta(id, origen, destino, duracion, precio));
            if (ok) Util.ok(request, "Ruta actualizada.");
            else Util.error(request, "La ruta no existe.");
        } else {
            dao.create(new Ruta(0, origen, destino, duracion, precio));
            Util.ok(request, "Ruta agregada.");
        }
    }
}
