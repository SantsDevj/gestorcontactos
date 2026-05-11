package model;

public class Telefone {

    private int id;
    private String numero;
    private TipoTelefone tipo;

    // Constructor vazio
    public Telefone() {
        // sem nehuma implementação
    }

    // Constructor com argumentos
    public Telefone(String numero, TipoTelefone tipo) {
        this.numero = numero;
        this.tipo = tipo;
    }
    
    // Getters e Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }
    public TipoTelefone getTipo() {
        return tipo;
    }
    public void setTipo(TipoTelefone tipo) {
        this.tipo = tipo;
    }

    
}
