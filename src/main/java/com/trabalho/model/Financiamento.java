package com.trabalho.model;

import com.trabalho.service.DadosService;
import java.time.LocalDate;

/**
 * Registrar e acompanhar o suporte financeiro dos projetos, armazenando dados sobre a agencia
 * fomentadora, o valor concedido e a validade do contrato.
 * Conforme Modelo de Classes (Secao 8 e 9.6).
 */
public class Financiamento {
    private int idProjeto;          // PK e FK exclusiva de um projeto (RN-07)
    private String agencia;         // Instituicao fomentadora
    private String tipoFomento;     // Modalidade do fomento
    private String valorFomento;    // Valor total concedido
    private LocalDate dataInicio;   // Vigencia inicio (RN-06: NOT NULL)
    private LocalDate dataFim;      // Vigencia fim (RN-06: NOT NULL)
    private String situacao;        // "Ativo" ou "Inativo" (RF-03, RN-09)

    public Financiamento() {
        this.situacao = "Ativo";
    }

    public Financiamento(int idProjeto, String agencia, String tipoFomento, String valorFomento, LocalDate dataInicio, LocalDate dataFim, String situacao) {
        this.idProjeto = idProjeto;
        this.agencia = agencia;
        this.tipoFomento = tipoFomento;
        this.valorFomento = valorFomento;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.situacao = situacao != null ? situacao : "Ativo";
    }

    /**
     * Registra os dados financeiros e vincula ao projeto.
     */
    public boolean cadastrarFinanciamento() {
        return DadosService.getInstance().salvarFinanciamento(this);
    }

    /**
     * Edita os valores, datas, agencia ou situacao do contrato.
     */
    public void atualizarFinanciamento(String agencia, String tipoFomento, String valorFomento, LocalDate dataInicio, LocalDate dataFim, String situacao) {
        this.agencia = agencia;
        this.tipoFomento = tipoFomento;
        this.valorFomento = valorFomento;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.situacao = situacao;
    }

    /**
     * Marca o contrato de financiamento como encerrado ou inativo (RN-09).
     */
    public void inativarFinanciamento() {
        this.situacao = "Inativo";
    }

    public int getIdProjeto() {
        return idProjeto;
    }

    public void setIdProjeto(int idProjeto) {
        this.idProjeto = idProjeto;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getTipoFomento() {
        return tipoFomento;
    }

    public void setTipoFomento(String tipoFomento) {
        this.tipoFomento = tipoFomento;
    }

    public String getValorFomento() {
        return valorFomento;
    }

    public void setValorFomento(String valorFomento) {
        this.valorFomento = valorFomento;
    }

    public String getValorTotal() {
        return valorFomento;
    }

    public void setValorTotal(String valorTotal) {
        this.valorFomento = valorTotal;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    // Helper para TableView
    public String getTituloProjeto() {
        Projeto proj = DadosService.getInstance().buscarProjetoPorId(idProjeto);
        return proj != null ? proj.getTitulo() : "Projeto #" + idProjeto;
    }

    public String getVigenciaFormatada() {
        if (dataInicio == null || dataFim == null) return "";
        return dataInicio + " até " + dataFim;
    }

    @Override
    public String toString() {
        return agencia + " - " + tipoFomento + " (" + situacao + ")";
    }
}
