package com.trabalho.controller;

import com.trabalho.model.Coordenador;
import com.trabalho.model.Projeto;
import com.trabalho.model.Usuario;
import com.trabalho.service.DadosService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Controlador para UC02 (Cadastrar Projeto) e UC07 (Editar Dados do Projeto).
 * Aplica RN-01, RN-02, RN-03.
 */
public class ProjetosController {

    @FXML private TextField txtIdProjeto;
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpDataTermino;
    @FXML private ComboBox<String> cbSituacao;
    @FXML private ComboBox<Coordenador> cbCoordenador;

    @FXML private Button btnSalvar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnEncerrar;
    @FXML private Button btnLimpar;
    @FXML private Label lblAvisoPerfil;

    @FXML private TableView<Projeto> tabelaProjetos;
    @FXML private TableColumn<Projeto, Integer> colId;
    @FXML private TableColumn<Projeto, String> colTitulo;
    @FXML private TableColumn<Projeto, String> colDescricao;
    @FXML private TableColumn<Projeto, LocalDate> colDataInicio;
    @FXML private TableColumn<Projeto, LocalDate> colDataTermino;
    @FXML private TableColumn<Projeto, String> colSituacao;
    @FXML private TableColumn<Projeto, String> colCoordenador;

    private Projeto projetoSelecionado;

    @FXML
    public void initialize() {
        cbSituacao.setItems(FXCollections.observableArrayList(
                "Em andamento",
                "Concluído",
                "Cancelado"
        ));

        // RN-02: Um projeto possui estritamente 1 docente responsavel/principal
        cbCoordenador.setItems(DadosService.getInstance().getCoordenadores());

        colId.setCellValueFactory(new PropertyValueFactory<>("idProjeto"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colDataInicio.setCellValueFactory(new PropertyValueFactory<>("dataInicio"));
        colDataTermino.setCellValueFactory(new PropertyValueFactory<>("dataTermino"));
        colSituacao.setCellValueFactory(new PropertyValueFactory<>("situacao"));
        colCoordenador.setCellValueFactory(new PropertyValueFactory<>("nomeCoordenador"));

        tabelaProjetos.setItems(DadosService.getInstance().getProjetos());

        tabelaProjetos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
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
            btnEncerrar.setDisable(true);
            lblAvisoPerfil.setText("Perfil Gestor Administrativo: Apenas consulta permitida.");
            lblAvisoPerfil.setVisible(true);
        } else {
            lblAvisoPerfil.setVisible(false);
        }
    }

    private void selecionarItem(Projeto p) {
        projetoSelecionado = p;
        txtIdProjeto.setText(String.valueOf(p.getIdProjeto()));
        txtTitulo.setText(p.getTitulo());
        txtDescricao.setText(p.getDescricao());
        dpDataInicio.setValue(p.getDataInicio());
        dpDataTermino.setValue(p.getDataTermino());
        cbSituacao.setValue(p.getSituacao());

        // Localiza o coordenador responsavel no ComboBox
        for (Coordenador c : cbCoordenador.getItems()) {
            if (c.getCpf().equals(p.getIdCoordenador())) {
                cbCoordenador.setValue(c);
                break;
            }
        }

        Usuario user = DadosService.getInstance().getUsuarioLogado();
        boolean podeEditar = (user == null || user.isCoordenador());
        btnAtualizar.setDisable(!podeEditar);
        btnEncerrar.setDisable(!podeEditar);
        btnSalvar.setDisable(true);
    }

    @FXML
    public void salvar() {
        Usuario user = DadosService.getInstance().getUsuarioLogado();
        if (user != null && user.isGestorAdministrativo()) {
            exibirMensagem(Alert.AlertType.WARNING, "Acesso Negado", "Apenas Coordenadores de Projeto podem cadastrar projetos.");
            return;
        }

        if (!validarCampos()) return;

        Coordenador coord = cbCoordenador.getValue();
        int novoId = DadosService.getInstance().gerarProximoIdProjeto();

        Projeto novo = new Projeto(
                novoId,
                coord.getCpf(),
                txtTitulo.getText().trim(),
                txtDescricao.getText().trim(),
                dpDataInicio.getValue(),
                dpDataTermino.getValue(),
                cbSituacao.getValue() != null ? cbSituacao.getValue() : "Em andamento"
        );

        novo.cadastrarProjeto();
        tabelaProjetos.refresh();
        limpar();
        exibirMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Projeto cadastrado com sucesso! Código gerado: #" + novoId);
    }

    @FXML
    public void atualizar() {
        if (projetoSelecionado == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Atenção", "Selecione um projeto na tabela para atualizar.");
            return;
        }

        if (!validarCampos()) return;

        Coordenador coord = cbCoordenador.getValue();
        projetoSelecionado.atualizarDadosProjeto(
                txtTitulo.getText().trim(),
                txtDescricao.getText().trim(),
                dpDataInicio.getValue(),
                dpDataTermino.getValue(),
                cbSituacao.getValue(),
                coord.getCpf()
        );

        tabelaProjetos.refresh();
        limpar();
        exibirMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Dados do projeto atualizados com sucesso!");
    }

    /**
     * Encerra o projeto atualizando situacao para Concluido e definindo data final (UC07).
     */
    @FXML
    public void encerrarProjeto() {
        if (projetoSelecionado == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Atenção", "Selecione um projeto na tabela para encerrar.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Encerrar Projeto");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja realmente encerrar as atividades do projeto \"" + projetoSelecionado.getTitulo() + "\"?");

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            LocalDate dataFim = dpDataTermino.getValue() != null ? dpDataTermino.getValue() : LocalDate.now();
            projetoSelecionado.encerrarProjeto("Concluído", dataFim);
            tabelaProjetos.refresh();
            limpar();
            exibirMensagem(Alert.AlertType.INFORMATION, "Projeto Encerrado", "Situação alterada para Concluído com data de término registrada.");
        }
    }

    @FXML
    public void limpar() {
        projetoSelecionado = null;
        txtIdProjeto.setText(String.valueOf(DadosService.getInstance().gerarProximoIdProjeto()) + " (Automático)");
        txtTitulo.clear();
        txtDescricao.clear();
        dpDataInicio.setValue(LocalDate.now());
        dpDataTermino.setValue(null); // RN-03: Pode ser deixada em branco
        cbSituacao.setValue("Em andamento");
        cbCoordenador.setValue(null);
        tabelaProjetos.getSelectionModel().clearSelection();

        Usuario user = DadosService.getInstance().getUsuarioLogado();
        boolean podeCriar = (user == null || user.isCoordenador());
        btnSalvar.setDisable(!podeCriar);
        btnAtualizar.setDisable(true);
        btnEncerrar.setDisable(true);
    }

    private boolean validarCampos() {
        if (txtTitulo.getText().trim().isEmpty()) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "O Título do projeto é obrigatório.");
            return false;
        }

        if (dpDataInicio.getValue() == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "A Data de Início é obrigatória.");
            return false;
        }

        if (cbCoordenador.getValue() == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "É obrigatório vincular um docente responsável.");
            return false;
        }

        // UC07 Excecao 6a: Datas conflitantes
        if (dpDataTermino.getValue() != null && dpDataTermino.getValue().isBefore(dpDataInicio.getValue())) {
            exibirMensagem(Alert.AlertType.ERROR, "Erro de Validação", "A data de término não pode ser anterior à data de início do projeto.");
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
