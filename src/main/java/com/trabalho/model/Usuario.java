package com.trabalho.model;

import com.trabalho.service.DadosService;
import java.util.List;

/**
 * Classe base responsavel por armazenar as informacoes de acesso ao sistema
 * e definir o nivel de permissao (controle de acesso) de cada individuo.
 * Conforme Modelo de Classes (Secao 8 e 9.1).
 */
public class Usuario {
    protected String cpf;
    protected String nome;
    protected String emailInstitucional;
    protected String nivelAcesso; // "Coordenador de Projeto" ou "Gestor Administrativo"
    protected String senha;

    public Usuario() {
    }

    public Usuario(String cpf, String nome, String emailInstitucional, String nivelAcesso, String senha) {
        this.cpf = cpf;
        this.nome = nome;
        this.emailInstitucional = emailInstitucional;
        this.nivelAcesso = nivelAcesso;
        this.senha = senha;
    }

    /**
     * Valida o acesso ao sistema.
     */
    public boolean autenticar(String email, String senhaFornecida) {
        if (this.emailInstitucional == null || this.senha == null) {
            return false;
        }
        return this.emailInstitucional.equalsIgnoreCase(email.trim()) && this.senha.equals(senhaFornecida);
    }

    /**
     * Retorna a lista de projetos referentes a um coordenador cadastrado no sistema.
     */
    public List<Projeto> consultarProjetos(String cpfOuNomeDocente) {
        return DadosService.getInstance().consultarProjetosPorDocente(cpfOuNomeDocente);
    }

    /**
     * Retorna a lista de participantes vinculados a um projeto.
     */
    public List<HistoricoProj> consultarParticipantes(int idProjeto) {
        return DadosService.getInstance().consultarParticipantesPorProjeto(idProjeto);
    }

    /**
     * Retorna a lista de producoes vinculadas a um projeto.
     */
    public List<Producao> consultarProducoes(int idProjeto) {
        return DadosService.getInstance().consultarProducoesPorProjeto(idProjeto);
    }

    /**
     * Retorna os dados dos financiamentos registrados em um projeto.
     */
    public Financiamento consultarFinanciamentos(int idProjeto) {
        return DadosService.getInstance().consultarFinanciamentoPorProjeto(idProjeto);
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmailInstitucional() {
        return emailInstitucional;
    }

    public void setEmailInstitucional(String emailInstitucional) {
        this.emailInstitucional = emailInstitucional;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isCoordenador() {
        return "Coordenador de Projeto".equalsIgnoreCase(this.nivelAcesso);
    }

    public boolean isGestorAdministrativo() {
        return "Gestor Administrativo".equalsIgnoreCase(this.nivelAcesso);
    }

    @Override
    public String toString() {
        return nome + " (" + nivelAcesso + ")";
    }
}
