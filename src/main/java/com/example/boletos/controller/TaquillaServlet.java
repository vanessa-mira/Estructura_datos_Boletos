package com.example.boletos.controller;

import com.example.boletos.model.dao.CorridaDao;
import com.example.boletos.model.dao.RutaDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

/** Panel del vendedor: proximas corridas, ocupacion y listas de espera. */
@WebServlet(name = "TaquillaServlet", value = "/taquilla")
public class TaquillaServlet extends HttpServlet {
    private final CorridaDao corridaDao = new CorridaDao();
    private final RutaDao rutaDao = new RutaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("corridas", corridaDao.getProximas(40));
        request.setAttribute("ciudades", rutaDao.getCiudades());
        request.setAttribute("hoy", LocalDate.now().toString());
        request.getRequestDispatcher("taquilla.jsp").forward(request, response);
    }
}
