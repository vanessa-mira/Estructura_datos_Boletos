package com.example.boletos.controller;

import com.example.boletos.model.dao.ListaEsperaDao;
import com.example.boletos.model.models.Pasajero;
import com.example.boletos.model.models.TipoPasajero;
import com.example.boletos.utils.Util;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Anota a una persona en la COLA de espera de una corrida llena (publico). */
@WebServlet(name = "ListaEsperaServlet", value = "/espera")
public class ListaEsperaServlet extends HttpServlet {
    private final ListaEsperaDao dao = new ListaEsperaDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int idCorrida = Util.entero(request.getParameter("idCorrida"), -1);
        String nombre = Util.texto(request.getParameter("nombre"));
        int edad = Util.entero(request.getParameter("edad"), -1);
        TipoPasajero tipo;
        try {
            tipo = TipoPasajero.valueOf(Util.texto(request.getParameter("tipo")));
        } catch (IllegalArgumentException e) {
            tipo = TipoPasajero.GENERAL;
        }
        try {
            if (!Util.nombreValido(nombre)) {
                throw new IllegalStateException("Escribe el nombre completo (solo letras).");
            }
            if (edad < 0 || edad > 120) {
                throw new IllegalStateException("La edad debe estar entre 0 y 120.");
            }
            int posicion = dao.encolar(idCorrida, new Pasajero(nombre, edad, tipo));
            Util.ok(request, nombre + " quedó registrado(a) en la lista de espera, posición " + posicion
                    + ". Si se libera un asiento, se le asignará automáticamente por orden de llegada.");
        } catch (IllegalStateException e) {
            Util.error(request, e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/corrida?action=asientos&idCorrida=" + idCorrida);
    }
}
