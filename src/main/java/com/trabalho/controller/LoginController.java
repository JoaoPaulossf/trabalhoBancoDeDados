package com.trabalho.controller;

import com.trabalho.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblMensagemErro;

    @FXML
    public void fazerLogin(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText().trim();

        // Validação básica: verifica se os campos foram preenchidos
        if (email.isEmpty() || senha.isEmpty()) {
            lblMensagemErro.setText("Por favor, preencha todos os campos.");
            return;
        }

        try {
            // Navega para a tela principal
            App.setRoot("menuPrincipal");
        } catch (IOException e) {
            lblMensagemErro.setText("Erro ao carregar a tela principal.");
            e.printStackTrace();
        }
    }
}
