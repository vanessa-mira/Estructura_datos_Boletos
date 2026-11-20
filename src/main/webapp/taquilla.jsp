<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/layout/header.jsp">
    <jsp:param name="titulo" value="Taquilla"/>
    <jsp:param name="activo" value="taquilla"/>
</jsp:include>

<div class="card shadow-sm mb-4">
    <div class="card-body">
        <h1 class="h4">Taquilla</h1>
        <p class="text-muted">Hola, <strong><c:out value="${sessionScope.usuario.nombre}"/></strong> (${sessionScope.usuario.rol.etiqueta}).
            Vende boletos, cancela o modifica, y atiende la lista de espera.</p>
        <a class="btn btn-primary me-2" href="${ctx}/corrida"><i class="bi bi-cart-plus me-1"></i>Vender boleto</a>
        <a class="btn btn-outline-primary" href="${ctx}/boleto"><i class="bi bi-ticket-perforated me-1"></i>Buscar / cancelar boleto</a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">
        <h2 class="h5">Próximas corridas</h2>
        <div class="table-responsive">
            <table class="table align-middle">
                <thead class="table-light">
                <tr><th>Salida</th><th>Ruta</th><th>Autobús</th><th>Ocupación</th><th>Estado</th><th>Lista de espera</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach items="${corridas}" var="c">
                    <tr>
                        <td>${c.fechaTexto}<br><small class="text-muted">${c.horaTexto}</small></td>
                        <td><c:out value="${c.ruta.nombre}"/></td>
                        <td>${c.autobus.numeroEconomico}</td>
                        <td>
                            <div class="progress barra-ocupacion"><div class="progress-bar" style="width:${c.porcentajeOcupacion}%"></div></div>
                            <small class="text-muted">${c.ocupados}/${c.autobus.capacidad}</small>
                        </td>
                        <td><span class="badge ${c.estado == 'Disponible' ? 'bg-success' : 'bg-warning text-dark'}">${c.estado}</span></td>
                        <td>
                            <c:choose>
                                <c:when test="${c.enEspera > 0}">
                                    <details>
                                        <summary>${c.enEspera} en espera</summary>
                                        <ol class="mb-0 ps-3">
                                            <c:forEach items="${c.pasajerosEnEspera}" var="p">
                                                <li><c:out value="${p.nombre}"/> <small class="text-muted">(${p.tipo.etiqueta})</small></li>
                                            </c:forEach>
                                        </ol>
                                    </details>
                                </c:when>
                                <c:otherwise><small class="text-muted">—</small></c:otherwise>
                            </c:choose>
                        </td>
                        <td><a class="btn btn-sm btn-primary" href="${ctx}/corrida?action=asientos&idCorrida=${c.id}">${c.llena ? 'Anotar en espera' : 'Vender'}</a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/layout/footer.jsp"/>
