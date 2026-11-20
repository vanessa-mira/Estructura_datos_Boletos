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
        <jsp:include page="/layout/adminTabs.jsp"><jsp:param name="seccion" value="rutas"/></jsp:include>

        <h2 class="h5">${empty editar ? 'Agregar ruta' : 'Editar ruta'}</h2>
        <form action="${ctx}/ruta" method="post" class="row g-3 align-items-end mb-4">
            <input type="hidden" name="action" value="guardar">
            <input type="hidden" name="id" value="${empty editar ? '' : editar.idRuta}">
            <div class="col-md-3"><label class="form-label fw-semibold">Origen</label>
                <input type="text" name="origen" class="form-control" required value="<c:out value='${editar.origen}'/>"></div>
            <div class="col-md-3"><label class="form-label fw-semibold">Destino</label>
                <input type="text" name="destino" class="form-control" required value="<c:out value='${editar.destino}'/>"></div>
            <div class="col-md-2"><label class="form-label fw-semibold">Duración</label>
                <input type="text" name="duracion" class="form-control" required placeholder="2 h 30 min" value="<c:out value='${editar.duracion}'/>"></div>
            <div class="col-md-2"><label class="form-label fw-semibold">Precio base</label>
                <input type="number" name="precio" class="form-control" step="0.01" min="1" required value="${editar.precioBase}"></div>
            <div class="col-md-2 d-flex gap-2">
                <button class="btn btn-primary" type="submit">${empty editar ? 'Agregar' : 'Guardar'}</button>
                <c:if test="${not empty editar}"><a class="btn btn-outline-secondary" href="${ctx}/ruta">Cancelar</a></c:if>
            </div>
        </form>

        <h2 class="h5">Rutas registradas</h2>
        <div class="table-responsive">
            <table class="table align-middle">
                <thead class="table-light"><tr><th>ID</th><th>Origen</th><th>Destino</th><th>Duración</th><th>Precio base</th><th></th></tr></thead>
                <tbody>
                <c:forEach items="${rutas}" var="r">
                    <tr>
                        <td>${r.idRuta}</td>
                        <td><c:out value="${r.origen}"/></td>
                        <td><c:out value="${r.destino}"/></td>
                        <td><c:out value="${r.duracion}"/></td>
                        <td><fmt:formatNumber value="${r.precioBase}" pattern="$#,##0.00"/></td>
                        <td class="text-end">
                            <a class="btn btn-sm btn-outline-primary" href="${ctx}/ruta?action=editar&id=${r.idRuta}">Editar</a>
                            <form action="${ctx}/ruta" method="post" class="d-inline" onsubmit="return confirm('¿Eliminar esta ruta?');">
                                <input type="hidden" name="action" value="eliminar">
                                <input type="hidden" name="id" value="${r.idRuta}">
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
