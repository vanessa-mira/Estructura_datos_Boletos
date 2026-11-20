<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/layout/header.jsp">
    <jsp:param name="titulo" value="Elige tu asiento"/>
    <jsp:param name="activo" value="inicio"/>
</jsp:include>

<a class="btn btn-link px-0 mb-2" href="javascript:history.back()"><i class="bi bi-arrow-left"></i> Volver a los resultados</a>
<div class="card shadow-sm">
    <div class="card-body">
        <h1 class="h4">Elige tu asiento</h1>
        <p class="text-muted">
            <strong><c:out value="${corrida.ruta.nombre}"/></strong> · ${corrida.fechaTexto} · ${corrida.horaTexto}
            · Autobús ${corrida.autobus.numeroEconomico}
        </p>
        <div class="row g-2 mb-3 text-center">
            <div class="col-6 col-md-3"><div class="bg-light rounded p-2"><strong class="fs-5">${corrida.libres}</strong><br><small class="text-muted">libres</small></div></div>
            <div class="col-6 col-md-3"><div class="bg-light rounded p-2"><strong class="fs-5">${corrida.ocupados}</strong><br><small class="text-muted">ocupados</small></div></div>
            <div class="col-6 col-md-3"><div class="bg-light rounded p-2"><strong class="fs-5">${corrida.porcentajeOcupacion}%</strong><br><small class="text-muted">ocupación</small></div></div>
            <div class="col-6 col-md-3"><div class="bg-light rounded p-2"><strong class="fs-5">${corrida.enEspera}</strong><br><small class="text-muted">en espera</small></div></div>
        </div>

        <c:choose>
            <c:when test="${corrida.llena}">
                <div class="alert alert-warning">
                    Esta corrida está <strong>llena</strong>. Puedes anotarte en la lista de espera: si alguien cancela,
                    el asiento se asigna al primero de la fila (cola).
                </div>
                <form action="${ctx}/espera" method="post">
                    <input type="hidden" name="idCorrida" value="${corrida.id}">
                    <div class="row g-3">
                        <div class="col-md-5">
                            <label class="form-label fw-semibold">Nombre completo</label>
                            <input type="text" name="nombre" class="form-control" required minlength="3" maxlength="80">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-semibold">Edad</label>
                            <input type="number" name="edad" class="form-control" min="0" max="120" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Tipo de pasajero</label>
                            <select name="tipo" class="form-select">
                                <option value="GENERAL">General</option>
                                <option value="ESTUDIANTE">Estudiante</option>
                                <option value="NINO">Niño</option>
                                <option value="ADULTO_MAYOR">Adulto mayor</option>
                            </select>
                        </div>
                    </div>
                    <button class="btn btn-primary mt-3" type="submit"><i class="bi bi-people me-1"></i>Anotarme en la lista de espera</button>
                </form>
            </c:when>
            <c:otherwise>
                <p>Selecciona <strong>${total}</strong> asiento(s).</p>
                <form id="formAsientos" action="${ctx}/boleto" method="get">
                    <input type="hidden" name="action" value="comprar">
                    <input type="hidden" name="idCorrida" value="${corrida.id}">
                    <input type="hidden" name="adultos" value="${adultos}">
                    <input type="hidden" name="ninos" value="${ninos}">
                    <input type="hidden" name="mayores" value="${mayores}">
                    <c:set var="tipoInput" value="checkbox" scope="request"/>
                    <c:set var="nombreInput" value="asientos" scope="request"/>
                    <jsp:include page="/layout/mapa.jsp"/>
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 border-top pt-3 mt-3">
                        <span id="contador" class="fw-semibold">0 de ${total} seleccionado(s)</span>
                        <button id="btnContinuar" class="btn btn-primary" type="submit" disabled>Continuar con la compra</button>
                    </div>
                </form>
                <script>
                    (function () {
                        var total = ${total};
                        var form = document.getElementById('formAsientos');
                        var cont = document.getElementById('contador');
                        var btn = document.getElementById('btnContinuar');
                        function marcados() { return form.querySelectorAll('input[name=asientos]:checked').length; }
                        function actualizar() {
                            cont.textContent = marcados() + ' de ' + total + ' seleccionado(s)';
                            btn.disabled = marcados() !== total;
                        }
                        form.querySelectorAll('input[name=asientos]').forEach(function (b) {
                            b.addEventListener('change', function () {
                                if (marcados() > total) { b.checked = false; }
                                actualizar();
                            });
                        });
                        actualizar();
                    })();
                </script>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<jsp:include page="/layout/footer.jsp"/>
