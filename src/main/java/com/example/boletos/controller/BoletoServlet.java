package com.example.boletos.controller;

import com.example.boletos.model.dao.BoletoDao;
import com.example.boletos.model.dao.CorridaDao;
import com.example.boletos.model.models.*;
import com.example.boletos.utils.AsientoVista;
import com.example.boletos.utils.FilaPasajero;
import com.example.boletos.utils.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Boletos (publico, el cliente no necesita cuenta).
 * GET : (sin action) consultar por folio o nombre | comprar (datos de pasajeros) | modificar
 * POST: comprar | cancelar | modificar
 */
@WebServlet(name = "BoletoServlet", value = "/boleto")
public class BoletoServlet extends HttpServlet {
    private final BoletoDao boletoDao = new BoletoDao();
    private final CorridaDao corridaDao = new CorridaDao();

    // ------------------------------------------------------------------ GET

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = Util.texto(request.getParameter("action"));

        switch (action) {
            case "comprar" -> {
                if (!prepararCompra(request, response)) return;
                List<Integer> asientos = asientosElegidos(request);
                List<FilaPasajero> filas = new ArrayList<>();
                List<TipoPasajero> sugeridos = tiposSugeridos(request);
                for (int i = 0; i < asientos.size(); i++) {
                    filas.add(new FilaPasajero(asientos.get(i), "", "", sugeridos.get(i).name()));
                }
                request.setAttribute("filas", filas);
                mostrarCompra(request, response);
            }
            case "modificar" -> mostrarModificar(request, response);
            default -> consultar(request, response);
        }
    }

    /** Funcion 4: consultar boleto por folio o por nombre. */
    private void consultar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String folio = Util.texto(request.getParameter("folio"));
        String nombre = Util.texto(request.getParameter("nombre"));
        List<Boleto> boletos = new ArrayList<>();
        boolean busco = false;

        if (!folio.isEmpty()) {
            busco = true;
            for (String f : folio.split(",")) {
                Boleto b = boletoDao.getById(f);
                if (b != null) boletos.add(b);
            }
        } else if (!nombre.isEmpty()) {
            busco = true;
            boletos = boletoDao.buscarPorNombre(nombre);
        }
        request.setAttribute("boletos", boletos);
        request.setAttribute("busco", busco);
        request.getRequestDispatcher("consultarBoleto.jsp").forward(request, response);
    }

    private void mostrarModificar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Boleto boleto = boletoDao.getById(request.getParameter("folio"));
        if (boleto == null || !boleto.isVigente() || !boleto.getCorrida().isVendible()) {
            Util.error(request, "Ese boleto no se puede modificar.");
            response.sendRedirect(request.getContextPath() + "/boleto");
            return;
        }
        // Corridas de la misma ruta a las que se puede cambiar
        List<Corrida> opciones = new ArrayList<>();
        for (Corrida c : corridaDao.getAll()) {
            if (c.isVendible() && c.getRuta().getIdRuta() == boleto.getCorrida().getRuta().getIdRuta()
                    && (c == boleto.getCorrida() || c.getLibres() > 0)) {
                opciones.add(c);
            }
        }
        Corrida elegida = corridaDao.getById(Util.entero(request.getParameter("idCorrida"), boleto.getCorrida().getId()));
        if (elegida == null || !opciones.contains(elegida)) {
            elegida = boleto.getCorrida();
        }
        request.setAttribute("boleto", boleto);
        request.setAttribute("opciones", opciones);
        request.setAttribute("elegida", elegida);
        request.setAttribute("filas", AsientoVista.construir(elegida, elegida == boleto.getCorrida() ? boleto.getAsiento() : -1));
        request.getRequestDispatcher("modificar.jsp").forward(request, response);
    }

    // ------------------------------------------------------------------ POST

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = Util.texto(request.getParameter("action"));

        switch (action) {
            case "comprar" -> comprar(request, response);
            case "cancelar" -> cancelar(request, response);
            case "modificar" -> modificar(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/boleto");
        }
    }

    /** Funcion 3: valida los datos de cada pasajero y compra todos los boletos. */
    private void comprar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!prepararCompra(request, response)) return;
        List<Integer> asientos = asientosElegidos(request);
        Corrida corrida = (Corrida) request.getAttribute("corrida");

        List<FilaPasajero> filas = new ArrayList<>();
        List<Pasajero> pasajeros = new ArrayList<>();
        String error = null;
        for (int i = 0; i < asientos.size() && error == null; i++) {
            String nombre = Util.texto(request.getParameter("nombre_" + i));
            String edadTxt = Util.texto(request.getParameter("edad_" + i));
            String tipoTxt = Util.texto(request.getParameter("tipo_" + i));
            filas.add(new FilaPasajero(asientos.get(i), nombre, edadTxt, tipoTxt));

            int edad = Util.entero(edadTxt, -1);
            TipoPasajero tipo;
            try {
                tipo = TipoPasajero.valueOf(tipoTxt);
            } catch (IllegalArgumentException e) {
                tipo = TipoPasajero.GENERAL;
            }
            String quien = "Pasajero del asiento " + asientos.get(i) + ": ";
            if (!Util.nombreValido(nombre)) {
                error = quien + "escribe el nombre completo (solo letras).";
            } else if (edad < 0 || edad > 120) {
                error = quien + "la edad debe estar entre 0 y 120.";
            } else if (tipo == TipoPasajero.NINO && edad > 12) {
                error = quien + "el tipo Niño aplica hasta los 12 años.";
            } else if (tipo == TipoPasajero.ADULTO_MAYOR && edad < 60) {
                error = quien + "el tipo Adulto mayor aplica desde los 60 años.";
            } else {
                pasajeros.add(new Pasajero(nombre, edad, tipo));
            }
        }
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("filas", filas);
            mostrarCompra(request, response);
            return;
        }

        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        String vendedor = usuario == null ? "Web (cliente)" : usuario.getUsuario();
        try {
            List<Boleto> vendidos = boletoDao.comprar(corrida.getId(), asientos, pasajeros, vendedor);
            StringBuilder folios = new StringBuilder();
            for (Boleto b : vendidos) {
                if (folios.length() > 0) folios.append(',');
                folios.append(b.getFolio());
            }
            Util.ok(request, "¡Compra realizada! Guarda tu folio para consultar o cancelar tu boleto.");
            response.sendRedirect(request.getContextPath() + "/boleto?folio=" + folios);
        } catch (IllegalStateException e) {
            Util.error(request, e.getMessage());
            response.sendRedirect(urlAsientos(request, corrida.getId()));
        }
    }

    /** Funcion 6: cancelar boleto (libera el asiento y atiende la cola de espera). */
    private void cancelar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String folio = Util.texto(request.getParameter("folio"));
        try {
            ResultadoCancelacion r = boletoDao.cancelar(folio);
            String msg = String.format("Boleto %s cancelado. Reembolso: $%.2f.", folio, r.getCancelado().getReembolso());
            if (r.getAsignadoDeEspera() != null) {
                msg += " El asiento " + r.getAsignadoDeEspera().getAsiento() + " se asignó a "
                        + r.getAsignadoDeEspera().getPasajero().getNombre()
                        + " de la lista de espera (folio " + r.getAsignadoDeEspera().getFolio() + ").";
            }
            Util.ok(request, msg);
        } catch (IllegalStateException e) {
            Util.error(request, e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/boleto?folio=" + URLEncoder.encode(folio, StandardCharsets.UTF_8));
    }

    /** Funcion 5: modificar asiento y/o corrida. */
    private void modificar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String folio = Util.texto(request.getParameter("folio"));
        int idCorrida = Util.entero(request.getParameter("idCorrida"), -1);
        int asiento = Util.entero(request.getParameter("asiento"), -1);
        try {
            boletoDao.modificar(folio, idCorrida, asiento);
            Util.ok(request, "Boleto " + folio + " actualizado correctamente.");
            response.sendRedirect(request.getContextPath() + "/boleto?folio=" + URLEncoder.encode(folio, StandardCharsets.UTF_8));
        } catch (IllegalStateException e) {
            Util.error(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/boleto?action=modificar&folio="
                    + URLEncoder.encode(folio, StandardCharsets.UTF_8) + "&idCorrida=" + idCorrida);
        }
    }

    // ------------------------------------------------------------------ apoyo

    private void mostrarCompra(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("tipos", TipoPasajero.values());
        request.getRequestDispatcher("comprar.jsp").forward(request, response);
    }

    private List<Integer> asientosElegidos(HttpServletRequest request) {
        List<Integer> asientos = new ArrayList<>();
        String[] valores = request.getParameterValues("asientos");
        if (valores != null) {
            for (String v : valores) {
                int n = Util.entero(v, -1);
                if (n > 0 && !asientos.contains(n)) asientos.add(n);
            }
        }
        return asientos;
    }

    private String urlAsientos(HttpServletRequest request, int idCorrida) {
        return request.getContextPath() + "/corrida?action=asientos&idCorrida=" + idCorrida
                + "&adultos=" + request.getAttribute("adultos")
                + "&ninos=" + request.getAttribute("ninos")
                + "&mayores=" + request.getAttribute("mayores");
    }

    /** Lee corrida, cantidades y asientos. Si algo no cuadra, regresa al mapa de asientos. */
    private boolean prepararCompra(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Corrida corrida = corridaDao.getById(Util.entero(request.getParameter("idCorrida"), -1));
        if (corrida == null || !corrida.isVendible()) {
            Util.error(request, "La corrida no existe o ya no está disponible.");
            response.sendRedirect(request.getContextPath() + "/corrida");
            return false;
        }
        Util.cargarCantidades(request);
        request.setAttribute("corrida", corrida);
        List<Integer> asientos = asientosElegidos(request);
        int total = (int) request.getAttribute("total");

        boolean ocupado = false;
        for (int a : asientos) {
            if (!corrida.getMapa().estaLibre(a)) ocupado = true;
        }
        if (asientos.size() != total || ocupado) {
            Util.error(request, ocupado
                    ? "Alguno de los asientos ya fue ocupado. Elige de nuevo."
                    : "Elige exactamente " + total + " asiento(s) para continuar.");
            response.sendRedirect(urlAsientos(request, corrida.getId()));
            return false;
        }
        request.setAttribute("asientos", asientos);
        return true;
    }

    private List<TipoPasajero> tiposSugeridos(HttpServletRequest request) {
        List<TipoPasajero> lista = new ArrayList<>();
        for (int i = 0; i < (int) request.getAttribute("adultos"); i++) lista.add(TipoPasajero.GENERAL);
        for (int i = 0; i < (int) request.getAttribute("ninos"); i++) lista.add(TipoPasajero.NINO);
        for (int i = 0; i < (int) request.getAttribute("mayores"); i++) lista.add(TipoPasajero.ADULTO_MAYOR);
        return lista;
    }
}
