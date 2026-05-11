package controller;

import javafx.animation.*;
import javafx.fxml.*;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Contacto;
import model.Utilizador;
import service.CategoriaService;
import service.ContactoService;
import service.ServiceLocator;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    @FXML private StackPane rootStack;
    @FXML private HBox mainLayout;
    @FXML private Label lblIniciais;
    @FXML private Label lblNomeUtilizador;
    @FXML private TextField campoPesquisa;
    @FXML private FlowPane gridContactos;
    @FXML private StackPane overlay;
    @FXML private HBox overlayContent;

    private ContactoService contactoService;
    private CategoriaService categoriaService;
    private Utilizador utilizadorActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contactoService  = ServiceLocator.getInstancia().getContactoService();
        categoriaService = ServiceLocator.getInstancia().getCategoriaService();

        // Pesquisa em tempo real ao escrever
        campoPesquisa.textProperty().addListener((obs, antigo, novo) -> {
            if (utilizadorActual != null) pesquisarContactos(novo);
        });

        // Clique no backdrop fecha o painel
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) fecharOverlay();
        });
    }

    // Chamado pelo LoginController após login bem-sucedido
    public void inicializar(Utilizador utilizador) {
        this.utilizadorActual = utilizador;

        String iniciais = obterIniciais(utilizador.getNome());
        lblIniciais.setText(iniciais);
        lblNomeUtilizador.setText(utilizador.getNome());

        carregarContactos();

        FadeTransition fade = new FadeTransition(Duration.millis(400), rootStack);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        rootStack.sceneProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) novo.setUserData(this);
        });
    }

    private void carregarContactos() {
        List<Contacto> lista =
            contactoService.listarContactos(utilizadorActual.getId());
        renderizarCartoes(lista);
    }

    private void pesquisarContactos(String termo) {
        List<Contacto> lista =
            contactoService.pesquisarContacto(termo, utilizadorActual.getId());
        renderizarCartoes(lista);
    }

    private void renderizarCartoes(List<Contacto> lista) {
        gridContactos.getChildren().clear();
        for (Contacto c : lista) {
            VBox cartao = criarCartaoContacto(c);
            gridContactos.getChildren().add(cartao);
        }
    }

    private VBox criarCartaoContacto(Contacto contacto) {
        // Avatar com iniciais
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("contact-avatar");
        Label lblAvatar = new Label(obterIniciais(contacto.getNomeCompleto()));
        lblAvatar.getStyleClass().add("contact-avatar-text");
        avatar.getChildren().add(lblAvatar);

        // Nome e categoria
        Label nome = new Label(contacto.getNomeCompleto());
        nome.getStyleClass().add("contact-name");

        String cat = contacto.getCategoria() != null
            ? contacto.getCategoria().getNome() : "";
        Label categoriaLabel = new Label(cat);
        categoriaLabel.getStyleClass().add("contact-role");

        VBox infoNome = new VBox(2, nome, categoriaLabel);

        HBox cabecalho = new HBox(12, avatar, infoNome);
        cabecalho.setStyle("-fx-alignment: center-left;");

        // Linha separadora
        Region divider = new Region();
        divider.getStyleClass().add("card-divider");
        divider.setMaxWidth(Double.MAX_VALUE);

        // Email
        Label email = new Label("✉  " + (contacto.getEmail() != null
            ? contacto.getEmail() : "—"));
        email.getStyleClass().add("contact-info-row");
        email.setMaxWidth(260);

        // Endereço
        Label endereco = new Label("📍  " + (contacto.getEndereco() != null
            ? contacto.getEndereco() : "—"));
        endereco.getStyleClass().add("contact-info-row");

        // Tag da categoria
        HBox tags = new HBox(6);
        if (contacto.getCategoria() != null) {
            Label tag = new Label(
                contacto.getCategoria().getNome().toUpperCase());
            tag.getStyleClass().add("tag-pill-active");
            tags.getChildren().add(tag);
        }

        VBox cartao = new VBox(12,
            cabecalho, divider, email, endereco, tags);
        cartao.getStyleClass().add("contact-card");

        // Clique no cartão abre detalhes
        cartao.setOnMouseClicked(e -> abrirDetalhes(contacto));

        return cartao;
    }

    @FXML
    private void abrirFormNovoContacto() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/formContacto.fxml"));
            Parent painel = loader.load();

            FormContactoController ctrl = loader.getController();
            ctrl.inicializarNovo(utilizadorActual, this::carregarContactos);

            mostrarPainelLateral(painel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirDetalhes(Contacto contacto) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/detalhesContacto.fxml"));
            Parent painel = loader.load();

            DetalhesContactoController ctrl = loader.getController();
            ctrl.inicializar(contacto, this::carregarContactos,
                this::abrirFormEdicao);

            mostrarPainelLateral(painel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abrirFormEdicao(Contacto contacto) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/formContacto.fxml"));
            Parent painel = loader.load();

            FormContactoController ctrl = loader.getController();
            ctrl.inicializarEdicao(contacto, utilizadorActual,
                this::carregarContactos);

            mostrarPainelLateral(painel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirCategorias() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/categoria.fxml"));
            Parent modal = loader.load();

            mostrarModal(modal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarPainelLateral(Parent painel) {
        overlayContent.getChildren().clear();
        overlayContent.getChildren().add(painel);
        overlayContent.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        overlay.setVisible(true);
        overlay.setManaged(true);

        // Animar backdrop
        FadeTransition fadeBack = new FadeTransition(
            Duration.millis(200), overlay);
        fadeBack.setFromValue(0);
        fadeBack.setToValue(1);
        fadeBack.play();

        // Animar painel a deslizar da direita
        painel.setTranslateX(440);
        TranslateTransition slide = new TranslateTransition(
            Duration.millis(300), painel);
        slide.setToX(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        slide.play();
    }

    private void mostrarModal(Parent modal) {
        overlayContent.getChildren().clear();
        overlayContent.getChildren().add(modal);
        overlayContent.setAlignment(javafx.geometry.Pos.CENTER);

        overlay.setVisible(true);
        overlay.setManaged(true);

        FadeTransition fade = new FadeTransition(Duration.millis(200), overlay);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        ScaleTransition scale = new ScaleTransition(
            Duration.millis(250), modal);
        scale.setFromX(0.88);
        scale.setFromY(0.88);
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.EASE_OUT);
        scale.play();
    }

    public void fecharOverlay() {
        if (!overlayContent.getChildren().isEmpty()) {
            Parent conteudo = (Parent) overlayContent.getChildren().get(0);

            FadeTransition fade = new FadeTransition(
                Duration.millis(200), overlay);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setOnFinished(e -> {
                overlay.setVisible(false);
                overlay.setManaged(false);
                overlayContent.getChildren().clear();
            });
            fade.play();
        }
    }

    @FXML
    private void navContactos() {
        carregarContactos();
    }

    @FXML
    private void filtrarContactos() {
        // Futuro: abrir painel de filtros
    }

    @FXML
    private void fazerLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Sair");
        confirm.setHeaderText(null);
        confirm.setContentText("Tens a certeza que queres sair?");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    Parent root = FXMLLoader.load(
                        getClass().getResource("/fxml/login.fxml"));
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(
                        getClass().getResource("/css/styles.css")
                            .toExternalForm());
                    Stage stage = (Stage) rootStack.getScene().getWindow();
                    stage.setScene(scene);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String obterIniciais(String nome) {
        if (nome == null || nome.isEmpty()) return "?";
        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 1)
            return String.valueOf(partes[0].charAt(0)).toUpperCase();
        return (String.valueOf(partes[0].charAt(0)) +
                String.valueOf(partes[partes.length - 1].charAt(0)))
                .toUpperCase();
    }
}