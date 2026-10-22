package com.trabalho.controller;

import com.trabalho.App;
import com.trabalho.model.Usuario;
import com.trabalho.service.DadosService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controlador de Autenticacao de Usuario (UC01 / RNF-02).
 */
public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblMensagemErro;

    @FXML
    public void initialize() {
        // Sugestao inicial para facilitar demonstracao
        txtEmail.setText("carlos.eduardo@ufes.br");
        txtSenha.setText("123456");
    }

    @FXML
    public void preencherCoordenador(ActionEvent event) {
        txtEmail.setText("carlos.eduardo@ufes.br");
        txtSenha.setText("123456");
        lblMensagemErro.setText("");
    }

    @FXML
    public void preencherGestor(ActionEvent event) {
        txtEmail.setText("gestor@ufes.br");
        txtSenha.setText("123456");
        lblMensagemErro.setText("");
    }

    @FXML
    public void fazerLogin(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            lblMensagemErro.setText("Por favor, preencha o e-mail institucional e a senha.");
            return;
        }

        // UC01 - 3. Valida credenciais e identifica perfil
        Usuario usuario = DadosService.getInstance().autenticar(email, senha);

        if (usuario == null) {
            // UC01 - 3a. Credenciais invalidas
            lblMensagemErro.setText("Falha na autenticação: E-mail ou senha inválidos.");
            return;
        }

        try {
            // Concede acesso e redireciona (UC01 - passo 5)
            App.setRoot("MenuPrincipal");
        } catch (Exception e) {
            String causa = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            lblMensagemErro.setText("Erro ao carregar a tela principal: " + causa);
            e.printStackTrace();
        }
    }
}
