package com.example.boletos.DB;

import com.example.boletos.estructuras.ListaSimple;
import com.example.boletos.model.models.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

/**
 * "Base de datos" en memoria (reemplaza a OracleConnectApp del proyecto de eventos).
 * No usa SQL: cada "tabla" es una estructura de datos propia (ListaSimple), y dentro
 * de cada Corrida viven la MATRIZ de asientos (MapaAsientos) y la COLA de espera.
 * Es un singleton: todos los DAO comparten la misma instancia.
 */
public final class BaseDatos {
    /** Candado para que dos peticiones no modifiquen los datos al mismo tiempo. */
    public static final Object LOCK = new Object();

    private static final BaseDatos INSTANCIA = new BaseDatos();

    public static BaseDatos getInstance() {
        return INSTANCIA;
    }

    public final ListaSimple<Ruta> rutas = new ListaSimple<>();
    public final ListaSimple<Autobus> autobuses = new ListaSimple<>();
    public final ListaSimple<Corrida> corridas = new ListaSimple<>();
    public final ListaSimple<Boleto> boletos = new ListaSimple<>();
    public final ListaSimple<Usuario> usuarios = new ListaSimple<>();

    private int sigRuta = 1;
    private int sigCorrida = 1;
    private int sigUsuario = 1;
    private int sigFolio = 1;

    private BaseDatos() {
        cargarDatosDeEjemplo();
    }

    public int siguienteIdRuta() { return sigRuta++; }
    public int siguienteIdCorrida() { return sigCorrida++; }
    public int siguienteIdUsuario() { return sigUsuario++; }

    /** Ocupa el asiento en la matriz, genera el folio y guarda el boleto en la lista. */
    public Boleto nuevoBoleto(Corrida corrida, int asiento, Pasajero pasajero, String vendedor) {
        String folio = String.format("BUS-%06d", sigFolio++);
        corrida.getMapa().ocupar(asiento, folio);
        Boleto boleto = new Boleto(folio, corrida, asiento, pasajero,
                corrida.getRuta().precioPara(pasajero.getTipo()), vendedor);
        boletos.agregar(boleto);
        return boleto;
    }

    // ------------------------------------------------------------------
    // Datos de ejemplo para la demostracion
    // ------------------------------------------------------------------

    private void cargarDatosDeEjemplo() {
        usuarios.agregar(new Usuario(sigUsuario++, "Administrador General", "admin", "admin123", Rol.ADMINISTRADOR));
        usuarios.agregar(new Usuario(sigUsuario++, "Taquillero Demo", "taquilla", "taquilla123", Rol.VENDEDOR));

        rutas.agregar(new Ruta(sigRuta++, "Cuernavaca", "Ciudad de México", "1 h 30 min", 180));
        rutas.agregar(new Ruta(sigRuta++, "Ciudad de México", "Cuernavaca", "1 h 30 min", 180));
        rutas.agregar(new Ruta(sigRuta++, "Cuernavaca", "Acapulco", "4 h 30 min", 520));
        rutas.agregar(new Ruta(sigRuta++, "Cuernavaca", "Puebla", "3 h 15 min", 290));
        rutas.agregar(new Ruta(sigRuta++, "Cuautla", "Ciudad de México", "2 h", 210));
        rutas.agregar(new Ruta(sigRuta++, "Cuernavaca", "Cuautla", "1 h", 90));

        autobuses.agregar(new Autobus("A-101", "MOR-123-A", 40));
        autobuses.agregar(new Autobus("A-102", "MOR-456-B", 40));
        autobuses.agregar(new Autobus("A-103", "MOR-789-C", 36));
        // Microbus pequeno: se llena rapido, sirve para demostrar la lista de espera
        autobuses.agregar(new Autobus("M-201", "MOR-321-D", 8));

        LocalTime[] horas = {LocalTime.of(7, 0), LocalTime.of(10, 30), LocalTime.of(15, 0), LocalTime.of(20, 0)};
        LocalDate hoy = LocalDate.now();
        List<Ruta> listaRutas = rutas.toList();
        List<Autobus> listaBuses = autobuses.toList();
        for (int dia = 0; dia < 7; dia++) {
            for (int r = 0; r < listaRutas.size(); r++) {
                Ruta ruta = listaRutas.get(r);
                boolean esCuautla = ruta.getDestino().equals("Cuautla");
                for (int h = 0; h < horas.length; h++) {
                    if (esCuautla && h > 1) continue; // el microbus solo hace 2 salidas
                    Autobus bus = esCuautla ? listaBuses.get(3) : listaBuses.get((r + h) % 3);
                    corridas.agregar(new Corrida(sigCorrida++, ruta, bus, hoy.plusDays(dia),
                            esCuautla ? horas[h].plusHours(2) : horas[h]));
                }
            }
        }

        String[] nombres = {"Juan Pérez López", "María Hernández Ruiz", "Carlos Sánchez Díaz",
                "Ana Martínez Cruz", "Luis Ramírez Torres", "Sofía García Flores", "Miguel Ángel Reyes",
                "Laura Jiménez Vega", "Pedro Morales Silva", "Daniela Ortiz Rojas"};
        Random azar = new Random(42);
        int n = 0;
        for (Corrida c : corridas) {
            if (!c.isVendible() || c.getRuta().getDestino().equals("Cuautla")) continue;
            int cuantos = azar.nextInt(Math.max(2, c.getMapa().getCapacidad() / 2));
            for (int i = 0; i < cuantos; i++) {
                int asiento = 1 + azar.nextInt(c.getMapa().getCapacidad());
                if (!c.getMapa().estaLibre(asiento)) continue;
                nuevoBoleto(c, asiento,
                        new Pasajero(nombres[n++ % nombres.length], 18 + azar.nextInt(50), TipoPasajero.GENERAL), "taquilla");
            }
        }

        // Primera corrida vendible del microbus (Cuernavaca -> Cuautla) totalmente llena:
        // sirve para demostrar la lista de espera (cola).
        Corrida llena = corridas.buscar(c -> c.getRuta().getDestino().equals("Cuautla") && c.isVendible());
        for (int asiento = 1; asiento <= llena.getMapa().getCapacidad(); asiento++) {
            nuevoBoleto(llena, asiento,
                    new Pasajero(nombres[(n++) % nombres.length], 25 + asiento, TipoPasajero.GENERAL), "taquilla");
        }
    }
}
