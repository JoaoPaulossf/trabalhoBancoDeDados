package com.trabalho.model;

import com.trabalho.service.DadosService;
import java.util.List;

/**
 * Documentar e armazenar os resultados cientificos gerados pelo projeto,
 * como artigos publicados, relatorios e apresentacoes.
 * Conforme Modelo de Classes (Secao 8 e 9.7).
 */
public class Producao {
    private int idProducao;         // PK gerada pelo sistema
    private int idProjeto;          // FK para o Projeto
    private String titulo;
    private String tipo;            // Artigo em Periodico, Resumo, Relatorio Tecnico, etc.
    private String dataPublicacao;  // Ano ou data em que foi apresentada/publicada
    private String meioDivulgacao;  // Periodico, conferencia, evento

    public Producao() {
    }

    public Producao(int idProducao, int idProjeto, String titulo, String tipo, String dataPublicacao, String meioDivulgacao) {
        this.idProducao = idProducao;
        this.idProjeto = idProjeto;
        this.titulo = titulo;
        this.tipo = tipo;
        this.dataPublicacao = dataPublicacao;
        this.meioDivulgacao = meioDivulgacao;
    }

    /**
     * Registra a nova producao cientifica associando-a a um projeto.
     */
    public boolean cadastrarProducao() {
        return DadosService.getInstance().salvarProducao(this);
    }

    /**
     * Corrige informacoes do titulo, tipo, data ou meio de divulgacao da producao.
     */
    public void atualizarDadosProducao(String titulo, String tipo, String dataPublicacao, String meioDivulgacao) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.dataPublicacao = dataPublicacao;
        this.meioDivulgacao = meioDivulgacao;
    }

    /**
     * Retorna a lista de participantes que sao autores daquela publicacao.
     */
    public List<Participante> listarAutoresProducao() {
        return DadosService.getInstance().listarAutoresDaProducao(idProducao);
    }

    public int getIdProducao() {
        return idProducao;
    }

    public void setIdProducao(int idProducao) {
        this.idProducao = idProducao;
    }

    public int getIdProjeto() {
        return idProjeto;
    }

    public void setIdProjeto(int idProjeto) {
        this.idProjeto = idProjeto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // Compatibilidade com telas
    public String getTipoProducao() {
        return tipo;
    }

    public void setTipoProducao(String tipoProducao) {
        this.tipo = tipoProducao;
    }

    public String getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public String getAnoPublicacao() {
        return dataPublicacao;
    }

    public void setAnoPublicacao(String anoPublicacao) {
        this.dataPublicacao = anoPublicacao;
    }

    public String getMeioDivulgacao() {
        return meioDivulgacao;
    }

    public void setMeioDivulgacao(String meioDivulgacao) {
        this.meioDivulgacao = meioDivulgacao;
    }

    // Helper para TableView
    public String getTituloProjeto() {
        Projeto proj = DadosService.getInstance().buscarProjetoPorId(idProjeto);
        return proj != null ? proj.getTitulo() : "Projeto #" + idProjeto;
    }

    public String getAutoresFormatados() {
        List<Participante> autores = listarAutoresProducao();
        if (autores.isEmpty()) return "(Nenhum autor registrado)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < autores.size(); i++) {
            sb.append(autores.get(i).getNome());
            if (i < autores.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    public String getAutores() {
        return getAutoresFormatados();
    }

    @Override
    public String toString() {
        return "[" + idProducao + "] " + titulo;
    }
}
