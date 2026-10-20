package com.trabalho;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Carrega o arquivo Login.fxml e define o tamanho da janela (400x450)
        scene = new Scene(loadFXML("Login"), 400, 450);
        
        // Configurações da janela
        stage.setTitle("SIGPesq - Acesso ao Sistema");
        stage.setScene(scene);
        stage.setResizable(false); // Impede que o usuário redimensione a tela de login
        stage.show();
    }

    // Método para trocar de tela, ajustando o tamanho da janela
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));

        // Ajusta o tamanho da janela conforme a tela carregada
        if (fxml.equals("menuPrincipal")) {
            primaryStage.setResizable(true);
            primaryStage.setWidth(900);
            primaryStage.setHeight(600);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("SIGPesq - Menu Principal");
        } else if (fxml.equals("Login")) {
            primaryStage.setResizable(false);
            primaryStage.setWidth(400);
            primaryStage.setHeight(450);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("SIGPesq - Acesso ao Sistema");
        }
    }

    // Método auxiliar que busca o arquivo FXML na pasta resources
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(); // Comando nativo do JavaFX para iniciar a interface
    }
}