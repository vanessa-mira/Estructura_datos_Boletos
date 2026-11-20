<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Dibuja la MATRIZ de asientos. Recibe en request: filas, tipoInput (checkbox|radio), nombreInput --%>
<div class="leyenda">
    <span><i class="muestra libre"></i> Libre</span>
    <span><i class="muestra seleccion"></i> Tu selección</span>
    <span><i class="muestra ocupado"></i> Ocupado</span>
    <c:if test="${tipoInput == 'radio'}"><span><i class="muestra actual"></i> Tu asiento actual</span></c:if>
</div>
<div class="bus">
    <div class="bus-frente"><i class="bi bi-bus-front"></i> Conductor</div>
    <c:forEach items="${filas}" var="fila">
        <div class="fila">
            <c:forEach items="${fila}" var="a" varStatus="st">
                <c:choose>
                    <c:when test="${not a.existe}"><span class="asiento vacio"></span></c:when>
                    <c:otherwise>
                        <label class="asiento ${a.ocupado ? 'ocupado' : ''} ${a.actual ? 'actual' : ''}">
                            <input type="${tipoInput}" name="${nombreInput}" value="${a.numero}"
                                   ${(a.ocupado or a.actual) ? 'disabled' : ''}>
                            <span>${a.numero}</span>
                        </label>
                    </c:otherwise>
                </c:choose>
                <c:if test="${st.index == 1}"><span class="pasillo"></span></c:if>
            </c:forEach>
        </div>
    </c:forEach>
</div>
