package com.gestorcontactos.model;

public class Categoria {
    private int id;
    private String nome;
    private String descricao;
    
    // Constructor sem argumentos
    public Categoria(){
        
    }
    
    // Constructor com argumentos
    public Categoria(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
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
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    
}
