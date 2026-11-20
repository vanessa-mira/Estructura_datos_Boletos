<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/layout/header.jsp">
    <jsp:param name="titulo" value="Modificar boleto"/>
    <jsp:param name="activo" value="boleto"/>
</jsp:include>

<a class="btn btn-link px-0 mb-2" href="${ctx}/boleto?folio=${boleto.folio}"><i class="bi bi-arrow-left"></i> Volver al boleto</a>
<div class="card shadow-sm">
    <div class="card-body">
        <h1 class="h4">Modificar boleto ${boleto.folio}</h1>
        <p class="text-muted">
            Pasajero: <strong><c:out value="${boleto.pasajero.nombre}"/></strong> ·
            Actual: ${boleto.corrida.fechaTexto} ${boleto.corrida.horaTexto}, asiento ${boleto.asiento}
        </p>

        <form action="${ctx}/boleto" method="get" class="mb-3">
            <input type="hidden" name="action" value="modificar">
            <input type="hidden" name="folio" value="${boleto.folio}">
            <label class="form-label fw-semibold">Fecha y horario (misma ruta: <c:out value="${boleto.corrida.ruta.nombre}"/>)</label>
            <select name="idCorrida" class="form-select" onchange="this.form.submit()">
                <c:forEach items="${opciones}" var="o">
                    <option value="${o.id}" ${o.id == elegida.id ? 'selected' : ''}>
                        ${o.fechaTexto} · ${o.horaTexto} · ${o.libres} libres
                    </option>
                </c:forEach>
            </select>
            <noscript><button class="btn btn-outline-secondary mt-2" type="submit">Ver asientos</button></noscript>
        </form>

        <form action="${ctx}/boleto" method="post">
            <input type="hidden" name="action" value="modificar">
            <input type="hidden" name="folio" value="${boleto.folio}">
            <input type="hidden" name="idCorrida" value="${elegida.id}">
            <c:set var="tipoInput" value="radio" scope="request"/>
            <c:set var="nombreInput" value="asiento" scope="request"/>
            <jsp:include page="/layout/mapa.jsp"/>
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 border-top pt-3 mt-3">
                <span>Elige el nuevo asiento en la corrida del ${elegida.fechaTexto} ${elegida.horaTexto}.</span>
                <button class="btn btn-primary" type="submit">Guardar cambio</button>
            </div>
        </form>
    </div>
</div>
<jsp:include page="/layout/footer.jsp"/>
