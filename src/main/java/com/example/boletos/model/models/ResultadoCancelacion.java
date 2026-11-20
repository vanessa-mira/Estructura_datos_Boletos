package com.example.boletos.model.models;

/** Resultado de cancelar: el boleto cancelado y, si habia lista de espera, el boleto nuevo. */
public class ResultadoCancelacion {
    private final Boleto cancelado;
    private final Boleto asignadoDeEspera;

    public ResultadoCancelacion(Boleto cancelado, Boleto asignadoDeEspera) {
        this.cancelado = cancelado;
        this.asignadoDeEspera = asignadoDeEspera;
    }

    public Boleto getCancelado() { return cancelado; }
    public Boleto getAsignadoDeEspera() { return asignadoDeEspera; }
}
