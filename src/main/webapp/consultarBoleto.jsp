<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/layout/header.jsp">
    <jsp:param name="titulo" value="Consultar boleto"/>
    <jsp:param name="activo" value="boleto"/>
</jsp:include>

<div class="card shadow-sm mb-4 no-print">
    <div class="card-body">
        <h1 class="h4">Consultar boleto</h1>
        <p class="text-muted">Busca tu compra con el número de folio o con el nombre del pasajero.</p>
        <form action="${ctx}/boleto" method="get" class="row g-3 align-items-end">
            <div class="col-md-5">
                <label class="form-label fw-semibold">Folio</label>
                <input type="text" name="folio" class="form-control" placeholder="BUS-000001" value="<c:out value='${param.folio}'/>">
            </div>
            <div class="col-md-5">
                <label class="form-label fw-semibold">o nombre del pasajero</label>
                <input type="text" name="nombre" class="form-control" placeholder="Juan Pérez" value="<c:out value='${param.nombre}'/>">
            </div>
            <div class="col-md-2 d-grid">
                <button class="btn btn-primary" type="submit"><i class="bi bi-search me-1"></i>Buscar</button>
            </div>
        </form>
    </div>
</div>

<c:if test="${busco and empty boletos}">
    <div class="alert alert-warning">No encontramos ningún boleto con esos datos. Revisa el folio o el nombre.</div>
</c:if>

<c:forEach items="${boletos}" var="b">
    <div class="card shadow-sm mb-3 boleto ${b.estado.name() == 'CANCELADO' ? 'cancelado' : ''}">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
                <span class="folio fs-5">${b.folio}</span>
                <span class="badge ${b.estado.name() == 'ACTIVO' ? 'bg-success' : (b.estado.name() == 'MODIFICADO' ? 'bg-warning text-dark' : 'bg-danger')}">${b.estado.etiqueta}</span>
            </div>
            <h2 class="h5 mt-2 mb-3"><c:out value="${b.corrida.ruta.nombre}"/></h2>
            <div class="row g-3">
                <div class="col-6 col-md-3"><small class="text-muted d-block">Salida</small><strong>${b.corrida.fechaTexto} · ${b.corrida.horaTexto}</strong></div>
                <div class="col-6 col-md-3"><small class="text-muted d-block">Asiento</small><strong>${b.asiento}</strong></div>
                <div class="col-6 col-md-3"><small class="text-muted d-block">Autobús</small><strong>${b.corrida.autobus.numeroEconomico}</strong></div>
                <div class="col-6 col-md-3"><small class="text-muted d-block">Pasajero</small><strong><c:out value="${b.pasajero.nombre}"/></strong></div>
                <div class="col-6 col-md-3"><small class="text-muted d-block">Tipo</small><strong>${b.pasajero.tipo.etiqueta}, ${b.pasajero.edad} años</strong></div>
                <div class="col-6 col-md-3"><small class="text-muted d-block">Pagado</small><strong><fmt:formatNumber value="${b.precioPagado}" pattern="$#,##0.00"/></strong></div>
                <div class="col-6 col-md-3"><small class="text-muted d-block">Vendido</small><strong>${b.fechaVentaTexto}</strong></div>
                <c:if test="${b.estado.name() == 'CANCELADO'}">
                    <div class="col-6 col-md-3"><small class="text-muted d-block">Reembolso</small><strong><fmt:formatNumber value="${b.reembolso}" pattern="$#,##0.00"/></strong></div>
                </c:if>
            </div>
        </div>
        <div class="card-footer d-flex flex-wrap gap-2 no-print">
            <button class="btn btn-outline-secondary btn-sm" type="button" onclick="window.print()"><i class="bi bi-printer me-1"></i>Imprimir</button>
            <c:if test="${b.vigente and b.corrida.vendible}">
                <a class="btn btn-outline-primary btn-sm" href="${ctx}/boleto?action=modificar&folio=${b.folio}">Cambiar asiento / fecha</a>
                <form action="${ctx}/boleto" method="post" class="d-inline"
                      onsubmit="return confirm('¿Seguro que quieres cancelar el boleto ${b.folio}? El asiento quedará libre.');">
                    <input type="hidden" name="action" value="cancelar">
                    <input type="hidden" name="folio" value="${b.folio}">
                    <button class="btn btn-outline-danger btn-sm" type="submit">Cancelar boleto</button>
                </form>
            </c:if>
        </div>
    </div>
</c:forEach>

<c:if test="${not empty boletos}">
    <p class="text-muted small no-print">Reembolso al cancelar: 100% si faltan 24 h o más para la salida, 50% si faltan 2 h o más, y 0% si falta menos.</p>
</c:if>
<jsp:include page="/layout/footer.jsp"/>
