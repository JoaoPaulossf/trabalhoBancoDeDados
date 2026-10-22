package com.trabalho;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        scene = new Scene(loadFXML("Login"), 420, 500);
        
        stage.setTitle("SIGPesq - Acesso ao Sistema");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));

        if (fxml.equalsIgnoreCase("MenuPrincipal")) {
            primaryStage.setResizable(true);
            primaryStage.setWidth(980);
            primaryStage.setHeight(650);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("SIGPesq - Sistema de Gerenciamento de Projetos");
        } else if (fxml.equalsIgnoreCase("Login")) {
            primaryStage.setResizable(false);
            primaryStage.setWidth(420);
            primaryStage.setHeight(500);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("SIGPesq - Acesso ao Sistema");
        }
    }

    public static Parent loadFXML(String fxml) throws IOException {
        URL resource = App.class.getResource(fxml + ".fxml");
        if (resource == null) {
            String alternativo = Character.toLowerCase(fxml.charAt(0)) + fxml.substring(1);
            resource = App.class.getResource(alternativo + ".fxml");
        }
        if (resource == null) {
            String alternativo = Character.toUpperCase(fxml.charAt(0)) + fxml.substring(1);
            resource = App.class.getResource(alternativo + ".fxml");
        }
        if (resource == null) {
            throw new IOException("Arquivo FXML não encontrado: " + fxml + ".fxml");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}