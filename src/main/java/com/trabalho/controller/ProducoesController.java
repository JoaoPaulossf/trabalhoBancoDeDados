package com.trabalho.controller;

import com.trabalho.model.Participante;
import com.trabalho.model.Producao;
import com.trabalho.model.Projeto;
import com.trabalho.model.Usuario;
import com.trabalho.service.DadosService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para UC05 (Cadastrar Producao Cientifica).
 * Atende regras RN-08 (vinculacao de 1 a N autores a partir da base de participantes).
 */
public class ProducoesController {

    @FXML private TextField txtIdProducao;
    @FXML private ComboBox<Projeto> cbProjeto;
    @FXML private TextField txtTitulo;
    @FXML private ComboBox<String> cbTipoProducao;
    @FXML private TextField txtAnoPublicacao;
    @FXML private TextField txtMeioDivulgacao;

    // Selecao de autores (Autoria - Secao 9.8 / RN-08)
    @FXML private ComboBox<Participante> cbParticipanteAutor;
    @FXML private ListView<Participante> lvAutoresSelecionados;
    private final ObservableList<Participante> listaAutoresSelecionados = FXCollections.observableArrayList();

    @FXML private Button btnAdicionarAutor;
    @FXML private Button btnRemoverAutor;
    @FXML private Button btnSalvar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnExcluir;
    @FXML private Button btnLimpar;
    @FXML private Label lblAvisoPerfil;

    @FXML private TableView<Producao> tabelaProducoes;
    @FXML private TableColumn<Producao, Integer> colId;
    @FXML private TableColumn<Producao, String> colTitulo;
    @FXML private TableColumn<Producao, String> colProjeto;
    @FXML private TableColumn<Producao, String> colTipoProducao;
    @FXML private TableColumn<Producao, String> colAnoPublicacao;
    @FXML private TableColumn<Producao, String> colMeioDivulgacao;
    @FXML private TableColumn<Producao, String> colAutores;

    private Producao producaoSelecionada;

    @FXML
    public void initialize() {
        cbTipoProducao.setItems(FXCollections.observableArrayList(
                "Artigo em Periódico",
                "Artigo em Congresso",
                "Capítulo de Livro",
                "Livro",
                "Patente",
                "Relatório Técnico",
                "Software"
        ));

        cbProjeto.setItems(DadosService.getInstance().getProjetos());
        cbParticipanteAutor.setItems(DadosService.getInstance().getParticipantes());
        lvAutoresSelecionados.setItems(listaAutoresSelecionados);

        colId.setCellValueFactory(new PropertyValueFactory<>("idProducao"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colProjeto.setCellValueFactory(new PropertyValueFactory<>("tituloProjeto"));
        colTipoProducao.setCellValueFactory(new PropertyValueFactory<>("tipoProducao"));
        colAnoPublicacao.setCellValueFactory(new PropertyValueFactory<>("anoPublicacao"));
        colMeioDivulgacao.setCellValueFactory(new PropertyValueFactory<>("meioDivulgacao"));
        colAutores.setCellValueFactory(new PropertyValueFactory<>("autoresFormatados"));

        tabelaProducoes.setItems(DadosService.getInstance().getProducoes());

        tabelaProducoes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selecionarItem(newSel);
            }
        });

        verificarPermissaoUsuario();
        limpar();
    }

    private void verificarPermissaoUsuario() {
        Usuario user = DadosService.getInstance().getUsuarioLogado();
        if (user != null && user.isGestorAdministrativo()) {
            btnSalvar.setDisable(true);
            btnAtualizar.setDisable(true);
            btnExcluir.setDisable(true);
            btnAdicionarAutor.setDisable(true);
            btnRemoverAutor.setDisable(true);
            lblAvisoPerfil.setText("Perfil Gestor Administrativo: Apenas consulta permitida (AT02).");
            lblAvisoPerfil.setVisible(true);
        } else {
            lblAvisoPerfil.setVisible(false);
        }
    }

    private void selecionarItem(Producao p) {
        producaoSelecionada = p;
        txtIdProducao.setText(String.valueOf(p.getIdProducao()));
        txtTitulo.setText(p.getTitulo());
        cbTipoProducao.setValue(p.getTipoProducao());
        txtAnoPublicacao.setText(p.getAnoPublicacao());
        txtMeioDivulgacao.setText(p.getMeioDivulgacao());

        for (Projeto proj : cbProjeto.getItems()) {
            if (proj.getIdProjeto() == p.getIdProjeto()) {
                cbProjeto.setValue(proj);
                break;
            }
        }

        // Carrega autores vinculados a esta producao
        listaAutoresSelecionados.clear();
        listaAutoresSelecionados.addAll(p.listarAutoresProducao());

        Usuario user = DadosService.getInstance().getUsuarioLogado();
        boolean podeEditar = (user == null || user.isCoordenador());
        btnAtualizar.setDisable(!podeEditar);
        btnExcluir.setDisable(!podeEditar);
        btnSalvar.setDisable(true);
    }

    @FXML
    public void adicionarAutor() {
        Participante selecionado = cbParticipanteAutor.getValue();
        if (selecionado == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Atenção", "Selecione um participante na lista suspensa para adicionar como autor.");
            return;
        }

        if (listaAutoresSelecionados.contains(selecionado)) {
            exibirMensagem(Alert.AlertType.WARNING, "Atenção", "Este participante já foi adicionado aos autores desta produção.");
            return;
        }

        listaAutoresSelecionados.add(selecionado);
    }

    @FXML
    public void removerAutor() {
        Participante selecionado = lvAutoresSelecionados.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Atenção", "Selecione um autor na lista de autores para remover.");
            return;
        }

        listaAutoresSelecionados.remove(selecionado);
    }

    /**
     * UC05: Cadastrar Producao Cientifica
     */
    @FXML
    public void salvar() {
        Usuario user = DadosService.getInstance().getUsuarioLogado();
        if (user != null && user.isGestorAdministrativo()) {
            exibirMensagem(Alert.AlertType.WARNING, "Acesso Negado", "Apenas Coordenadores podem registrar produções científicas.");
            return;
        }

        if (!validarCampos()) return;

        Projeto proj = cbProjeto.getValue();
        int novoId = DadosService.getInstance().gerarProximoIdProducao();

        Producao nova = new Producao(
                novoId,
                proj.getIdProjeto(),
                txtTitulo.getText().trim(),
                cbTipoProducao.getValue(),
                txtAnoPublicacao.getText().trim(),
                txtMeioDivulgacao.getText().trim()
        );

        nova.cadastrarProducao();

        // Salva vinculo de autoria (Autoria - Secao 9.8)
        List<String> cpfs = new ArrayList<>();
        for (Participante p : listaAutoresSelecionados) {
            cpfs.add(p.getCpfColaborador());
        }
        DadosService.getInstance().substituirAutoresDaProducao(novoId, cpfs);

        tabelaProducoes.refresh();
        limpar();
        exibirMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Produção científica cadastrada com sucesso (UC05)! ID #" + novoId);
    }

    @FXML
    public void atualizar() {
        if (producaoSelecionada == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Atenção", "Selecione uma produção na tabela para atualizar.");
            return;
        }

        if (!validarCampos()) return;

        Projeto proj = cbProjeto.getValue();
        producaoSelecionada.setIdProjeto(proj.getIdProjeto());
        producaoSelecionada.atualizarDadosProducao(
                txtTitulo.getText().trim(),
                cbTipoProducao.getValue(),
                txtAnoPublicacao.getText().trim(),
                txtMeioDivulgacao.getText().trim()
        );

        // Atualiza autores vinculados (Autoria)
        List<String> cpfs = new ArrayList<>();
        for (Participante p : listaAutoresSelecionados) {
            cpfs.add(p.getCpfColaborador());
        }
        DadosService.getInstance().substituirAutoresDaProducao(producaoSelecionada.getIdProducao(), cpfs);

        tabelaProducoes.refresh();
        limpar();
        exibirMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Dados da produção científica atualizados!");
    }

    @FXML
    public void excluir() {
        if (producaoSelecionada == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Atenção", "Selecione uma produção na tabela para excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja realmente remover a produção \"" + producaoSelecionada.getTitulo() + "\"?");

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            DadosService.getInstance().substituirAutoresDaProducao(producaoSelecionada.getIdProducao(), null);
            DadosService.getInstance().getProducoes().remove(producaoSelecionada);
            tabelaProducoes.refresh();
            limpar();
            exibirMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Produção removida com sucesso!");
        }
    }

    @FXML
    public void limpar() {
        producaoSelecionada = null;
        txtIdProducao.setText(String.valueOf(DadosService.getInstance().gerarProximoIdProducao()) + " (Automático)");
        cbProjeto.setValue(null);
        txtTitulo.clear();
        cbTipoProducao.setValue(null);
        txtAnoPublicacao.clear();
        txtMeioDivulgacao.clear();
        cbParticipanteAutor.setValue(null);
        listaAutoresSelecionados.clear();
        tabelaProducoes.getSelectionModel().clearSelection();

        Usuario user = DadosService.getInstance().getUsuarioLogado();
        boolean podeEditar = (user == null || user.isCoordenador());
        btnSalvar.setDisable(!podeEditar);
        btnAtualizar.setDisable(true);
        btnExcluir.setDisable(true);
    }

    private boolean validarCampos() {
        if (cbProjeto.getValue() == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "Selecione o Projeto de pesquisa ao qual a produção pertence.");
            return false;
        }

        if (txtTitulo.getText().trim().isEmpty()) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "O Título da produção é obrigatório.");
            return false;
        }

        if (cbTipoProducao.getValue() == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "Selecione o Tipo de Produção Científica.");
            return false;
        }

        if (txtAnoPublicacao.getText().trim().isEmpty()) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "Informe o Ano ou Data de publicação.");
            return false;
        }

        // RN-08 / UC05 Excecao 4a: Ausencia de autores
        if (listaAutoresSelecionados.isEmpty()) {
            exibirMensagem(Alert.AlertType.ERROR, "Ausência de Autores (RN-08)",
                    "Não é possível salvar uma produção científica sem informar ao menos um autor vinculado!");
            return false;
        }

        return true;
    }

    private void exibirMensagem(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
