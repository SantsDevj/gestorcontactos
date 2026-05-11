package controller;

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
import service.AuthService;
import service.ServiceLocator;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private HBox rootPane;
    @FXML private TextField campoUsername;
    @FXML private PasswordField campoSenha;
    @FXML private TextField campoSenhaVisivel;
    @FXML private Button btnMostrarSenha;
    @FXML private CheckBox chkLembrar;
    @FXML private Label lblErro;

    private AuthService authService;
    private boolean senhaVisivel = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        authService = ServiceLocator.getInstancia().getAuthService();

        // Sincroniza PasswordField e TextField
        campoSenhaVisivel.textProperty().bindBidirectional(
            campoSenha.textProperty());

        // Fade de entrada
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
    private void fazerLogin() {
        String username = campoUsername.getText().trim();
        String senha    = campoSenha.getText();

        esconderErro();

        try {
            boolean sucesso = authService.login(username, senha);
            if (sucesso) {
                navegarParaPrincipal();
            } else {
                mostrarErro("Username ou senha incorrectos. Tente novamente.");
            }
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void irParaCadastro() {
        navegarPara("/fxml/cadastro.fxml");
    }

    @FXML
    private void esqueceuSenha() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recuperar senha");
        alert.setHeaderText(null);
        alert.setContentText("Funcionalidade de recuperação de senha não disponível nesta versão.");
        alert.showAndWait();
    }

    private void navegarParaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/principal.fxml"));
            Parent root = loader.load();

            PrincipalController ctrl = loader.getController();
            ctrl.inicializar(authService.getUtilizadorActual());

            Scene scene = new Scene(root);
            scene.setUserData(ctrl);
            scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm());

            Stage stage = (Stage) rootPane.getScene().getWindow();

            FadeTransition fade = new FadeTransition(Duration.millis(300), root);
            fade.setFromValue(0);
            fade.setToValue(1);

            stage.setScene(scene);
            fade.play();
        } catch (Exception e) {
            mostrarErro("Erro ao abrir o sistema.");
            e.printStackTrace();
        }
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