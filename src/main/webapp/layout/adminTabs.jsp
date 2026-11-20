<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<ul class="nav nav-tabs mb-4">
    <li class="nav-item"><a class="nav-link ${param.seccion == 'rutas' ? 'active' : ''}" href="${ctx}/ruta">Rutas</a></li>
    <li class="nav-item"><a class="nav-link ${param.seccion == 'autobuses' ? 'active' : ''}" href="${ctx}/autobus">Autobuses</a></li>
    <li class="nav-item"><a class="nav-link ${param.seccion == 'corridas' ? 'active' : ''}" href="${ctx}/corrida?action=gestion">Corridas</a></li>
    <li class="nav-item"><a class="nav-link ${param.seccion == 'usuarios' ? 'active' : ''}" href="${ctx}/usuario">Usuarios</a></li>
    <li class="nav-item"><a class="nav-link ${param.seccion == 'reportes' ? 'active' : ''}" href="${ctx}/reporte">Reportes</a></li>
</ul>
