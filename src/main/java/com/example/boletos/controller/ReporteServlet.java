package com.example.boletos.controller;

import com.example.boletos.model.dao.CorridaDao;
import com.example.boletos.model.dao.ReporteDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Reportes de ventas, ingresos y ocupacion (solo administrador). */
@WebServlet(name = "ReporteServlet", value = "/reporte")
public class ReporteServlet extends HttpServlet {
    private final ReporteDao reporteDao = new ReporteDao();
    private final CorridaDao corridaDao = new CorridaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("reporte", reporteDao.generar());
        request.setAttribute("corridas", corridaDao.getProximas(30));
        request.getRequestDispatcher("reportes.jsp").forward(request, response);
    }
}
