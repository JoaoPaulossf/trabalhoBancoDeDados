package com.trabalho.controller;

import com.trabalho.App;
import com.trabalho.model.Usuario;
import com.trabalho.service.DadosService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

/**
 * Controlador da Tela Principal e Navegacao do SIGPesq.
 * Aplica controle de acesso por perfis de usuario (RNF-02, AT01, AT02).
 */
public class MenuPrincipalController {

    @FXML private Label lblUsuarioInfo;
    @FXML private Label lblNivelAcesso;
    @FXML private Button btnProjetos;
    @FXML private Button btnParticipantes;
    @FXML private Button btnFinanciamentos;
    @FXML private Button btnProducoes;
    @FXML private Button btnConsultas;
    
    @FXML private StackPane painelCentral; 

    @FXML
    public void initialize() {
        try {
            Usuario usuario = DadosService.getInstance().getUsuarioLogado();

            if (usuario != null) {
                if (lblUsuarioInfo != null) {
                    lblUsuarioInfo.setText(usuario.getNome());
                }
                if (lblNivelAcesso != null) {
                    lblNivelAcesso.setText(usuario.getNivelAcesso());
                }

                // RNF-02: Controle de acesso por perfil
                if (usuario.isGestorAdministrativo()) {
                    if (btnProjetos != null) btnProjetos.setStyle("-fx-opacity: 0.85;");
                    if (btnParticipantes != null) btnParticipantes.setStyle("-fx-opacity: 0.85;");
                    if (btnFinanciamentos != null) btnFinanciamentos.setStyle("-fx-opacity: 0.85;");
                    if (btnProducoes != null) btnProducoes.setStyle("-fx-opacity: 0.85;");
                    
                    // Gestor Administrativo inicia na tela de Consultas
                    Platform.runLater(() -> abrirTelaConsultas(null));
                } else {
                    // Coordenador de Projeto: abre Projetos por padrao
                    Platform.runLater(() -> abrirTelaProjetos(null));
                }
            } else {
                if (lblUsuarioInfo != null) lblUsuarioInfo.setText("Usuário Anônimo");
                if (lblNivelAcesso != null) lblNivelAcesso.setText("Sem Perfil");
                Platform.runLater(() -> abrirTelaProjetos(null));
            }
        } catch (Exception e) {
            System.err.println("Aviso na inicialização do menu principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarFormulario(String nomeArquivoFxml) {
        if (painelCentral == null) {
            System.err.println("painelCentral é nulo! Verifique o fx:id no FXML.");
            return;
        }

        try {
            URL resource = App.class.getResource(nomeArquivoFxml + ".fxml");
            if (resource == null) {
                String alternativo = Character.toLowerCase(nomeArquivoFxml.charAt(0)) + nomeArquivoFxml.substring(1);
                resource = App.class.getResource(alternativo + ".fxml");
            }
            if (resource == null) {
                String alternativo = Character.toUpperCase(nomeArquivoFxml.charAt(0)) + nomeArquivoFxml.substring(1);
                resource = App.class.getResource(alternativo + ".fxml");
            }

            if (resource == null) {
                System.err.println("Arquivo FXML não encontrado: " + nomeArquivoFxml + ".fxml");
                painelCentral.getChildren().clear();
                Label lblErro = new Label("Formulário '" + nomeArquivoFxml + "' não encontrado.");
                painelCentral.getChildren().add(lblErro);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Node formulario = loader.load();
            painelCentral.getChildren().clear();
            painelCentral.getChildren().add(formulario);
        } catch (IOException e) {
            System.err.println("Erro ao carregar o formulário: " + nomeArquivoFxml);
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Falha ao carregar tela");
            alert.setContentText("Não foi possível carregar o formulário: " + nomeArquivoFxml + "\nMotivo: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void abrirTelaProjetos(ActionEvent event) {
        carregarFormulario("Projetos");
    }

    @FXML
    public void abrirTelaParticipantes(ActionEvent event) {
        carregarFormulario("Participantes");
    }

    @FXML
    public void abrirTelaFinanciamentos(ActionEvent event) {
        carregarFormulario("Financiamentos");
    }

    @FXML
    public void abrirTelaProducoes(ActionEvent event) {
        carregarFormulario("Producoes");
    }

    @FXML
    public void abrirTelaConsultas(ActionEvent event) {
        carregarFormulario("Consultas");
    }

    @FXML
    public void fazerLogout(ActionEvent event) {
        DadosService.getInstance().logout();
        try {
            App.setRoot("Login");
        } catch (IOException e) {
            System.err.println("Erro ao voltar para a tela de login.");
            e.printStackTrace();
        }
    }
}