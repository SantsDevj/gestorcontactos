package controller;

import javafx.fxml.*;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.*;
import service.ContactoService;
import service.ServiceLocator;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class DetalhesContactoController implements Initializable {

    @FXML private VBox painelRoot;
    @FXML private Label lblIniciais;
    @FXML private Label lblNome;
    @FXML private Label lblCategoriaBadge;
    @FXML private Label lblEmail;
    @FXML private Label lblEndereco;
    @FXML private Label lblDataNasc;
    @FXML private VBox containerTelefones;

    private ContactoService contactoService;
    private Contacto contactoActual;
    private Runnable aoEliminar;
    private Consumer<Contacto> aoEditar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contactoService = ServiceLocator.getInstancia().getContactoService();
    }

    public void inicializar(Contacto contacto, Runnable aoEliminar,
                            Consumer<Contacto> aoEditar) {
        this.contactoActual = contacto;
        this.aoEliminar     = aoEliminar;
        this.aoEditar       = aoEditar;

        preencherDados(contacto);
    }

    private void preencherDados(Contacto c) {
        lblIniciais.setText(obterIniciais(c.getNomeCompleto()));
        lblNome.setText(c.getNomeCompleto());

        if (c.getCategoria() != null) {
            lblCategoriaBadge.setText(c.getCategoria().getNome());
            lblCategoriaBadge.setVisible(true);
            lblCategoriaBadge.setManaged(true);
        } else {
            lblCategoriaBadge.setVisible(false);
            lblCategoriaBadge.setManaged(false);
        }

        lblEmail.setText(c.getEmail() != null && !c.getEmail().isEmpty()
            ? c.getEmail() : "—");
        lblEndereco.setText(c.getEndereco() != null && !c.getEndereco().isEmpty()
            ? c.getEndereco() : "—");

        if (c.getDataNasc() != null) {
            lblDataNasc.setText(c.getDataNasc()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            lblDataNasc.setText("—");
        }

        // Telefones — carregados do DAO via service
        containerTelefones.getChildren().clear();
        List<Telefone> telefones = c.getTelefones();
        if (telefones == null || telefones.isEmpty()) {
            Label semTel = new Label("Nenhum telefone registado.");
            semTel.getStyleClass().add("contact-info-row");
            containerTelefones.getChildren().add(semTel);
        } else {
            for (Telefone t : telefones) {
                HBox linha = criarLinhaTelefone(t);
                containerTelefones.getChildren().add(linha);
            }
        }
    }

    private HBox criarLinhaTelefone(Telefone telefone) {
        String icone = "📱";
        if (telefone.getTipo() == TipoTelefone.FIXO)      icone = "📞";
        if (telefone.getTipo() == TipoTelefone.TRABALHO)  icone = "💼";

        Label icon = new Label(icone);
        icon.setStyle("-fx-text-fill: #7B41B3; -fx-font-size: 16px;");

        Label numero = new Label(telefone.getNumero());
        numero.getStyleClass().add("details-info-text");
        HBox.setHgrow(numero, Priority.ALWAYS);

        String tipoStr = telefone.getTipo() != null
            ? capitalize(telefone.getTipo().name()) : "Móvel";
        Label badge = new Label(tipoStr);
        badge.getStyleClass().add("phone-type-badge");

        HBox linha = new HBox(12, icon, numero, badge);
        linha.getStyleClass().add("details-info-box");
        linha.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return linha;
    }

    @FXML
    private void editarContacto() {
        fechar();
        if (aoEditar != null) aoEditar.accept(contactoActual);
    }

    @FXML
    private void eliminarContacto() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar contacto");
        confirm.setHeaderText(null);
        confirm.setContentText(
            "Tens a certeza que queres eliminar \"" +
            contactoActual.getNomeCompleto() + "\"?");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                contactoService.eliminarContacto(contactoActual.getId());
                if (aoEliminar != null) aoEliminar.run();
                fechar();
            }
        });
    }

    @FXML
    private void fechar() {
        Object userData = painelRoot.getScene().getUserData();
        if (userData instanceof PrincipalController ctrl) {
            ctrl.fecharOverlay();
        }
    }

    private String obterIniciais(String nome) {
        if (nome == null || nome.isEmpty()) return "?";
        String[] p = nome.trim().split("\\s+");
        if (p.length == 1)
            return String.valueOf(p[0].charAt(0)).toUpperCase();
        return (String.valueOf(p[0].charAt(0)) +
                String.valueOf(p[p.length - 1].charAt(0))).toUpperCase();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}