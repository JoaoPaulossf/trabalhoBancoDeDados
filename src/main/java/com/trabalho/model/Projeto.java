package com.trabalho.model;

import com.trabalho.service.DadosService;
import java.time.LocalDate;

/**
 * Classe central do sistema. Armazena as informacoes principais de cada pesquisa,
 * como titulo, vigencia, situacao e quem e o docente responsavel.
 * Conforme Modelo de Classes (Secao 8 e 9.3).
 */
public class Projeto {
    private int idProjeto;          // PK gerada automaticamente (RN-01)
    private String idCoordenador;   // FK para o Coordenador responsavel (RN-02)
    private String titulo;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataTermino;  // Pode ser nula/em branco (RN-03)
    private String situacao;        // "Em andamento", "Concluido", "Cancelado"

    public Projeto() {
    }

    public Projeto(int idProjeto, String idCoordenador, String titulo, String descricao, LocalDate dataInicio, LocalDate dataTermino, String situacao) {
        this.idProjeto = idProjeto;
        this.idCoordenador = idCoordenador;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
        this.situacao = situacao;
    }

    /**
     * Registra um novo projeto no sistema e gera seu ID.
     */
    public boolean cadastrarProjeto() {
        return DadosService.getInstance().salvarProjeto(this);
    }

    /**
     * Edita informacoes como titulo, descricao, datas e coordenador.
     */
    public void atualizarDadosProjeto(String titulo, String descricao, LocalDate dataInicio, LocalDate dataTermino, String situacao, String idCoordenador) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
        this.situacao = situacao;
        this.idCoordenador = idCoordenador;
    }

    /**
     * Atualiza a situacao para "Concluido" ou "Cancelado" e define a data final.
     */
    public void encerrarProjeto(String novaSituacao, LocalDate dataFim) {
        this.situacao = (novaSituacao != null && !novaSituacao.isEmpty()) ? novaSituacao : "Concluído";
        this.dataTermino = (dataFim != null) ? dataFim : LocalDate.now();
    }

    /**
     * Retorna todos os dados detalhados do projeto para exibicao.
     */
    public String consultarDetalhesProjeto() {
        return String.format("Projeto [%d] - %s | Situacao: %s | Periodo: %s a %s | Coordenador: %s",
                idProjeto,
                titulo,
                situacao,
                dataInicio,
                (dataTermino != null ? dataTermino.toString() : "Em andamento"),
                getNomeCoordenador());
    }

    public int getIdProjeto() {
        return idProjeto;
    }

    public void setIdProjeto(int idProjeto) {
        this.idProjeto = idProjeto;
    }

    public String getIdCoordenador() {
        return idCoordenador;
    }

    public void setIdCoordenador(String idCoordenador) {
        this.idCoordenador = idCoordenador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(LocalDate dataTermino) {
        this.dataTermino = dataTermino;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    // Helper para TableView
    public String getNomeCoordenador() {
        return DadosService.getInstance().buscarNomeCoordenador(idCoordenador);
    }

    // Alias para compatibilidade anterior se chamado getCoordenador()
    public String getCoordenador() {
        return getNomeCoordenador();
    }

    public void setCoordenador(String coordenador) {
        this.idCoordenador = coordenador;
    }

    @Override
    public String toString() {
        return "[" + idProjeto + "] " + titulo;
    }
}
