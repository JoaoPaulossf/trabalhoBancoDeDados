package com.trabalho.controller;

import com.trabalho.model.*;
import com.trabalho.service.DadosService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para UC06 (Consultar Informacoes) e Requisito RF-05.
 * Permite aos Coordenadores e Gestores Administrativos realizar buscas e consultas estruturadas.
 */
public class ConsultasController {

    // TabPane de Consultas
    @FXML private TabPane tabPaneConsultas;

    // --- ABA 1: Projetos por Docente ---
    @FXML private ComboBox<Coordenador> cbFiltroDocente;
    @FXML private TableView<Projeto> tabelaProjetosDocente;
    @FXML private TableColumn<Projeto, Integer> colProjId;
    @FXML private TableColumn<Projeto, String> colProjTitulo;
    @FXML private TableColumn<Projeto, String> colProjDescricao;
    @FXML private TableColumn<Projeto, LocalDate> colProjInicio;
    @FXML private TableColumn<Projeto, LocalDate> colProjTermino;
    @FXML private TableColumn<Projeto, String> colProjSituacao;
    @FXML private TableColumn<Projeto, String> colProjCoordenador;
    @FXML private Label lblResultadoProjetos;

    // --- ABA 2: Participantes por Projeto ---
    @FXML private ComboBox<Projeto> cbFiltroProjetoParticipantes;
    @FXML private TableView<HistoricoProj> tabelaParticipantesProjeto;
    @FXML private TableColumn<HistoricoProj, String> colPartNome;
    @FXML private TableColumn<HistoricoProj, String> colPartCpf;
    @FXML private TableColumn<HistoricoProj, String> colPartEmail;
    @FXML private TableColumn<HistoricoProj, String> colPartTipo;
    @FXML private TableColumn<HistoricoProj, String> colPartFuncao;
    @FXML private TableColumn<HistoricoProj, LocalDate> colPartInicio;
    @FXML private TableColumn<HistoricoProj, LocalDate> colPartFim;
    @FXML private TableColumn<HistoricoProj, String> colPartStatus;
    @FXML private Label lblResultadoParticipantes;

    // --- ABA 3: Producoes por Projeto ---
    @FXML private ComboBox<Projeto> cbFiltroProjetoProducoes;
    @FXML private TableView<Producao> tabelaProducoesProjeto;
    @FXML private TableColumn<Producao, Integer> colProdId;
    @FXML private TableColumn<Producao, String> colProdTitulo;
    @FXML private TableColumn<Producao, String> colProdTipo;
    @FXML private TableColumn<Producao, String> colProdAno;
    @FXML private TableColumn<Producao, String> colProdMeio;
    @FXML private TableColumn<Producao, String> colProdAutores;
    @FXML private Label lblResultadoProducoes;

    // --- ABA 4: Financiamento por Projeto ---
    @FXML private ComboBox<Projeto> cbFiltroProjetoFinanciamento;
    @FXML private TableView<Financiamento> tabelaFinanciamentoProjeto;
    @FXML private TableColumn<Financiamento, String> colFinProjeto;
    @FXML private TableColumn<Financiamento, String> colFinAgencia;
    @FXML private TableColumn<Financiamento, String> colFinTipo;
    @FXML private TableColumn<Financiamento, String> colFinValor;
    @FXML private TableColumn<Financiamento, String> colFinSituacao;
    @FXML private TableColumn<Financiamento, LocalDate> colFinInicio;
    @FXML private TableColumn<Financiamento, LocalDate> colFinFim;
    @FXML private Label lblResultadoFinanciamento;

    @FXML
    public void initialize() {
        // Inicializa combos
        cbFiltroDocente.setItems(DadosService.getInstance().getCoordenadores());
        cbFiltroProjetoParticipantes.setItems(DadosService.getInstance().getProjetos());
        cbFiltroProjetoProducoes.setItems(DadosService.getInstance().getProjetos());
        cbFiltroProjetoFinanciamento.setItems(DadosService.getInstance().getProjetos());

        configurarTabelaProjetos();
        configurarTabelaParticipantes();
        configurarTabelaProducoes();
        configurarTabelaFinanciamentos();

        // Carrega dados iniciais em cada tabela
        consultarTodosProjetos();
        consultarTodosParticipantes();
        consultarTodasProducoes();
        consultarTodosFinanciamentos();
    }

    private void configurarTabelaProjetos() {
        colProjId.setCellValueFactory(new PropertyValueFactory<>("idProjeto"));
        colProjTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colProjDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colProjInicio.setCellValueFactory(new PropertyValueFactory<>("dataInicio"));
        colProjTermino.setCellValueFactory(new PropertyValueFactory<>("dataTermino"));
        colProjSituacao.setCellValueFactory(new PropertyValueFactory<>("situacao"));
        colProjCoordenador.setCellValueFactory(new PropertyValueFactory<>("nomeCoordenador"));
    }

    private void configurarTabelaParticipantes() {
        colPartNome.setCellValueFactory(new PropertyValueFactory<>("nomeParticipante"));
        colPartCpf.setCellValueFactory(new PropertyValueFactory<>("cpfColaborador"));
        colPartEmail.setCellValueFactory(new PropertyValueFactory<>("emailParticipante"));
        colPartTipo.setCellValueFactory(new PropertyValueFactory<>("tipoParticipante"));
        colPartFuncao.setCellValueFactory(new PropertyValueFactory<>("funcao"));
        colPartInicio.setCellValueFactory(new PropertyValueFactory<>("dataInicio"));
        colPartFim.setCellValueFactory(new PropertyValueFactory<>("dataFim"));
        colPartStatus.setCellValueFactory(new PropertyValueFactory<>("statusVinculo"));
    }

    private void configurarTabelaProducoes() {
        colProdId.setCellValueFactory(new PropertyValueFactory<>("idProducao"));
        colProdTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colProdTipo.setCellValueFactory(new PropertyValueFactory<>("tipoProducao"));
        colProdAno.setCellValueFactory(new PropertyValueFactory<>("anoPublicacao"));
        colProdMeio.setCellValueFactory(new PropertyValueFactory<>("meioDivulgacao"));
        colProdAutores.setCellValueFactory(new PropertyValueFactory<>("autoresFormatados"));
    }

    private void configurarTabelaFinanciamentos() {
        colFinProjeto.setCellValueFactory(new PropertyValueFactory<>("tituloProjeto"));
        colFinAgencia.setCellValueFactory(new PropertyValueFactory<>("agencia"));
        colFinTipo.setCellValueFactory(new PropertyValueFactory<>("tipoFomento"));
        colFinValor.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        colFinSituacao.setCellValueFactory(new PropertyValueFactory<>("situacao"));
        colFinInicio.setCellValueFactory(new PropertyValueFactory<>("dataInicio"));
        colFinFim.setCellValueFactory(new PropertyValueFactory<>("dataFim"));
    }

    // ==========================================
    // CONSULTA 1: Projetos por Docente
    // ==========================================
    @FXML
    public void filtrarProjetosPorDocente() {
        Coordenador coord = cbFiltroDocente.getValue();
        if (coord == null) {
            consultarTodosProjetos();
            return;
        }

        List<Projeto> resultados = DadosService.getInstance().consultarProjetosPorDocente(coord.getCpf());
        tabelaProjetosDocente.setItems(FXCollections.observableArrayList(resultados));

        if (resultados.isEmpty()) {
            lblResultadoProjetos.setText("Aviso: Não há registros correspondentes aos critérios de busca (Exceção 3a).");
            lblResultadoProjetos.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
        } else {
            lblResultadoProjetos.setText(resultados.size() + " projeto(s) encontrado(s) para o docente " + coord.getNome() + ".");
            lblResultadoProjetos.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void consultarTodosProjetos() {
        cbFiltroDocente.setValue(null);
        tabelaProjetosDocente.setItems(DadosService.getInstance().getProjetos());
        lblResultadoProjetos.setText("Exibindo todos os " + DadosService.getInstance().getProjetos().size() + " projetos cadastrados.");
        lblResultadoProjetos.setStyle("-fx-text-fill: #64748b;");
    }

    // ==========================================
    // CONSULTA 2: Participantes por Projeto
    // ==========================================
    @FXML
    public void filtrarParticipantesPorProjeto() {
        Projeto proj = cbFiltroProjetoParticipantes.getValue();
        if (proj == null) {
            consultarTodosParticipantes();
            return;
        }

        List<HistoricoProj> resultados = DadosService.getInstance().consultarParticipantesPorProjeto(proj.getIdProjeto());
        tabelaParticipantesProjeto.setItems(FXCollections.observableArrayList(resultados));

        if (resultados.isEmpty()) {
            lblResultadoParticipantes.setText("Aviso: Nenhum participante vinculado a este projeto (Exceção 3a).");
            lblResultadoParticipantes.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
        } else {
            lblResultadoParticipantes.setText(resultados.size() + " participante(s) vinculado(s) ao projeto selecionado.");
            lblResultadoParticipantes.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void consultarTodosParticipantes() {
        cbFiltroProjetoParticipantes.setValue(null);
        tabelaParticipantesProjeto.setItems(DadosService.getInstance().getHistoricoProjetos());
        lblResultadoParticipantes.setText("Exibindo todos os " + DadosService.getInstance().getHistoricoProjetos().size() + " vínculos registrados.");
        lblResultadoParticipantes.setStyle("-fx-text-fill: #64748b;");
    }

    // ==========================================
    // CONSULTA 3: Producoes por Projeto
    // ==========================================
    @FXML
    public void filtrarProducoesPorProjeto() {
        Projeto proj = cbFiltroProjetoProducoes.getValue();
        if (proj == null) {
            consultarTodasProducoes();
            return;
        }

        List<Producao> resultados = DadosService.getInstance().consultarProducoesPorProjeto(proj.getIdProjeto());
        tabelaProducoesProjeto.setItems(FXCollections.observableArrayList(resultados));

        if (resultados.isEmpty()) {
            lblResultadoProducoes.setText("Aviso: Nenhuma produção científica registrada para este projeto (Exceção 3a).");
            lblResultadoProducoes.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
        } else {
            lblResultadoProducoes.setText(resultados.size() + " produção(ões) encontrada(s) para este projeto.");
            lblResultadoProducoes.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void consultarTodasProducoes() {
        cbFiltroProjetoProducoes.setValue(null);
        tabelaProducoesProjeto.setItems(DadosService.getInstance().getProducoes());
        lblResultadoProducoes.setText("Exibindo todas as " + DadosService.getInstance().getProducoes().size() + " produções cadastradas.");
        lblResultadoProducoes.setStyle("-fx-text-fill: #64748b;");
    }

    // ==========================================
    // CONSULTA 4: Financiamentos por Projeto
    // ==========================================
    @FXML
    public void filtrarFinanciamentoPorProjeto() {
        Projeto proj = cbFiltroProjetoFinanciamento.getValue();
        if (proj == null) {
            consultarTodosFinanciamentos();
            return;
        }

        Financiamento fin = DadosService.getInstance().consultarFinanciamentoPorProjeto(proj.getIdProjeto());
        ObservableList<Financiamento> lista = FXCollections.observableArrayList();
        if (fin != null) {
            lista.add(fin);
            tabelaFinanciamentoProjeto.setItems(lista);
            lblResultadoFinanciamento.setText("Contrato de financiamento localizado (Situação: " + fin.getSituacao() + ").");
            lblResultadoFinanciamento.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        } else {
            tabelaFinanciamentoProjeto.setItems(lista);
            lblResultadoFinanciamento.setText("Aviso: Este projeto não possui suporte financeiro registrado (Exceção 3a).");
            lblResultadoFinanciamento.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void consultarTodosFinanciamentos() {
        cbFiltroProjetoFinanciamento.setValue(null);
        tabelaFinanciamentoProjeto.setItems(DadosService.getInstance().getFinanciamentos());
        lblResultadoFinanciamento.setText("Exibindo todos os " + DadosService.getInstance().getFinanciamentos().size() + " registros de financiamento.");
        lblResultadoFinanciamento.setStyle("-fx-text-fill: #64748b;");
    }
}
