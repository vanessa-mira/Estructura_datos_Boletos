package com.example.boletos.controller;

import com.example.boletos.model.dao.AutobusDao;
import com.example.boletos.model.models.Autobus;
import com.example.boletos.utils.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** CRUD de autobuses (solo administrador). POST: action=guardar | eliminar. */
@WebServlet(name = "AutobusServlet", value = "/autobus")
public class AutobusServlet extends HttpServlet {
    private final AutobusDao dao = new AutobusDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("autobuses", dao.getAll());
        request.getRequestDispatcher("autobuses.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = Util.texto(request.getParameter("action"));
        String numero = Util.texto(request.getParameter("numero"));

        if ("eliminar".equals(action)) {
            if (dao.delete(numero)) Util.ok(request, "Autobús eliminado.");
            else Util.error(request, "No se puede eliminar: el autobús tiene corridas asignadas.");
        } else if ("guardar".equals(action)) {
            String placas = Util.texto(request.getParameter("placas"));
            int capacidad = Util.entero(request.getParameter("capacidad"), 0);
            if (numero.isEmpty() || placas.isEmpty()) {
                Util.error(request, "Número económico y placas son obligatorios.");
            } else if (capacidad < 4 || capacidad > 60) {
                Util.error(request, "La capacidad debe estar entre 4 y 60 asientos.");
            } else if (dao.create(new Autobus(numero, placas, capacidad))) {
                Util.ok(request, "Autobús registrado.");
            } else {
                Util.error(request, "Ya existe un autobús con ese número económico.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/autobus");
    }
}
