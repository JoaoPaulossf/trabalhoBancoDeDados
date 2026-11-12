package com.trabalho.controller;

import com.trabalho.model.Financiamento;
import com.trabalho.model.Projeto;
import com.trabalho.model.Usuario;
import com.trabalho.service.DadosService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Controlador para UC04 (Cadastrar Financiamento).
 * Atende regras RN-06 (Vigencia inicio/fim NOT NULL), RN-07 (max 1 financiamento por projeto),
 * e RN-09 (Inativacao sem exclusao fisica).
 */
public class FinanciamentosController {

    @FXML private ComboBox<Projeto> cbProjeto;
    @FXML private ComboBox<String> cbSituacao;
    @FXML private TextField txtAgencia;
    @FXML private ComboBox<String> cbTipoFomento;
    @FXML private TextField txtValorTotal;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpDataFim;

    @FXML private Button btnSalvar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnInativar;
    @FXML private Button btnLimpar;
    @FXML private Label lblAvisoPerfil;

    @FXML private TableView<Financiamento> tabelaFinanciamentos;
    @FXML private TableColumn<Financiamento, String> colProjeto;
    @FXML private TableColumn<Financiamento, String> colAgencia;
    @FXML private TableColumn<Financiamento, String> colTipoFomento;
    @FXML private TableColumn<Financiamento, String> colValorTotal;
    @FXML private TableColumn<Financiamento, String> colSituacao;
    @FXML private TableColumn<Financiamento, LocalDate> colDataInicio;
    @FXML private TableColumn<Financiamento, LocalDate> colDataFim;

    private Financiamento financiamentoSelecionado;

    @FXML
    public void initialize() {
        cbSituacao.setItems(FXCollections.observableArrayList(
                "Ativo",
                "Inativo"
        ));

        cbTipoFomento.setItems(FXCollections.observableArrayList(
                "Edital Universal",
                "Auxílio à Pesquisa",
                "Bolsa de Produtividade",
                "Bolsa de Iniciação Científica",
                "Bolsa de Pós-Graduação",
                "Taxa de Bancada",
                "Acordo de Cooperação Técnica"
        ));

        cbProjeto.setItems(DadosService.getInstance().getProjetos());

        colProjeto.setCellValueFactory(new PropertyValueFactory<>("tituloProjeto"));
        colAgencia.setCellValueFactory(new PropertyValueFactory<>("agencia"));
        colTipoFomento.setCellValueFactory(new PropertyValueFactory<>("tipoFomento"));
        colValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        colSituacao.setCellValueFactory(new PropertyValueFactory<>("situacao"));
        colDataInicio.setCellValueFactory(new PropertyValueFactory<>("dataInicio"));
        colDataFim.setCellValueFactory(new PropertyValueFactory<>("dataFim"));

        tabelaFinanciamentos.setItems(DadosService.getInstance().getFinanciamentos());

        tabelaFinanciamentos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selecionarItem(newSel);
            }
        });

        // Alterna obrigatoriedade/campos se usuario escolher Inativo
        cbSituacao.valueProperty().addListener((obs, oldV, newV) -> {
            boolean ativo = "Ativo".equalsIgnoreCase(newV);
            txtAgencia.setDisable(!ativo && financiamentoSelecionado == null);
            cbTipoFomento.setDisable(!ativo && financiamentoSelecionado == null);
            txtValorTotal.setDisable(!ativo && financiamentoSelecionado == null);
            dpDataInicio.setDisable(!ativo && financiamentoSelecionado == null);
            dpDataFim.setDisable(!ativo && financiamentoSelecionado == null);
        });

        verificarPermissaoUsuario();
        limpar();
    }

    private void verificarPermissaoUsuario() {
        Usuario user = DadosService.getInstance().getUsuarioLogado();
        if (user != null && user.isGestorAdministrativo()) {
            btnSalvar.setDisable(true);
            btnAtualizar.setDisable(true);
            btnInativar.setDisable(true);
            lblAvisoPerfil.setText("Perfil Gestor Administrativo: Apenas consulta permitida (AT02).");
            lblAvisoPerfil.setVisible(true);
        } else {
            lblAvisoPerfil.setVisible(false);
        }
    }

    private void selecionarItem(Financiamento f) {
        financiamentoSelecionado = f;

        for (Projeto p : cbProjeto.getItems()) {
            if (p.getIdProjeto() == f.getIdProjeto()) {
                cbProjeto.setValue(p);
                break;
            }
        }

        cbSituacao.setValue(f.getSituacao());
        txtAgencia.setText(f.getAgencia());
        cbTipoFomento.setValue(f.getTipoFomento());
        txtValorTotal.setText(f.getValorTotal());
        dpDataInicio.setValue(f.getDataInicio());
        dpDataFim.setValue(f.getDataFim());

        Usuario user = DadosService.getInstance().getUsuarioLogado();
        boolean podeEditar = (user == null || user.isCoordenador());
        btnAtualizar.setDisable(!podeEditar);
        btnInativar.setDisable(!podeEditar || "Inativo".equalsIgnoreCase(f.getSituacao()));
        btnSalvar.setDisable(true);
    }

    /**
     * UC04: Cadastrar Financiamento
     */
    @FXML
    public void salvar() {
        Usuario user = DadosService.getInstance().getUsuarioLogado();
        if (user != null && user.isGestorAdministrativo()) {
            exibirMensagem(Alert.AlertType.WARNING, "Acesso Negado", "Apenas Coordenadores podem registrar financiamentos.");
            return;
        }

        Projeto proj = cbProjeto.getValue();
        if (proj == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "Selecione o projeto que receberá o financiamento.");
            return;
        }

        String situacao = cbSituacao.getValue() != null ? cbSituacao.getValue() : "Ativo";

        // RN-07 / Excecao 3a: Excedente de contratos (Max 1 financiamento por projeto)
        Financiamento existente = DadosService.getInstance().buscarFinanciamentoPorProjeto(proj.getIdProjeto());
        if (existente != null && "Ativo".equalsIgnoreCase(existente.getSituacao()) && "Ativo".equalsIgnoreCase(situacao)) {
            exibirMensagem(Alert.AlertType.ERROR, "Excedente de Contratos (RN-07)",
                    "O projeto selecionado já possui um financiamento ativo! Um projeto pode ter, no máximo, 1 financiamento associado.");
            return;
        }

        // Se inativo, nao exige dados financeiros (UC04 Fluxo Alternativo 2a)
        if ("Inativo".equalsIgnoreCase(situacao)) {
            Financiamento inativo = new Financiamento(proj.getIdProjeto(), "Nenhum", "Sem fomento", "0,00", LocalDate.now(), LocalDate.now(), "Inativo");
            if (!inativo.cadastrarFinanciamento()) {
                exibirMensagem(Alert.AlertType.ERROR, "Erro", "Não foi possível registrar o financiamento no banco de dados.");
                return;
            }
            tabelaFinanciamentos.refresh();
            limpar();
            exibirMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Projeto registrado como sem financiamento (Inativo).");
            return;
        }

        // Se ativo, valida campos obrigatorios
        if (!validarCamposAtivos()) return;

        Financiamento novo = new Financiamento(
                proj.getIdProjeto(),
                txtAgencia.getText().trim(),
                cbTipoFomento.getValue(),
                txtValorTotal.getText().trim(),
                dpDataInicio.getValue(),
                dpDataFim.getValue(),
                "Ativo"
        );

        if (!novo.cadastrarFinanciamento()) {
            exibirMensagem(Alert.AlertType.ERROR, "Erro", "Não foi possível salvar o financiamento no banco de dados.");
            return;
        }
        tabelaFinanciamentos.refresh();
        limpar();
        exibirMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Financiamento cadastrado com sucesso (UC04)!");
    }

    /**
     * Atualizar dados do Financiamento
     */
    @FXML
    public void atualizar() {
        if (financiamentoSelecionado == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Atenção", "Selecione um financiamento na tabela para atualizar.");
            return;
        }

        if ("Ativo".equalsIgnoreCase(cbSituacao.getValue())) {
            if (!validarCamposAtivos()) return;
        }

        financiamentoSelecionado.atualizarFinanciamento(
                txtAgencia.getText().trim(),
                cbTipoFomento.getValue(),
                txtValorTotal.getText().trim(),
                dpDataInicio.getValue(),
                dpDataFim.getValue(),
                cbSituacao.getValue()
        );

            if (!DadosService.getInstance().salvarFinanciamento(financiamentoSelecionado)) {
                exibirMensagem(Alert.AlertType.ERROR, "Erro", "Não foi possível atualizar o financiamento no banco de dados.");
                return;
            }

        tabelaFinanciamentos.refresh();
        limpar();
        exibirMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Dados do financiamento atualizados!");
    }

    /**
     * RN-09 & UC04: Inativar Financiamento (sem exclusao fisica)
     */
    @FXML
    public void inativarFinanciamento() {
        if (financiamentoSelecionado == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Atenção", "Selecione um financiamento para inativar.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Inativar Financiamento (RN-09)");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja realmente inativar o financiamento da agência " +
                financiamentoSelecionado.getAgencia() + " para o projeto \"" + financiamentoSelecionado.getTituloProjeto() + "\"?");

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            financiamentoSelecionado.inativarFinanciamento();
            if (!DadosService.getInstance().salvarFinanciamento(financiamentoSelecionado)) {
                exibirMensagem(Alert.AlertType.ERROR, "Erro", "Não foi possível inativar o financiamento no banco de dados.");
                return;
            }
            tabelaFinanciamentos.refresh();
            limpar();
            exibirMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Financiamento inativado no sistema mantendo o registro (RN-09)!");
        }
    }

    @FXML
    public void limpar() {
        financiamentoSelecionado = null;
        cbProjeto.setValue(null);
        cbSituacao.setValue("Ativo");
        txtAgencia.clear();
        cbTipoFomento.setValue(null);
        txtValorTotal.clear();
        dpDataInicio.setValue(LocalDate.now());
        dpDataFim.setValue(LocalDate.now().plusYears(1));
        tabelaFinanciamentos.getSelectionModel().clearSelection();

        Usuario user = DadosService.getInstance().getUsuarioLogado();
        boolean podeEditar = (user == null || user.isCoordenador());
        btnSalvar.setDisable(!podeEditar);
        btnAtualizar.setDisable(true);
        btnInativar.setDisable(true);
    }

    private boolean validarCamposAtivos() {
        if (txtAgencia.getText().trim().isEmpty()) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "A Agência Financiadora é obrigatória.");
            return false;
        }

        if (cbTipoFomento.getValue() == null) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "Selecione o Tipo de Fomento.");
            return false;
        }

        if (txtValorTotal.getText().trim().isEmpty()) {
            exibirMensagem(Alert.AlertType.WARNING, "Validação", "Informe o Valor Total concedido.");
            return false;
        }

        // RN-06 / Excecao 4a: As datas de inicio e termino sao obrigatorias (NOT NULL)
        if (dpDataInicio.getValue() == null || dpDataFim.getValue() == null) {
            exibirMensagem(Alert.AlertType.ERROR, "Erro de Validação (RN-06)",
                    "As datas de início e término da vigência são estritamente obrigatórias para contratos ativos.");
            return false;
        }

        if (dpDataFim.getValue().isBefore(dpDataInicio.getValue())) {
            exibirMensagem(Alert.AlertType.ERROR, "Erro de Validação", "A data final de vigência não pode ser anterior à data de início.");
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
