package com.trabalho.model;

import com.trabalho.service.DadosService;

/**
 * Classe associativa que vincula as Producoes Cientificas aos seus respectivos autores.
 * Conforme Modelo de Classes (Secao 8 e 9.8).
 */
public class Autoria {
    private int idProducao;         // FK referenciando a producao
    private String cpfParticip;     // FK referenciando o participante/autor

    public Autoria() {
    }

    public Autoria(int idProducao, String cpfParticip) {
        this.idProducao = idProducao;
        this.cpfParticip = cpfParticip;
    }

    /**
     * Associa um participante existente como autor de uma producao no momento do cadastro.
     */
    public boolean registrarAutor() {
        return DadosService.getInstance().salvarAutoria(this);
    }

    /**
     * Remove o vinculo entre o participante e a producao (em caso de erro de cadastro).
     */
    public boolean removerAutor() {
        return DadosService.getInstance().removerAutoria(this);
    }

    public int getIdProducao() {
        return idProducao;
    }

    public void setIdProducao(int idProducao) {
        this.idProducao = idProducao;
    }

    public String getCpfParticip() {
        return cpfParticip;
    }

    public void setCpfParticip(String cpfParticip) {
        this.cpfParticip = cpfParticip;
    }

    // Helper para exibir o nome do autor
    public String getNomeAutor() {
        Participante p = DadosService.getInstance().buscarParticipantePorCpf(cpfParticip);
        return p != null ? p.getNome() : cpfParticip;
    }

    @Override
    public String toString() {
        return getNomeAutor();
    }
}
