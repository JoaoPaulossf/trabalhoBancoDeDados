package com.trabalho.model;

import com.trabalho.service.DadosService;
import java.time.LocalDate;

/**
 * Classe associativa que gerencia o vinculo entre Participantes e Projetos,
 * registrando a funcao exercida e o periodo de atuacao para garantir a rastreabilidade.
 * Conforme Modelo de Classes (Secao 8 e 9.5).
 */
public class HistoricoProj {
    private int idProjeto;          // FK / PK Composta
    private String cpfColaborador;  // FK / PK Composta
    private String funcao;          // Papel/cargo desempenhado
    private LocalDate dataInicio;   // Inicio do vinculo
    private LocalDate dataFim;      // Encerramento do vinculo (pode ser null se ativo)

    public HistoricoProj() {
    }

    public HistoricoProj(int idProjeto, String cpfColaborador, String funcao, LocalDate dataInicio, LocalDate dataFim) {
        this.idProjeto = idProjeto;
        this.cpfColaborador = cpfColaborador;
        this.funcao = funcao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    /**
     * Cria o vinculo ativo entre o participante e o projeto com a respectiva funcao.
     */
    public boolean vincularParticipante() {
        return DadosService.getInstance().salvarVinculo(this);
    }

    /**
     * Permite corrigir a funcao ou ajustar as datas do vinculo.
     */
    public void atualizarVinculo(String novaFuncao, LocalDate novaDataInicio, LocalDate novaDataFim) {
        this.funcao = novaFuncao;
        this.dataInicio = novaDataInicio;
        this.dataFim = novaDataFim;
    }

    /**
     * Realiza a exclusao logica, preenchendo a data_fim para manter a rastreabilidade da pesquisa (RN-09).
     */
    public void encerrarVinculo(LocalDate dataFim) {
        this.dataFim = dataFim != null ? dataFim : LocalDate.now();
    }

    public int getIdProjeto() {
        return idProjeto;
    }

    public void setIdProjeto(int idProjeto) {
        this.idProjeto = idProjeto;
    }

    public String getCpfColaborador() {
        return cpfColaborador;
    }

    public void setCpfColaborador(String cpfColaborador) {
        this.cpfColaborador = cpfColaborador;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
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

    // Metodos auxiliares para visualizacao conveniente em tabelas
    public String getNomeParticipante() {
        Participante p = DadosService.getInstance().buscarParticipantePorCpf(cpfColaborador);
        return p != null ? p.getNome() : cpfColaborador;
    }

    public String getEmailParticipante() {
        Participante p = DadosService.getInstance().buscarParticipantePorCpf(cpfColaborador);
        return p != null ? p.getEmail() : "";
    }

    public String getTipoParticipante() {
        Participante p = DadosService.getInstance().buscarParticipantePorCpf(cpfColaborador);
        return p != null ? p.getTipo() : "";
    }

    public String getTituloProjeto() {
        Projeto proj = DadosService.getInstance().buscarProjetoPorId(idProjeto);
        return proj != null ? proj.getTitulo() : "Projeto #" + idProjeto;
    }

    public String getStatusVinculo() {
        return (dataFim == null || !dataFim.isBefore(LocalDate.now())) ? "Ativo" : "Encerrado";
    }

    public boolean isAtivo() {
        return dataFim == null;
    }
}
