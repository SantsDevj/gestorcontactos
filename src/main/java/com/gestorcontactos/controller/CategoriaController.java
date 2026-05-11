package controller;

import javafx.fxml.*;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Categoria;
import service.CategoriaService;
import service.ServiceLocator;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CategoriaController implements Initializable {

    @FXML private TextField campoNome;
    @FXML private TextField campoDescricao;
    @FXML private Label lblErro;
    @FXML private VBox listaCategorias;

    private CategoriaService categoriaService;

    // Cores para os ícones de categoria (rotação)
    private static final String[] CORES = {
        "#7B41B3", "#C05621", "#2C7A7B",
        "#2B6CB0", "#276749", "#B7791F"
    };
    private int corIndex = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoriaService = ServiceLocator.getInstancia().getCategoriaService();
        carregarCategorias();
    }

    private void carregarCategorias() {
        listaCategorias.getChildren().clear();
        List<Categoria> cats = categoriaService.listarCategorias();

        if (cats.isEmpty()) {
            Label vazio = new Label("Nenhuma categoria criada ainda.");
            vazio.setStyle("-fx-text-fill: #74777F; -fx-font-size: 13px;");
            vazio.setStyle("-fx-font-style: italic; -fx-text-fill: #74777F;");
            listaCategorias.getChildren().add(vazio);
            return;
        }

        corIndex = 0;
        for (Categoria cat : cats) {
            HBox linha = criarLinhaCategoria(cat);
            listaCategorias.getChildren().add(linha);
        }
    }

    private HBox criarLinhaCategoria(Categoria cat) {
        // Círculo colorido com ícone
        String cor = CORES[corIndex % CORES.length];
        corIndex++;

        StackPane circulo = new StackPane();
        circulo.getStyleClass().add("category-icon");
        circulo.setStyle("-fx-background-color: " + cor + "; " +
                         "-fx-background-radius: 50%; " +
                         "-fx-min-width: 40px; -fx-min-height: 40px; " +
                         "-fx-max-width: 40px; -fx-max-height: 40px;");
        Label iconeLabel = new Label("📁");
        iconeLabel.setStyle("-fx-font-size: 16px;");
        circulo.getChildren().add(iconeLabel);

        // Nome e descrição
        Label nome = new Label(cat.getNome());
        nome.getStyleClass().add("category-name");

        Label desc = new Label(cat.getDescricao() != null
            ? cat.getDescricao() : "");
        desc.getStyleClass().add("category-desc");

        VBox info = new VBox(2, nome, desc);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Botão eliminar
        Button btnDel = new Button("🗑");
        btnDel.getStyleClass().add("btn-icon-danger");
        btnDel.setOnAction(e -> eliminarCategoria(cat));

        HBox linha = new HBox(12, circulo, info, btnDel);
        linha.getStyleClass().add("category-item-row");
        linha.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return linha;
    }

    @FXML
    private void criarCategoria() {
        esconderErro();
        try {
            Categoria nova = new Categoria(
                campoNome.getText().trim(),
                campoDescricao.getText().trim());
            categoriaService.criarCategoria(nova);
            campoNome.clear();
            campoDescricao.clear();
            carregarCategorias();
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void eliminarCategoria(Categoria cat) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar categoria");
        confirm.setHeaderText(null);
        confirm.setContentText(
            "Eliminar a categoria \"" + cat.getNome() + "\"?\n" +
            "Os contactos associados perderão esta categoria.");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                categoriaService.eliminarCategoria(cat.getId());
                carregarCategorias();
            }
        });
    }

    @FXML
    private void fechar() {
        Object userData = campoNome.getScene().getUserData();
        if (userData instanceof PrincipalController ctrl) {
            ctrl.fecharOverlay();
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