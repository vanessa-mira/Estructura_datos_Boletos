package com.example.boletos.model.dao;

import com.example.boletos.DB.BaseDatos;
import com.example.boletos.model.models.Boleto;

import java.util.Map;
import java.util.TreeMap;

/** Funcion 10: reportes de ventas e ingresos (solo lectura). */
public class ReporteDao {
    public static class Reporte {
        public Map<String, Integer> boletosPorDia = new TreeMap<>();
        public Map<String, Integer> boletosPorRuta = new TreeMap<>();
        public Map<String, Integer> boletosPorVendedor = new TreeMap<>();
        public double ingresos;
        public int boletosVigentes;
        public int boletosCancelados;

        public Map<String, Integer> getBoletosPorDia() { return boletosPorDia; }
        public Map<String, Integer> getBoletosPorRuta() { return boletosPorRuta; }
        public Map<String, Integer> getBoletosPorVendedor() { return boletosPorVendedor; }
        public double getIngresos() { return ingresos; }
        public int getBoletosVigentes() { return boletosVigentes; }
        public int getBoletosCancelados() { return boletosCancelados; }
    }

    public Reporte generar() {
        BaseDatos db = BaseDatos.getInstance();
        Reporte r = new Reporte();
        synchronized (BaseDatos.LOCK) {
            for (Boleto b : db.boletos) {
                if (b.isVigente()) {
                    r.boletosVigentes++;
                    r.ingresos += b.getPrecioPagado();
                    r.boletosPorDia.merge(b.getFechaVentaTexto().substring(0, 10), 1, Integer::sum);
                    r.boletosPorRuta.merge(b.getCorrida().getRuta().getNombre(), 1, Integer::sum);
                    r.boletosPorVendedor.merge(b.getVendedor(), 1, Integer::sum);
                } else {
                    r.boletosCancelados++;
                    r.ingresos += b.getPrecioPagado() - b.getReembolso();
                }
            }
        }
        r.ingresos = Math.round(r.ingresos * 100.0) / 100.0;
        return r;
    }
}
