<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/layout/header.jsp">
    <jsp:param name="titulo" value="Datos del pasajero"/>
    <jsp:param name="activo" value="inicio"/>
</jsp:include>

<c:url var="urlAtras" value="/corrida">
    <c:param name="action" value="asientos"/>
    <c:param name="idCorrida" value="${corrida.id}"/>
    <c:param name="adultos" value="${adultos}"/><c:param name="ninos" value="${ninos}"/><c:param name="mayores" value="${mayores}"/>
</c:url>
<a class="btn btn-link px-0 mb-2" href="${urlAtras}"><i class="bi bi-arrow-left"></i> Cambiar asientos</a>

<div class="card shadow-sm">
    <div class="card-body">
        <h1 class="h4">Datos de los pasajeros</h1>
        <p class="text-muted">
            <strong><c:out value="${corrida.ruta.nombre}"/></strong> · ${corrida.fechaTexto} · ${corrida.horaTexto}
            · Precio base <fmt:formatNumber value="${corrida.ruta.precioBase}" pattern="$#,##0.00"/>
        </p>

        <form action="${ctx}/boleto" method="post">
            <input type="hidden" name="action" value="comprar">
            <input type="hidden" name="idCorrida" value="${corrida.id}">
            <input type="hidden" name="adultos" value="${adultos}">
            <input type="hidden" name="ninos" value="${ninos}">
            <input type="hidden" name="mayores" value="${mayores}">
            <c:forEach items="${filas}" var="f" varStatus="st">
                <input type="hidden" name="asientos" value="${f.asiento}">
                <fieldset class="border rounded p-3 mb-3">
                    <legend class="float-none w-auto px-2 fs-6 fw-bold text-primary">Asiento ${f.asiento}</legend>
                    <div class="row g-3">
                        <div class="col-md-5">
                            <label class="form-label fw-semibold">Nombre completo</label>
                            <input type="text" name="nombre_${st.index}" class="form-control" value="<c:out value='${f.nombre}'/>" required minlength="3" maxlength="80">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-semibold">Edad</label>
                            <input type="number" name="edad_${st.index}" class="form-control" value="<c:out value='${f.edad}'/>" min="0" max="120" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Tipo de pasajero</label>
                            <select name="tipo_${st.index}" class="form-select tipo">
                                <c:forEach items="${tipos}" var="t">
                                    <option value="${t.nombre}" data-desc="${t.porcentaje}" ${f.tipo == t.nombre ? 'selected' : ''}>
                                        ${t.etiqueta}<c:if test="${t.porcentaje > 0}"> (−${t.porcentaje}%)</c:if>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </fieldset>
            </c:forEach>

            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 border-top pt-3">
                <span class="fs-5">Total a pagar: <strong id="total">—</strong></span>
                <button class="btn btn-success btn-lg" type="submit"><i class="bi bi-check2-circle me-1"></i>Confirmar compra</button>
            </div>
        </form>
    </div>
</div>

<script>
    (function () {
        var base = ${corrida.ruta.precioBase};
        var selects = document.querySelectorAll('select.tipo');
        function calcular() {
            var suma = 0;
            selects.forEach(function (s) {
                var d = parseInt(s.options[s.selectedIndex].dataset.desc, 10) || 0;
                suma += Math.round(base * (100 - d)) / 100;
            });
            document.getElementById('total').textContent =
                '$' + suma.toLocaleString('es-MX', {minimumFractionDigits: 2, maximumFractionDigits: 2});
        }
        selects.forEach(function (s) { s.addEventListener('change', calcular); });
        calcular();
    })();
</script>
<jsp:include page="/layout/footer.jsp"/>
