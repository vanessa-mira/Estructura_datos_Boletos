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
        <jsp:include page="/layout/adminTabs.jsp"><jsp:param name="seccion" value="autobuses"/></jsp:include>

        <h2 class="h5">Registrar autobús</h2>
        <form action="${ctx}/autobus" method="post" class="row g-3 align-items-end mb-4">
            <input type="hidden" name="action" value="guardar">
            <div class="col-md-4"><label class="form-label fw-semibold">Número económico</label>
                <input type="text" name="numero" class="form-control" required placeholder="A-104"></div>
            <div class="col-md-4"><label class="form-label fw-semibold">Placas</label>
                <input type="text" name="placas" class="form-control" required placeholder="MOR-000-X"></div>
            <div class="col-md-2"><label class="form-label fw-semibold">Capacidad</label>
                <input type="number" name="capacidad" class="form-control" min="4" max="60" value="40" required></div>
            <div class="col-md-2 d-grid"><button class="btn btn-primary" type="submit">Registrar</button></div>
        </form>

        <h2 class="h5">Flota</h2>
        <div class="table-responsive">
            <table class="table align-middle">
                <thead class="table-light"><tr><th>Número económico</th><th>Placas</th><th>Capacidad</th><th></th></tr></thead>
                <tbody>
                <c:forEach items="${autobuses}" var="a">
                    <tr>
                        <td><c:out value="${a.numeroEconomico}"/></td>
                        <td><c:out value="${a.placas}"/></td>
                        <td>${a.capacidad} asientos</td>
                        <td class="text-end">
                            <form action="${ctx}/autobus" method="post" class="d-inline" onsubmit="return confirm('¿Eliminar este autobús?');">
                                <input type="hidden" name="action" value="eliminar">
                                <input type="hidden" name="numero" value="<c:out value='${a.numeroEconomico}'/>">
                                <button class="btn btn-sm btn-outline-danger" type="submit">Eliminar</button>
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
