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
        <jsp:include page="/layout/adminTabs.jsp"><jsp:param name="seccion" value="usuarios"/></jsp:include>

        <h2 class="h5">Dar de alta usuario</h2>
        <form action="${ctx}/usuario" method="post" class="row g-3 align-items-end mb-4">
            <input type="hidden" name="action" value="crear">
            <div class="col-md-3"><label class="form-label fw-semibold">Nombre completo</label>
                <input type="text" name="nombre" class="form-control" required></div>
            <div class="col-md-3"><label class="form-label fw-semibold">Usuario</label>
                <input type="text" name="usuario" class="form-control" required></div>
            <div class="col-md-2"><label class="form-label fw-semibold">Contraseña</label>
                <input type="password" name="contra" class="form-control" required></div>
            <div class="col-md-2"><label class="form-label fw-semibold">Rol</label>
                <select name="rol" class="form-select"><option value="VENDEDOR">Vendedor</option><option value="ADMINISTRADOR">Administrador</option></select></div>
            <div class="col-md-2 d-grid"><button class="btn btn-primary" type="submit">Crear</button></div>
        </form>

        <h2 class="h5">Usuarios del sistema</h2>
        <div class="table-responsive">
            <table class="table align-middle">
                <thead class="table-light"><tr><th>ID</th><th>Nombre</th><th>Usuario</th><th>Rol</th><th></th></tr></thead>
                <tbody>
                <c:forEach items="${usuarios}" var="u">
                    <tr>
                        <td>${u.id}</td>
                        <td><c:out value="${u.nombre}"/></td>
                        <td><c:out value="${u.usuario}"/></td>
                        <td>${u.rol.etiqueta}</td>
                        <td class="text-end">
                            <c:if test="${u.id != sessionScope.usuario.id}">
                                <form action="${ctx}/usuario" method="post" class="d-inline" onsubmit="return confirm('¿Dar de baja a este usuario?');">
                                    <input type="hidden" name="action" value="eliminar">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <button class="btn btn-sm btn-outline-danger" type="submit">Dar de baja</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/layout/footer.jsp"/>
