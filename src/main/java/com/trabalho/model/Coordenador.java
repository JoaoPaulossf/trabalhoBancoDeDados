package com.trabalho.model;

/**
 * Representa o docente principal com privilegios gerenciais no sistema,
 * responsavel por inserir, atualizar e gerenciar os dados dos projetos
 * e suas entidades relacionadas.
 * Conforme Modelo de Classes (Secao 8 e 9.2).
 */
public class Coordenador extends Usuario {

    public Coordenador() {
        super();
        this.nivelAcesso = "Coordenador de Projeto";
    }

    public Coordenador(String cpf, String nome, String emailInstitucional, String senha) {
        super(cpf, nome, emailInstitucional, "Coordenador de Projeto", senha);
    }
}
