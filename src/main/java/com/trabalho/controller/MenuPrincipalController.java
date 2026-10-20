package com.trabalho.controller;

import com.trabalho.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MenuPrincipalController {

    @FXML private Label lblUsuarioInfo;
    @FXML private Button btnProjetos;
    @FXML private Button btnParticipantes;
    @FXML private Button btnFinanciamentos;
    @FXML private Button btnProducoes;
    @FXML private Button btnConsultas;
    
    // Este é o painel onde os formulários serão carregados depois
    @FXML private StackPane painelCentral; 

    @FXML
    public void initialize() {
        // No futuro, a lógica de ocultar botões do Gestor Administrativo ficará aqui.
    }

    /**
     * Método auxiliar que carrega um arquivo FXML e exibe no painel central.
     */
    private void carregarFormulario(String nomeArquivoFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(nomeArquivoFxml + ".fxml"));
            Node formulario = loader.load();
            painelCentral.getChildren().clear();
            painelCentral.getChildren().add(formulario);
        } catch (IOException e) {
            System.out.println("Erro ao carregar o formulário: " + nomeArquivoFxml);
            e.printStackTrace();
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
        System.out.println("A abrir o painel de Consultas...");
    }

    @FXML
    public void fazerLogout(ActionEvent event) {
        try {
            App.setRoot("Login");
        } catch (IOException e) {
            System.out.println("Erro ao voltar para a tela de login.");
            e.printStackTrace();
        }
    }
}
