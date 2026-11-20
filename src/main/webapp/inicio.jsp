<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/layout/header.jsp">
    <jsp:param name="titulo" value="Compra tus boletos"/>
    <jsp:param name="activo" value="inicio"/>
</jsp:include>

<div class="text-center mb-4">
    <h1 class="display-6 fw-bold text-primary-emphasis">Compra tu boleto sin filas</h1>
    <p class="text-muted mb-0">Elige tu viaje, escoge tu asiento en el mapa del camión y listo.</p>
</div>

<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="card shadow-sm mb-4">
            <div class="card-body p-4">
                <h2 class="h5 mb-3"><i class="bi bi-geo-alt me-2"></i>Escoge tu destino</h2>
                <form action="${ctx}/corrida" method="get" autocomplete="off">
                    <input type="hidden" name="action" value="buscar">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">De</label>
                            <input type="text" name="origen" list="ciudades" class="form-control" placeholder="Ciudad de origen" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">A</label>
                            <input type="text" name="destino" list="ciudades" class="form-control" placeholder="Ciudad de destino" required>
                        </div>
                        <datalist id="ciudades">
                            <c:forEach items="${ciudades}" var="ciudad"><option value="${ciudad}"></option></c:forEach>
                        </datalist>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Salida</label>
                            <input type="date" name="fecha" class="form-control" value="${hoy}" min="${hoy}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Hora a partir de <small class="text-muted">(opcional)</small></label>
                            <input type="time" name="hora" class="form-control">
                        </div>
                    </div>

                    <ul class="list-group my-4">
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            <span>General <small class="text-muted">(12 a 59 años)</small></span>
                            <span class="stepper input-group w-auto">
                                <button type="button" class="btn btn-outline-secondary" data-d="-1">−</button>
                                <input type="number" name="adultos" value="1" min="0" max="9" class="form-control" readonly>
                                <button type="button" class="btn btn-outline-secondary" data-d="1">+</button>
                            </span>
                        </li>
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            <span>Niños <small class="text-muted">(hasta 12 años · 50%)</small></span>
                            <span class="stepper input-group w-auto">
                                <button type="button" class="btn btn-outline-secondary" data-d="-1">−</button>
                                <input type="number" name="ninos" value="0" min="0" max="9" class="form-control" readonly>
                                <button type="button" class="btn btn-outline-secondary" data-d="1">+</button>
                            </span>
                        </li>
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            <span>Tercera edad <small class="text-muted">(60+ · 50%)</small></span>
                            <span class="stepper input-group w-auto">
                                <button type="button" class="btn btn-outline-secondary" data-d="-1">−</button>
                                <input type="number" name="mayores" value="0" min="0" max="9" class="form-control" readonly>
                                <button type="button" class="btn btn-outline-secondary" data-d="1">+</button>
                            </span>
                        </li>
                    </ul>
                    <button class="btn btn-warning btn-lg w-100 fw-bold" type="submit"><i class="bi bi-search me-2"></i>Buscar</button>
                </form>
            </div>
        </div>

        <div class="card shadow-sm">
            <div class="card-body d-flex justify-content-between align-items-center flex-wrap gap-2">
                <div>
                    <h2 class="h6 mb-1">¿Ya compraste?</h2>
                    <small class="text-muted">Consulta, modifica o cancela tu boleto con tu folio o tu nombre.</small>
                </div>
                <a class="btn btn-outline-primary" href="${ctx}/boleto"><i class="bi bi-ticket-perforated me-1"></i>Consultar mi boleto</a>
            </div>
        </div>
    </div>
</div>

<script>
    document.querySelectorAll('.stepper').forEach(function (s) {
        var input = s.querySelector('input');
        s.querySelectorAll('button').forEach(function (b) {
            b.addEventListener('click', function () {
                var v = parseInt(input.value, 10) + parseInt(b.dataset.d, 10);
                input.value = Math.max(0, Math.min(9, v));
            });
        });
    });
</script>
<jsp:include page="/layout/footer.jsp"/>
