package com.trabalho.model;

import com.trabalho.service.DadosService;
import java.util.List;

/**
 * Responsavel por guardar os dados pessoais e institucionais de todas
 * as pessoas envolvidas nas pesquisas (docentes, discentes ou tecnicos).
 * Conforme Modelo de Classes (Secao 8 e 9.4).
 */
public class Participante {
    private String cpfColaborador; // PK
    private String nome;
    private String email;          // Email institucional
    private String tipo;           // Docente, Discente, Tecnico-Administrativo

    public Participante() {
    }

    public Participante(String cpfColaborador, String nome, String email, String tipo) {
        this.cpfColaborador = cpfColaborador;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
    }

    /**
     * Registra um novo participante na base de dados do sistema.
     */
    public boolean cadastrarParticipante() {
        return DadosService.getInstance().salvarParticipante(this);
    }

    /**
     * Altera ou corrige as informacoes pessoais e de contato.
     */
    public void atualizarDadosParticipante(String nome, String email, String tipo) {
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
    }

    /**
     * Retorna a lista de projetos nos quais a pessoa ja atuou ou atua.
     */
    public List<HistoricoProj> consultarHistoricoParticipante() {
        return DadosService.getInstance().consultarHistoricoPorParticipante(this.cpfColaborador);
    }

    public String getCpfColaborador() {
        return cpfColaborador;
    }

    public void setCpfColaborador(String cpfColaborador) {
        this.cpfColaborador = cpfColaborador;
    }

    // Alias para compatibilidade com TableView ou getCpf
    public String getCpf() {
        return cpfColaborador;
    }

    public void setCpf(String cpf) {
        this.cpfColaborador = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return nome + " (" + tipo + " - " + cpfColaborador + ")";
    }
}
