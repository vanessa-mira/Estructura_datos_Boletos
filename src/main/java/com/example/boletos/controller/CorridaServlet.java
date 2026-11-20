package com.example.boletos.controller;

import com.example.boletos.model.dao.AutobusDao;
import com.example.boletos.model.dao.CorridaDao;
import com.example.boletos.model.dao.RutaDao;
import com.example.boletos.model.models.Autobus;
import com.example.boletos.model.models.Corrida;
import com.example.boletos.model.models.Ruta;
import com.example.boletos.utils.AsientoVista;
import com.example.boletos.utils.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Corridas. Publico: action=inicio (buscador), buscar (resultados) y asientos (mapa del camion).
 * Administrador: action=gestion (lista), POST programar / cancelar.
 */
@WebServlet(name = "CorridaServlet", value = "/corrida")
public class CorridaServlet extends HttpServlet {
    private final CorridaDao corridaDao = new CorridaDao();
    private final RutaDao rutaDao = new RutaDao();
    private final AutobusDao autobusDao = new AutobusDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = Util.texto(request.getParameter("action"));

        switch (action) {
            case "buscar" -> buscar(request, response);
            case "asientos" -> asientos(request, response);
            case "gestion" -> {
                request.setAttribute("corridas", corridaDao.getProximas(60));
                request.setAttribute("rutas", rutaDao.getAll());
                request.setAttribute("autobuses", autobusDao.getAll());
                request.setAttribute("hoy", LocalDate.now().toString());
                request.getRequestDispatcher("corridas.jsp").forward(request, response);
            }
            default -> { // inicio: pantalla del boceto
                request.setAttribute("ciudades", rutaDao.getCiudades());
                request.setAttribute("hoy", LocalDate.now().toString());
                request.getRequestDispatcher("inicio.jsp").forward(request, response);
            }
        }
    }

    /** Funcion 1: consultar corridas disponibles. */
    private void buscar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Util.cargarCantidades(request);
        String orden = Util.texto(request.getParameter("orden"));
        request.setAttribute("corridas", corridaDao.buscar(
                request.getParameter("origen"),
                request.getParameter("destino"),
                Util.fecha(Util.texto(request.getParameter("fecha"))),
                Util.hora(Util.texto(request.getParameter("hora"))),
                orden));
        request.setAttribute("orden", orden);
        request.getRequestDispatcher("resultados.jsp").forward(request, response);
    }

    /** Funcion 2: mapa de asientos (recorre la MATRIZ de la corrida). */
    private void asientos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Corrida corrida = corridaDao.getById(Util.entero(request.getParameter("idCorrida"), -1));
        if (corrida == null || !corrida.isVendible()) {
            Util.error(request, "La corrida no existe o ya no está disponible.");
            response.sendRedirect(request.getContextPath() + "/corrida");
            return;
        }
        Util.cargarCantidades(request);
        request.setAttribute("corrida", corrida);
        request.setAttribute("filas", AsientoVista.construir(corrida, -1));
        request.getRequestDispatcher("asientos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = Util.texto(request.getParameter("action"));

        if ("cancelar".equals(action)) {
            Corrida c = corridaDao.getById(Util.entero(request.getParameter("id"), -1));
            if (c == null) {
                Util.error(request, "La corrida no existe.");
            } else {
                c.setCancelada(true);
                corridaDao.update(c);
                Util.ok(request, "Corrida cancelada: ya no aparece en las búsquedas.");
            }
        } else if ("programar".equals(action)) {
            programar(request);
        }
        response.sendRedirect(request.getContextPath() + "/corrida?action=gestion");
    }

    private void programar(HttpServletRequest request) {
        Ruta ruta = rutaDao.getById(Util.entero(request.getParameter("ruta"), -1));
        String numero = Util.texto(request.getParameter("autobus"));
        Autobus autobus = numero.isEmpty() ? null : autobusDao.getById(numero);
        LocalDate fecha = Util.fecha(Util.texto(request.getParameter("fecha")));
        LocalTime hora = Util.hora(Util.texto(request.getParameter("hora")));

        if (ruta == null || autobus == null || fecha == null || hora == null) {
            Util.error(request, "Selecciona ruta, autobús, fecha y hora.");
        } else if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            Util.error(request, "La salida debe ser en el futuro.");
        } else if (corridaDao.hayChoque(autobus.getNumeroEconomico(), fecha, hora)) {
            Util.error(request, "Ese autobús ya tiene una corrida a esa fecha y hora.");
        } else {
            corridaDao.create(new Corrida(0, ruta, autobus, fecha, hora));
            Util.ok(request, "Corrida programada.");
        }
    }
}
