<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/layout/header.jsp">
    <jsp:param name="titulo" value="Corridas disponibles"/>
    <jsp:param name="activo" value="inicio"/>
</jsp:include>

<c:url var="urlHora" value="/corrida">
    <c:param name="action" value="buscar"/>
    <c:param name="origen" value="${param.origen}"/><c:param name="destino" value="${param.destino}"/>
    <c:param name="fecha" value="${param.fecha}"/><c:param name="hora" value="${param.hora}"/>
    <c:param name="adultos" value="${adultos}"/><c:param name="ninos" value="${ninos}"/><c:param name="mayores" value="${mayores}"/>
    <c:param name="orden" value="hora"/>
</c:url>
<c:url var="urlPrecio" value="/corrida">
    <c:param name="action" value="buscar"/>
    <c:param name="origen" value="${param.origen}"/><c:param name="destino" value="${param.destino}"/>
    <c:param name="fecha" value="${param.fecha}"/><c:param name="hora" value="${param.hora}"/>
    <c:param name="adultos" value="${adultos}"/><c:param name="ninos" value="${ninos}"/><c:param name="mayores" value="${mayores}"/>
    <c:param name="orden" value="precio"/>
</c:url>

<a class="btn btn-link px-0 mb-2" href="${ctx}/corrida"><i class="bi bi-arrow-left"></i> Nueva búsqueda</a>
<div class="card shadow-sm">
    <div class="card-body">
        <h1 class="h4">Corridas disponibles</h1>
        <p class="text-muted">
            <strong><c:out value="${empty param.origen ? 'Cualquier origen' : param.origen}"/></strong> →
            <strong><c:out value="${empty param.destino ? 'Cualquier destino' : param.destino}"/></strong>
            <c:if test="${not empty param.fecha}"> · <c:out value="${param.fecha}"/></c:if>
            · ${total} pasajero(s)
        </p>
        <div class="mb-3">
            Ordenar por:
            <a href="${urlHora}" class="btn btn-sm ${orden != 'precio' ? 'btn-primary' : 'btn-outline-primary'}">Hora de salida</a>
            <a href="${urlPrecio}" class="btn btn-sm ${orden == 'precio' ? 'btn-primary' : 'btn-outline-primary'}">Precio</a>
        </div>

        <c:choose>
            <c:when test="${empty corridas}">
                <div class="text-center text-muted py-5">No encontramos corridas con esos datos. Prueba con otra fecha u otra ciudad.</div>
            </c:when>
            <c:otherwise>
                <div class="list-group list-group-flush">
                    <c:forEach items="${corridas}" var="c">
                        <div class="list-group-item py-3">
                            <div class="row align-items-center g-3">
                                <div class="col-4 col-md-2">
                                    <div class="hora-corrida">${c.horaTexto}</div>
                                    <small class="text-muted">${c.fechaTexto}</small>
                                </div>
                                <div class="col-8 col-md-6">
                                    <strong><c:out value="${c.ruta.nombre}"/></strong><br>
                                    <small class="text-muted">Duración ${c.ruta.duracion} · Autobús ${c.autobus.numeroEconomico}</small><br>
                                    <span class="badge ${c.estado == 'Disponible' ? 'bg-success' : 'bg-warning text-dark'}">${c.estado}</span>
                                    <small class="text-muted ms-1">
                                        <c:choose>
                                            <c:when test="${c.llena}">Sin lugares · ${c.enEspera} en lista de espera</c:when>
                                            <c:otherwise>${c.libres} asientos libres</c:otherwise>
                                        </c:choose>
                                    </small>
                                </div>
                                <div class="col-12 col-md-4 text-md-end">
                                    <small class="text-muted">Desde</small>
                                    <div class="fs-5 fw-bold"><fmt:formatNumber value="${c.ruta.precioBase}" pattern="$#,##0.00"/></div>
                                    <c:url var="urlAsientos" value="/corrida">
                                        <c:param name="action" value="asientos"/>
                                        <c:param name="idCorrida" value="${c.id}"/>
                                        <c:param name="adultos" value="${adultos}"/><c:param name="ninos" value="${ninos}"/><c:param name="mayores" value="${mayores}"/>
                                    </c:url>
                                    <a class="btn ${c.llena ? 'btn-outline-secondary' : 'btn-primary'}" href="${urlAsientos}">
                                        ${c.llena ? 'Lista de espera' : 'Elegir asiento'}
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<jsp:include page="/layout/footer.jsp"/>
