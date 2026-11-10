package com.trabalho.controller;

import com.trabalho.App;
import com.trabalho.model.Usuario;
import com.trabalho.service.DadosService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


/**
 * Controlador de Autenticacao de Usuario (UC01 / RNF-02).
 */
public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblMensagemErro;

    @FXML
    public void initialize() {
        String erroBanco = DadosService.getInstance().getErroInicializacao();
        if (erroBanco != null) {
            lblMensagemErro.setText("Banco indisponível. Verifique MySQL/configuração: " + erroBanco);
        }
    }

    @FXML
    public void preencherCoordenador() {
        preencherPerfilDemo("carlos.eduardo@ufes.br");
    }

    @FXML
    public void preencherGestor() {
        preencherPerfilDemo("gestor@ufes.br");
    }

    private void preencherPerfilDemo(String email) {
        txtEmail.setText(email);
        txtSenha.clear();
        lblMensagemErro.setText("");
        txtSenha.requestFocus();
    }

    @FXML
    public void fazerLogin() {
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            lblMensagemErro.setText("Por favor, preencha o e-mail institucional e a senha.");
            return;
        }

        // UC01 - 3. Valida credenciais e identifica perfil
        Usuario usuario;
        try {
            usuario = DadosService.getInstance().autenticar(email, senha);
        } catch (IllegalStateException e) {
            lblMensagemErro.setText(e.getMessage());
            return;
        }

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
