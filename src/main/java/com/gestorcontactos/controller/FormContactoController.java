package controller;

import javafx.fxml.*;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.*;
import service.CategoriaService;
import service.ContactoService;
import service.ServiceLocator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FormContactoController implements Initializable {

    @FXML private VBox painelRoot;
    @FXML private Label lblTitulo;
    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private TextField campoEndereco;
    @FXML private DatePicker campoDataNasc;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private HBox containerPills;
    @FXML private VBox containerTelefones;
    @FXML private Label erroNome;

    private ContactoService contactoService;
    private CategoriaService categoriaService;
    private Utilizador utilizadorActual;
    private Contacto contactoEmEdicao;
    private boolean modoEdicao = false;
    private Runnable aoGuardar;
    private List<HBox> linhasTelefone = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contactoService  = ServiceLocator.getInstancia().getContactoService();
        categoriaService = ServiceLocator.getInstancia().getCategoriaService();

        carregarCategorias();
        adicionarLinhaTelefone(); // começa com uma linha de telefone
    }

    public void inicializarNovo(Utilizador utilizador, Runnable aoGuardar) {
        this.utilizadorActual = utilizador;
        this.aoGuardar = aoGuardar;
        this.modoEdicao = false;
        lblTitulo.setText("Novo Contacto");
    }

    public void inicializarEdicao(Contacto contacto, Utilizador utilizador,
                                   Runnable aoGuardar) {
        this.utilizadorActual = utilizador;
        this.contactoEmEdicao = contacto;
        this.aoGuardar = aoGuardar;
        this.modoEdicao = true;
        lblTitulo.setText("Editar Contacto");

        // Preencher campos com dados existentes
        campoNome.setText(contacto.getNomeCompleto());
        campoEmail.setText(contacto.getEmail() != null ? contacto.getEmail() : "");
        campoEndereco.setText(contacto.getEndereco() != null ? contacto.getEndereco() : "");
        if (contacto.getDataNasc() != null)
            campoDataNasc.setValue(contacto.getDataNasc());

        if (contacto.getCategoria() != null) {
            comboCategoria.getItems().stream()
                .filter(c -> c.getId() == contacto.getCategoria().getId())
                .findFirst()
                .ifPresent(c -> comboCategoria.setValue(c));
        }

        // Preencher telefones
        containerTelefones.getChildren().clear();
        linhasTelefone.clear();
        if (contacto.getTelefones() != null && !contacto.getTelefones().isEmpty()) {
            for (Telefone t : contacto.getTelefones()) {
                adicionarLinhaTelefone(t.getNumero(),
                    t.getTipo() != null ? t.getTipo().name() : "MOVEL");
            }
        } else {
            adicionarLinhaTelefone();
        }
    }

    private void carregarCategorias() {
        List<Categoria> categorias = categoriaService.listarCategorias();
        comboCategoria.getItems().clear();
        comboCategoria.getItems().addAll(categorias);
        comboCategoria.setConverter(
            new javafx.util.StringConverter<Categoria>() {
                @Override
                public String toString(Categoria c) {
                    return c != null ? c.getNome() : "";
                }
                @Override
                public Categoria fromString(String s) { return null; }
            });

        // Gerar pills
        containerPills.getChildren().clear();
        for (Categoria c : categorias) {
            Button pill = new Button("● " + c.getNome());
            pill.getStyleClass().add("category-pill");
            Categoria catRef = c;
            pill.setOnAction(e -> comboCategoria.setValue(catRef));
            containerPills.getChildren().add(pill);
        }
    }

    @FXML
    private void adicionarLinhaTelefone() {
        adicionarLinhaTelefone("", "MOVEL");
    }

    private void adicionarLinhaTelefone(String numero, String tipo) {
        TextField campoNumero = new TextField(numero);
        campoNumero.setPromptText("+258 84 000 0000");
        campoNumero.getStyleClass().add("form-field");
        campoNumero.setMinWidth(160);
        HBox.setHgrow(campoNumero, Priority.ALWAYS);

        ComboBox<String> comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("MOVEL", "FIXO", "TRABALHO");
        comboTipo.setValue(tipo);
        comboTipo.getStyleClass().add("form-combo");
        comboTipo.setMinWidth(110);

        Button btnRemover = new Button("🗑");
        btnRemover.getStyleClass().add("btn-icon-danger");

        HBox linha = new HBox(8, campoNumero, comboTipo, btnRemover);
        linha.getStyleClass().add("phone-row");
        linha.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        btnRemover.setOnAction(e -> {
            containerTelefones.getChildren().remove(linha);
            linhasTelefone.remove(linha);
        });

        linhasTelefone.add(linha);
        containerTelefones.getChildren().add(linha);
    }

    @FXML
    private void guardar() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            erroNome.setText("O nome é obrigatório.");
            erroNome.setVisible(true);
            erroNome.setManaged(true);
            return;
        }
        erroNome.setVisible(false);
        erroNome.setManaged(false);

        try {
            Contacto contacto = modoEdicao
                ? contactoEmEdicao : new Contacto();

            contacto.setNomeCompleto(nome);
            contacto.setEmail(campoEmail.getText().trim());
            contacto.setEndereco(campoEndereco.getText().trim());
            contacto.setDataNasc(campoDataNasc.getValue());
            contacto.setCategoria(comboCategoria.getValue());

            // Recolher telefones
            List<Telefone> telefones = new ArrayList<>();
            for (HBox linha : linhasTelefone) {
                TextField tf = (TextField) linha.getChildren().get(0);
                ComboBox<String> cb =
                    (ComboBox<String>) linha.getChildren().get(1);
                String num = tf.getText().trim();
                if (!num.isEmpty()) {
                    TipoTelefone tipo = TipoTelefone.valueOf(cb.getValue());
                    telefones.add(new Telefone(num, tipo));
                }
            }
            contacto.setTelefones(telefones);

            if (modoEdicao) {
                contactoService.editarContacto(contacto);
            } else {
                contactoService.cadastrarContacto(
                    contacto, utilizadorActual.getId());
            }

            if (aoGuardar != null) aoGuardar.run();
            fechar();

        } catch (IllegalArgumentException e) {
            erroNome.setText(e.getMessage());
            erroNome.setVisible(true);
            erroNome.setManaged(true);
        }
    }

    @FXML
    private void fechar() {
        // Notifica o PrincipalController para fechar o overlay
        StackPane overlay = (StackPane) painelRoot.getScene()
            .lookup("#overlay");
        if (overlay != null) {
            PrincipalController ctrl = (PrincipalController)
                overlay.getScene().getUserData();
        }
        // Trigger de fecho via lookup do rootStack
        VBox root = (VBox) painelRoot.getParent().getParent().getParent();
        // Solução limpa: evento
        painelRoot.fireEvent(
            new javafx.event.Event(javafx.event.Event.ANY));

        // Fecha o overlay através de referência ao controller principal
        Object userData = painelRoot.getScene().getUserData();
        if (userData instanceof PrincipalController ctrl) {
            ctrl.fecharOverlay();
        }
    }
}