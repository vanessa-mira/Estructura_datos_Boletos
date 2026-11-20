<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/layout/header.jsp">
    <jsp:param name="titulo" value="Iniciar sesión"/>
    <jsp:param name="activo" value="login"/>
</jsp:include>

<div class="row justify-content-center">
    <div class="col-md-6 col-lg-4">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h4 mb-1">Acceso del personal</h1>
                <p class="text-muted small">Solo para vendedores (taquilla) y administradores. Los clientes no necesitan cuenta.</p>
                <form action="${ctx}/login" method="post">
                    <input type="hidden" name="destino" value="<c:out value='${destino}'/>">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Usuario</label>
                        <input type="text" name="usuario" class="form-control" required autofocus>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Contraseña</label>
                        <input type="password" name="contra" class="form-control" required>
                    </div>
                    <button class="btn btn-warning w-100 fw-bold" type="submit">Entrar</button>
                </form>
                <div class="bg-light rounded p-3 mt-4 small">
                    <strong>Cuentas de demostración</strong><br>
                    Vendedor: <code>taquilla</code> / <code>taquilla123</code><br>
                    Administrador: <code>admin</code> / <code>admin123</code>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/layout/footer.jsp"/>
