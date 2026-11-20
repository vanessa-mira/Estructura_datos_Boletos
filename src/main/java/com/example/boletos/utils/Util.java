package com.example.boletos.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalTime;

/** Utilidades pequenas compartidas por los servlets. */
public final class Util {
    private Util() {
    }

    public static int entero(String texto, int porDefecto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (Exception e) {
            return porDefecto;
        }
    }

    public static LocalDate fecha(String texto) {
        try {
            return LocalDate.parse(texto.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalTime hora(String texto) {
        try {
            return LocalTime.parse(texto.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /** Nombre de persona: solo letras, espacios, punto, apostrofe y guion (3 a 80 caracteres). */
    public static boolean nombreValido(String nombre) {
        return nombre != null && nombre.matches("^[\\p{L} .'-]{3,80}$");
    }

    /** Lee adultos / ninos / mayores (0 a 9) del request y los deja como atributos junto con "total". */
    public static void cargarCantidades(HttpServletRequest req) {
        int adultos = Math.max(0, Math.min(9, entero(req.getParameter("adultos"), 1)));
        int ninos = Math.max(0, Math.min(9, entero(req.getParameter("ninos"), 0)));
        int mayores = Math.max(0, Math.min(9, entero(req.getParameter("mayores"), 0)));
        if (adultos + ninos + mayores == 0) {
            adultos = 1;
        }
        req.setAttribute("adultos", adultos);
        req.setAttribute("ninos", ninos);
        req.setAttribute("mayores", mayores);
        req.setAttribute("total", adultos + ninos + mayores);
    }

    public static String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    /** Mensajes "flash": se guardan en la sesion y se muestran una sola vez en la siguiente pagina. */
    public static void ok(HttpServletRequest req, String mensaje) {
        req.getSession().setAttribute("flashOk", mensaje);
    }

    public static void error(HttpServletRequest req, String mensaje) {
        req.getSession().setAttribute("flashError", mensaje);
    }
}
