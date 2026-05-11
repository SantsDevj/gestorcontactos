package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Contacto {

    private int id;
    private int idUtilizador;
    private String nomeCompleto;
    private String email;
    private String endereco;
    private LocalDate dataNasc;
    private List<Telefone> telefones;
    private Categoria categoria;

    // Construtor vazio — inicializa a lista como ArrayList vazio
    public Contacto() {
        this.telefones = new ArrayList<>();
    }

    // Construtor com argumentos — também inicializa a lista
    public Contacto(String nomeCompleto, String email, String endereco,
                    LocalDate dataNasc, Categoria categoria) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.endereco = endereco;
        this.dataNasc = dataNasc;
        this.categoria = categoria;
        this.telefones = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(int id) {
        this.idUtilizador = idUtilizador;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public LocalDate getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(LocalDate dataNasc) {
        this.dataNasc = dataNasc;
    }

    public List<Telefone> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<Telefone> telefones) {
        this.telefones = telefones;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    // Métodos especiais da lista
    public void adicionarTelefone(Telefone telefone) {
        this.telefones.add(telefone);
    }

    public void removerTelefone(Telefone telefone) {
        this.telefones.remove(telefone);
    }
}