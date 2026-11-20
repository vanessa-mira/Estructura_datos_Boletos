package com.example.boletos.controller.filters;

import com.example.boletos.model.models.Usuario;
import com.example.boletos.utils.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Reglas de acceso (como FiltroAutenticacion del proyecto de eventos):
 *  - Publico (cliente, sin cuenta): inicio, buscar corridas, asientos, comprar, consultar boleto, lista de espera.
 *  - Vendedor o administrador: /taquilla.
 *  - Solo administrador: /ruta, /autobus, /usuario, /reporte y la gestion de corridas.
 */
@WebFilter(urlPatterns = {"/*"})
public class FiltroAutenticacion extends HttpFilter {
    private static final List<String> SOLO_ADMIN = List.of("/ruta", "/autobus", "/usuario", "/reporte");
    private static final List<String> ACCIONES_CORRIDA_ADMIN = List.of("gestion", "programar", "cancelar");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8"); // antes de leer cualquier parametro
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String ctx = request.getContextPath();
        String path = request.getRequestURI().substring(ctx.length());

        boolean recursoEstatico = path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/img/");
        if (recursoEstatico) {
            chain.doFilter(req, res);
            return;
        }

        // Las pantallas (JSP) se abren a traves de un servlet, no escribiendo su nombre en el navegador
        if (path.endsWith(".jsp") && !path.equals("/index.jsp") && !path.equals("/login.jsp")) {
            response.sendRedirect(ctx + "/");
            return;
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        String accion = request.getParameter("action");
        boolean soloAdmin = SOLO_ADMIN.contains(path)
                || (path.equals("/corrida") && accion != null && ACCIONES_CORRIDA_ADMIN.contains(accion));
        boolean requiereLogin = soloAdmin || path.equals("/taquilla");

        if (requiereLogin && usuario == null) {
            String destino = path;
            if ("GET".equals(request.getMethod()) && request.getQueryString() != null) {
                destino += "?" + request.getQueryString();
            }
            response.sendRedirect(ctx + "/login?destino=" + URLEncoder.encode(destino, StandardCharsets.UTF_8));
            return;
        }
        if (soloAdmin && !usuario.isAdministrador()) {
            Util.error(request, "Solo el administrador puede entrar a esa sección.");
            response.sendRedirect(ctx + "/taquilla");
            return;
        }
        chain.doFilter(req, res);
    }
}
