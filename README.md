# Sistema de Gestión y Venta de Boletos de Autobús (Jakarta EE)

Misma arquitectura que el proyecto **gestorEventos** (model/models + model/dao con `Dao<T,K>`, un servlet por
entidad con parámetro `action`, filtro de autenticación, JSP en la raíz con `layout/` y Bootstrap 5).
La diferencia: en vez de Oracle, los DAO trabajan sobre **estructuras de datos propias en memoria**.

## 1. Correrlo en IntelliJ y ver las pantallas

Necesitas **JDK 17+**.

**Opción A (sin instalar Tomcat): Maven + Cargo**
1. `File > Open...` y elige la carpeta del proyecto (donde está `pom.xml`). Espera a que Maven descargue.
2. Arriba a la derecha aparece **"Ejecutar app (Cargo)"**; dale ▶. (Si no aparece: pestaña *Maven* > *Execute Maven Goal* > `clean package cargo:run`.)
3. La primera vez baja Tomcat 10.1 solo (necesita internet). Abre **http://localhost:8080/**.

**Opción B: Tomcat 10.1 en IntelliJ Ultimate**
`Run > Edit Configurations > + > Tomcat Server > Local` → Deployment > `+` > Artifact > `boletos-autobus:war exploded`, contexto `/`.
(Tomcat debe ser 10.x; el 9 usa `javax` y no sirve.)

## 2. Estructura (igual que gestorEventos)

```
src/main/java/com/example/boletos/
├── DB/BaseDatos.java            <- reemplaza a OracleConnectApp: "tablas" = ListaSimple + datos de ejemplo
├── estructuras/                 Nodo, ListaSimple, Cola, MapaAsientos   (LAS ESTRUCTURAS DE DATOS)
├── model/
│   ├── models/                  Ruta, Autobus, Corrida, Boleto, Pasajero, Usuario, Rol, TipoPasajero, EstadoBoleto
│   └── dao/                     Dao<T,K> + RutaDao, AutobusDao, CorridaDao, BoletoDao,
│                                ListaEsperaDao, UsuarioDao, ReporteDao
├── controller/                  Un servlet por entidad (con ?action=...)
│   ├── CorridaServlet           /corrida     inicio | buscar | asientos | gestion(admin) ; POST programar|cancelar
│   ├── BoletoServlet            /boleto      consultar | comprar | modificar ; POST comprar|cancelar|modificar
│   ├── ListaEsperaServlet       /espera      POST (encola en la cola de espera)
│   ├── TaquillaServlet          /taquilla    panel del vendedor
│   ├── RutaServlet              /ruta        CRUD (admin)
│   ├── AutobusServlet           /autobus     CRUD (admin)
│   ├── UsuarioServlet           /usuario     CRUD (admin)
│   ├── ReporteServlet           /reporte     reportes (admin)
│   ├── LoginServlet / LogoutServlet
│   └── filters/                 FiltroAutenticacion (roles), FiltroCodificacion (UTF-8)
└── utils/                       Util (mensajes flash, conversiones), AsientoVista, FilaPasajero

src/main/webapp/                 (JSP en la raíz, como en gestorEventos)
├── index.jsp                    redirige a /corrida
├── inicio.jsp  resultados.jsp  asientos.jsp  comprar.jsp  consultarBoleto.jsp  modificar.jsp
├── login.jsp   taquilla.jsp
├── rutas.jsp  autobuses.jsp  corridas.jsp  usuarios.jsp  reportes.jsp
├── layout/                      header.jsp, footer.jsp, adminTabs.jsp, mapa.jsp
└── css/mapa.css                 estilos del mapa del camión (el resto es Bootstrap)
```

Flujo: **navegador → Servlet → DAO → BaseDatos (estructuras) → JSP**.
Los JSP no se abren directo: el filtro redirige al inicio si alguien escribe `/rutas.jsp` en el navegador.

## 3. Estructuras de datos

| Estructura | Clase | Qué guarda | Dónde se usa |
|---|---|---|---|
| Arreglo bidimensional (matriz) | `MapaAsientos` | Folio del boleto de cada asiento (`null` = libre) | Mapa del camión, evita vender un asiento dos veces |
| Lista simplemente enlazada | `ListaSimple<T>` | Rutas, autobuses, corridas, boletos, usuarios | Buscar por folio o nombre, filtrar corridas, altas y bajas |
| Cola (FIFO) | `Cola<T>` | Pasajeros en lista de espera de una corrida | Al cancelar un boleto, el asiento va al primero de la cola |

## 4. Cuentas de prueba
| Rol | Usuario | Contraseña |
|---|---|---|
| Vendedor | `taquilla` | `taquilla123` |
| Administrador | `admin` | `admin123` |

El cliente no necesita cuenta.

## 5. Guion para el video
1. **Navegación (4+ pantallas):** Inicio → Resultados (*Cuernavaca → Ciudad de México*) → Mapa de asientos → Datos del pasajero → Boleto → menú *Mis boletos*.
2. **Matriz:** compra el asiento 5; vuelve al mapa: aparece ocupado.
3. **Cola:** busca *Cuernavaca → Cuautla* (la primera corrida viene llena) → *Lista de espera* → anótate. Entra como `taquilla`, mira la cola en *Taquilla*, cancela un boleto de esa corrida (*Mis boletos*) y comprueba que el asiento se asigna al primero de la fila.
4. **Explicación:** qué guarda la estructura, cómo interviene y por qué se eligió.

## 6. Reglas
- Descuentos: Estudiante 20 %, Niño 50 %, Adulto mayor 50 %.
- Reembolso: 100 % si faltan 24 h o más, 50 % si faltan 2 h o más, 0 % si falta menos.
- Compra "todo o nada" (si un asiento se ocupó mientras se llenaba el formulario, no se vende ninguno).
- Los datos viven en memoria: al reiniciar Tomcat vuelven al estado inicial. Contraseñas en texto plano (solo para fines escolares).
- Pendiente: el campo "Regreso (opcional)" del boceto (solo viaje sencillo).
