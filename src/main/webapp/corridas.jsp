<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/layout/header.jsp">
    <jsp:param name="titulo" value="Administración"/>
    <jsp:param name="activo" value="admin"/>
</jsp:include>

<div class="card shadow-sm">
    <div class="card-body">
        <h1 class="h4 mb-3">Administración</h1>
        <jsp:include page="/layout/adminTabs.jsp"><jsp:param name="seccion" value="corridas"/></jsp:include>

        <h2 class="h5">Programar corrida</h2>
        <form action="${ctx}/corrida" method="post" class="row g-3 align-items-end mb-4">
            <input type="hidden" name="action" value="programar">
            <div class="col-md-3"><label class="form-label fw-semibold">Ruta</label>
                <select name="ruta" class="form-select" required>
                    <c:forEach items="${rutas}" var="r"><option value="${r.idRuta}"><c:out value="${r.nombre}"/></option></c:forEach>
                </select></div>
            <div class="col-md-3"><label class="form-label fw-semibold">Autobús</label>
                <select name="autobus" class="form-select" required>
                    <c:forEach items="${autobuses}" var="a"><option value="${a.numeroEconomico}"><c:out value="${a.numeroEconomico}"/> (${a.capacidad})</option></c:forEach>
                </select></div>
            <div class="col-md-2"><label class="form-label fw-semibold">Fecha</label>
                <input type="date" name="fecha" class="form-control" min="${hoy}" value="${hoy}" required></div>
            <div class="col-md-2"><label class="form-label fw-semibold">Hora</label>
                <input type="time" name="hora" class="form-control" required></div>
            <div class="col-md-2 d-grid"><button class="btn btn-primary" type="submit">Programar</button></div>
        </form>

        <h2 class="h5">Próximas corridas <small class="text-muted">(primeras 60)</small></h2>
        <div class="table-responsive">
            <table class="table align-middle">
                <thead class="table-light"><tr><th>ID</th><th>Salida</th><th>Ruta</th><th>Autobús</th><th>Ocupación</th><th>Estado</th><th></th></tr></thead>
                <tbody>
                <c:forEach items="${corridas}" var="c">
                    <tr>
                        <td>${c.id}</td>
                        <td>${c.fechaTexto} ${c.horaTexto}</td>
                        <td><c:out value="${c.ruta.nombre}"/></td>
                        <td>${c.autobus.numeroEconomico}</td>
                        <td>${c.ocupados}/${c.autobus.capacidad}</td>
                        <td><span class="badge ${c.estado == 'Disponible' ? 'bg-success' : 'bg-warning text-dark'}">${c.estado}</span></td>
                        <td class="text-end">
                            <form action="${ctx}/corrida" method="post" class="d-inline" onsubmit="return confirm('¿Cancelar esta corrida?');">
                                <input type="hidden" name="action" value="cancelar">
                                <input type="hidden" name="id" value="${c.id}">
                                <button class="btn btn-sm btn-outline-danger" type="submit">Cancelar</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/layout/footer.jsp"/>
