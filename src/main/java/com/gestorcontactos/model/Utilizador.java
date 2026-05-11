package model;

import java.time.LocalDate;

public class Utilizador {

    private int id;
    private String nome;
    private String username;
    private String senha;
    private LocalDate dataCriacao;
    
    // Constructor vazio sem argumentos
    public Utilizador() {
        // Será útil para criar objecto Uilizador, e depois preencher os campos um a um
    }

    // Constructor com argumentos, útil para a situação onde já tenho dados prontos
    public Utilizador(String nome, String username, String senha, LocalDate dataCriacao) {
        this.nome = nome;
        this.username = username;
        this.senha = senha;
        this.dataCriacao = dataCriacao;
    }


    
    // Getters e Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public LocalDate getdataCriacao() {
        return dataCriacao;
    }
    public void setdataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    
}