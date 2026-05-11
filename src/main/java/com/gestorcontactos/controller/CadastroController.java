package com.gestorcontactos.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.gestorcontactos.service.AuthService;
import com.gestorcontactos.service.ServiceLocator;

import java.net.URL;
import java.util.ResourceBundle;

public class CadastroController implements Initializable {

    @FXML private HBox rootPane;
    @FXML private TextField campoNome;
    @FXML private TextField campoUsername;
    @FXML private PasswordField campoSenha;
    @FXML private TextField campoSenhaVisivel;
    @FXML private PasswordField campoConfirmar;
    @FXML private Button btnMostrarSenha;
    @FXML private Label lblErro;

    private AuthService authService;
    private boolean senhaVisivel = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        authService = ServiceLocator.getInstancia().getAuthService();
        campoSenhaVisivel.textProperty().bindBidirectional(
            campoSenha.textProperty());

        FadeTransition fade = new FadeTransition(Duration.millis(400), rootPane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    @FXML
    private void toggleSenha() {
        senhaVisivel = !senhaVisivel;
        if (senhaVisivel) {
            campoSenhaVisivel.setVisible(true);
            campoSenhaVisivel.setManaged(true);
            campoSenha.setVisible(false);
            campoSenha.setManaged(false);
            btnMostrarSenha.setText("🙈");
        } else {
            campoSenha.setVisible(true);
            campoSenha.setManaged(true);
            campoSenhaVisivel.setVisible(false);
            campoSenhaVisivel.setManaged(false);
            btnMostrarSenha.setText("👁");
        }
    }

    @FXML
    private void criarConta() {
        esconderErro();
        try {
            authService.registar(
                campoNome.getText().trim(),
                campoUsername.getText().trim(),
                campoSenha.getText(),
                campoConfirmar.getText()
            );
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Conta criada");
            alert.setHeaderText(null);
            alert.setContentText("Conta criada com sucesso! Podes fazer login.");
            alert.showAndWait();
            irParaLogin();
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void irParaLogin() {
        navegarPara("/fxml/login.fxml");
    }

    private void navegarPara(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm());
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FadeTransition fade = new FadeTransition(Duration.millis(300), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            stage.setScene(scene);
            fade.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarErro(String msg) {
        lblErro.setText(msg);
        lblErro.setVisible(true);
        lblErro.setManaged(true);
    }

    private void esconderErro() {
        lblErro.setVisible(false);
        lblErro.setManaged(false);
    }
}