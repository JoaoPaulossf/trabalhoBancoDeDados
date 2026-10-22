package com.trabalho.controller;

import com.trabalho.model.HistoricoProj;
import com.trabalho.model.Participante;
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
 * Controlador para UC03 (Vincular Participante ao Projeto) e UC08 (Atualizar Vinculo de Participante).
 * Atende regras RN-04, RN-05, RN-09.
 */
public class ParticipantesController {

    // Dados do Participante (Pessoa)
    @FXML private TextField txtCpf;
    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cbTipo;

    // Dados do Vinculo com Projeto (Historico_Proj)
    @FXML private ComboBox<Projeto> cbProjeto;
    @FXML private ComboBox<String> cbFuncao;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpDataFim;

    // Botoes de Acao
    @FXML private Button btnVincular;
    @FXML private Button btnAtualizarVinculo;
    @FXML private Button btnEncerrarVinculo;
    @FXML private Button btnLimpar;
    @FXML private Label lblAvisoPerfil;

    // Tabela de Vinculos (Historico_Proj)
    @FXML private TableView<HistoricoProj> tabelaVinculos;
    @FXML private TableColumn<HistoricoProj, String> colProjeto;
    @FXML private TableColumn<HistoricoProj, String> colParticipante;
    @FXML private TableColumn<HistoricoProj, String> colCpf;
    @FXML private TableColumn<HistoricoProj, String> colTipo;
    @FXML private TableColumn<HistoricoProj, String> colFuncao;
    @FXML private TableColumn<HistoricoProj, LocalDate> colDataInicio;
    @FXML private TableColumn<HistoricoProj, LocalDate> colDataFim;
    @FXML private TableColumn<HistoricoProj, String> colStatus;

    private HistoricoProj vinculoSelecionado;

    @FXML
    public void initialize() {
        cbTipo.setItems(FXCollections.observableArrayList(
                "Docente",
                "Discente",
                "Técnico-Administrativo"
        ));

        cbFuncao.setItems(FXCollections.observableArrayList(
                "Coordenador",
                "Pesquisador",
                "Pesquisador Colaborador",
                "Bolsista de IC",
                "Bolsista de Mestrado",
                "Bolsista de Doutorado",
                "Colaborador",
                "Orientador",
                "Apoio Técnico"
        ));

        cbProjeto.setItems(DadosService.getInstance().getProjetos());

        // Colunas da Tabela
        colProjeto.setCellValueFactory(new PropertyValueFactory<>("tituloProjeto"));
        colParticipante.setCellValueFactory(new PropertyValueFactory<>("nomeParticipante"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpfColaborador"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoParticipante"));
        colFuncao.setCellValueFactory(new PropertyValueFactory<>("funcao"));
        colDataInicio.setCellValueFactory(new PropertyValueFactory<>("dataInicio"));
        colDataFim.setCellValueFactory(new PropertyValueFactory<>("dataFim"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusVinculo"));

        tabelaVinculos.setItems(DadosService.getInstance().getHistoricoProjetos());

        tabelaVinculos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selecionarItem(newSel);
            }
        });

        // Autocompletar dados do participante ao digitar CPF existente (UC03 - Fluxo Alternativo 3a)
        txtCpf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // ao perder foco
                buscarEAutocompletarParticipante();
            }
        });

        verificarPermissaoUsuario();
        limpar();
    }

    private void verificarPermissaoUsuario() {
        Usuario user = DadosService.getInstance().getUsuarioLogado();
        if (user != null && user.isGestorAdministrativo()) {
            btnVincular.setDisable(true);
            btnAtualizarVinculo.setDisable(true);
            btnEncerrarVinculo.setDisable(true);
            lblAvisoPerfil.setText("Perfil Gestor Administrativo: Apenas consulta permitida.");
            lblAvisoPerfil.setVisible(true);
        } else {
            lblAvisoPerfil.setVisible(false);
        }
    }

    /**
     * UC03 - Fluxo Alternativo 3a: Participante ja existe
     * O sistema identifica o participante atraves do CPF e preenche os dados cadastrais.
     */
    private void buscarEAutocompletarParticipante() {
        String cpf = txtCpf.getText().trim();
        if (cpf.isEmpty()) return;

        Participante existente = DadosService.getInstance().buscarParticipantePorCpf(cpf);
        if (existente != null) {
            txtNome.setText(existente.getNome());
            txtEmail.setText(existente.getEmail());
            cbTipo.setValue(existente.getTipo());
        }
    }

    private void selecionarItem(HistoricoProj h) {
        vinculoSelecionado = h;

        // Preenche dados do vinculo
        cbFuncao.setValue(h.getFuncao());
        dpDataInicio.setValue(h.getDataInicio());
        dpDataFim.setValue(h.getDataFim());

        // Seleciona projeto
        for (Projeto p : cbProjeto.getItems()) {
            if (p.getIdProjeto() == h.getIdProjeto()) {
                cbProjeto.setValue(p);
                break;
            }
        }

        // Preenche dados do participante
        Participante part = DadosService.getInstance().buscarParticipantePorCpf(h.getCpfColaborador());
        if (part != null) {
            txtCpf.setText(part.getCpfColaborador());
            txtNome.setText(part.getNome());
            txtEmail.setText(part.getEmail());
            cbTipo.setValue(part.getTipo());
        }

        Usuario user = DadosService.getInstance().getUsuarioLogado();
        boolean podeEditar = (user == null || user.isCoordenador());
        btnAtualizarVinculo.setDisable(!podeEditar);
        btnEncerrarVinculo.setDisable(!podeEditar || !h.isAtivo());
        btnVincular.setDisable(true);
    }

    /**
     * UC03: Vincular Participante ao Projeto
     */
    @FXML
    public void vincular() {
        Usuario user = DadosService.getInstance().getUsuarioLogado();
        if (user != null && user.isGestorAdministrativo()) {
            exibirMensagem(Alert.AlertType.WARNING, "Acesso Negado", "Apenas Coordenadores podem vincular participantes.");
            return;
        }

        if (!validarCampos()) return;

        String cpfLimpo = DadosService.limparCpf(txtCpf.getText());
        Projeto proj = cbProjeto.getValue();

        // 1. Cadastra ou atualiza o participante na base (UC03)
        Participante part = DadosService.getInstance().buscarParticipantePorCpf(cpfLimpo);
        if (part == null) {
            part = new Participante(cpfLimpo, txtNome.getText().trim(), txtEmail.getText().trim(), cbTipo.getValue());
            part.cadastrarParticipante();
        } else {
            part.atualizarDadosParticipante(txtNome.getText().trim(), txtEmail.getText().trim(), cbTipo.getValue());
        }

        // 2. Cria o vinculo (Historico_Proj)
        HistoricoProj vinculo = new HistoricoProj(
                proj.getIdProjeto(),
                cpfLimpo,
                cbFuncao.getValue(),
                dpDataInicio.getValue(),
                dpDataFim.getValue()
        );

        vinculo.vincularParticipante();
        tabelaVinculos.refresh();
        limpar();
        exibirMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Participante vinculado com sucesso ao projeto (UC03)!");
    }

    /**
     * UC08: Atualizar Vinculo de Participante
     */
    @FXML
    public void atualizarVinculo() {
        if (vinculoSelecionado == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Atenção", "Selecione um vínculo na tabela para atualizar (UC08).");
            return;
        }

        if (!validarCampos()) return;

        // UC08 Excecao 3a: Datas invalidas
        if (dpDataFim.getValue() != null && dpDataFim.getValue().isBefore(dpDataInicio.getValue())) {
            exibirMensagem(Alert.AlertType.ERROR, "Validação de Datas", "A data de fim não pode ser anterior à data de início do vínculo.");
            return;
        }

        vinculoSelecionado.atualizarVinculo(cbFuncao.getValue(), dpDataInicio.getValue(), dpDataFim.getValue());

        // Atualiza tambem dados cadastrais se alterados
        Participante part = DadosService.getInstance().buscarParticipantePorCpf(vinculoSelecionado.getCpfColaborador());
        if (part != null) {
            part.atualizarDadosParticipante(txtNome.getText().trim(), txtEmail.getText().trim(), cbTipo.getValue());
        }

        tabelaVinculos.refresh();
        limpar();
        exibirMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Vínculo de participação atualizado com sucesso (UC08)!");
    }

    /**
     * UC08 Fluxo Alternativo 2a & RN-09: Encerrar Participacao
     * Preenche a data de fim com a data atual e encerra o vinculo sem exclusao fisica.
     */
    @FXML
    public void encerrarVinculo() {
        if (vinculoSelecionado == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Atenção", "Selecione um vínculo ativo para encerrar.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Encerrar Vínculo de Participante (RN-09)");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja encerrar o vínculo de " + vinculoSelecionado.getNomeParticipante() +
                " no projeto \"" + vinculoSelecionado.getTituloProjeto() + "\"?\n\n(A data de saída será preenchida com a data de hoje)");

        Optional<ButtonType> resp = confirmacao.showAndWait();
        if (resp.isPresent() && resp.get() == ButtonType.OK) {
            vinculoSelecionado.encerrarVinculo(LocalDate.now());
            tabelaVinculos.refresh();
            limpar();
            exibirMensagem(Alert.AlertType.INFORMATION, "Vínculo Encerrado", "Participação encerrada com sucesso mantendo o histórico (RN-09).");
        }
    }

    @FXML
    public void limpar() {
        vinculoSelecionado = null;
        txtCpf.clear();
        txtNome.clear();
        txtEmail.clear();
        cbTipo.setValue(null);
        cbProjeto.setValue(null);
        cbFuncao.setValue(null);
        dpDataInicio.setValue(LocalDate.now());
        dpDataFim.setValue(null);
        tabelaVinculos.getSelectionModel().clearSelection();

        Usuario user = DadosService.getInstance().getUsuarioLogado();
        boolean podeEditar = (user == null || user.isCoordenador());
        btnVincular.setDisable(!podeEditar);
        btnAtualizarVinculo.setDisable(true);
        btnEncerrarVinculo.setDisable(true);
    }

    private boolean validarCampos() {
        // Valida campos obrigatorios do participante
        if (txtNome.getText().trim().isEmpty()) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "O Nome do participante é obrigatório.");
            return false;
        }

        String cpf = txtCpf.getText().trim();
        // RN-05 / Excecao 4a: CPF valido
        if (cpf.isEmpty() || !DadosService.validarCPF(cpf)) {
            exibirMensagem(Alert.AlertType.ERROR, "Erro de Validação (RN-05)",
                    "CPF inválido! Por favor informe um CPF válido com 11 dígitos.");
            return false;
        }

        String email = txtEmail.getText().trim();
        // RN-05 / Excecao 4a: E-mail institucional
        if (email.isEmpty() || !DadosService.validarEmailInstitucional(email)) {
            exibirMensagem(Alert.AlertType.ERROR, "Erro de Validação (RN-05)",
                    "E-mail institucional obrigatório! Utilize o e-mail oficial (ex: nome@ufes.br ou @aluno.ufes.br).");
            return false;
        }

        if (cbTipo.getValue() == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "Selecione a categoria de vínculo (Docente, Discente ou Técnico-Adm).");
            return false;
        }

        // Valida campos do vinculo
        if (cbProjeto.getValue() == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "Selecione o projeto de destino para vincular o participante.");
            return false;
        }

        if (cbFuncao.getValue() == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "Selecione a função a ser desempenhada.");
            return false;
        }

        if (dpDataInicio.getValue() == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "A Data de Início da atuação é obrigatória.");
            return false;
        }

        // Excecao 3a: Datas conflitantes
        if (dpDataFim.getValue() != null && dpDataFim.getValue().isBefore(dpDataInicio.getValue())) {
            exibirMensagem(Alert.AlertType.ERROR, "Erro de Validação", "A data de fim não pode ser anterior à data de início do vínculo.");
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
