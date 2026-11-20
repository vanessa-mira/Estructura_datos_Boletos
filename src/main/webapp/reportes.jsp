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
        <jsp:include page="/layout/adminTabs.jsp"><jsp:param name="seccion" value="reportes"/></jsp:include>

        <div class="row g-3 mb-4 text-center">
            <div class="col-md-4"><div class="bg-light rounded p-3"><strong class="fs-4">${reporte.boletosVigentes}</strong><br><small class="text-muted">boletos vigentes</small></div></div>
            <div class="col-md-4"><div class="bg-light rounded p-3"><strong class="fs-4">${reporte.boletosCancelados}</strong><br><small class="text-muted">cancelados</small></div></div>
            <div class="col-md-4"><div class="bg-light rounded p-3"><strong class="fs-4"><fmt:formatNumber value="${reporte.ingresos}" pattern="$#,##0.00"/></strong><br><small class="text-muted">ingresos (netos de reembolsos)</small></div></div>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <h3 class="h6">Boletos por día de venta</h3>
                <table class="table table-sm"><tbody>
                <c:forEach items="${reporte.boletosPorDia}" var="e"><tr><td>${e.key}</td><td class="text-end">${e.value}</td></tr></c:forEach>
                </tbody></table>
            </div>
            <div class="col-md-4">
                <h3 class="h6">Boletos por ruta</h3>
                <table class="table table-sm"><tbody>
                <c:forEach items="${reporte.boletosPorRuta}" var="e"><tr><td><c:out value="${e.key}"/></td><td class="text-end">${e.value}</td></tr></c:forEach>
                </tbody></table>
            </div>
            <div class="col-md-4">
                <h3 class="h6">Boletos por vendedor</h3>
                <table class="table table-sm"><tbody>
                <c:forEach items="${reporte.boletosPorVendedor}" var="e"><tr><td><c:out value="${e.key}"/></td><td class="text-end">${e.value}</td></tr></c:forEach>
                </tbody></table>
            </div>
        </div>

        <h3 class="h6 mt-4">Ocupación de las próximas corridas</h3>
        <div class="table-responsive">
            <table class="table align-middle">
                <thead class="table-light"><tr><th>Salida</th><th>Ruta</th><th>Ocupación</th><th>%</th></tr></thead>
                <tbody>
                <c:forEach items="${corridas}" var="c">
                    <tr>
                        <td>${c.fechaTexto} ${c.horaTexto}</td>
                        <td><c:out value="${c.ruta.nombre}"/></td>
                        <td style="min-width:140px"><div class="progress barra-ocupacion"><div class="progress-bar" style="width:${c.porcentajeOcupacion}%"></div></div></td>
                        <td>${c.porcentajeOcupacion}%</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/layout/footer.jsp"/>
