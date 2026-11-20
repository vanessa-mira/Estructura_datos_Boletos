<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty param.titulo ? 'Central de Autobuses' : param.titulo}"/> · Central de Autobuses</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${ctx}/css/mapa.css">
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
<!-- barra de navegación -->
<header>
    <nav class="navbar bg-dark navbar-dark navbar-expand-lg">
        <div class="container">
            <a class="navbar-brand" href="${ctx}/corrida"><i class="bi bi-bus-front me-2"></i>Bienvenido a la central</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#menuPrincipal"
                    aria-controls="menuPrincipal" aria-expanded="false" aria-label="Menú">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="menuPrincipal">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item"><a class="nav-link ${param.activo == 'inicio' ? 'active' : ''}" href="${ctx}/corrida">Comprar boletos</a></li>
                    <li class="nav-item"><a class="nav-link ${param.activo == 'boleto' ? 'active' : ''}" href="${ctx}/boleto">Mis boletos</a></li>
                    <c:if test="${not empty sessionScope.usuario}">
                        <li class="nav-item"><a class="nav-link ${param.activo == 'taquilla' ? 'active' : ''}" href="${ctx}/taquilla">Taquilla</a></li>
                    </c:if>
                    <c:if test="${sessionScope.idRol == 1}">
                        <li class="nav-item"><a class="nav-link ${param.activo == 'admin' ? 'active' : ''}" href="${ctx}/ruta">Administración</a></li>
                    </c:if>
                </ul>
                <div class="d-flex align-items-center gap-2">
                    <c:choose>
                        <c:when test="${not empty sessionScope.usuario}">
                            <span class="text-light small"><i class="bi bi-person-circle me-1"></i><c:out value="${sessionScope.usuario.nombre}"/></span>
                            <a href="${ctx}/logout" class="btn btn-outline-danger btn-sm"><i class="bi bi-box-arrow-right me-1"></i>Cerrar sesión</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${ctx}/login" class="btn btn-outline-light btn-sm"><i class="bi bi-person me-1"></i>Personal</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </nav>
</header>

<!-- contenido principal -->
<main class="flex-grow-1 mt-4 mb-5">
    <div class="container">
        <c:if test="${not empty sessionScope.flashOk}">
            <div class="alert alert-success"><i class="bi bi-check-circle me-2"></i><c:out value="${sessionScope.flashOk}"/></div>
            <c:remove var="flashOk" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i><c:out value="${sessionScope.flashError}"/></div>
            <c:remove var="flashError" scope="session"/>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i><c:out value="${error}"/></div>
        </c:if>
