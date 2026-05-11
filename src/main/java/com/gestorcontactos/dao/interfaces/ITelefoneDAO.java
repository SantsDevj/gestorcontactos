package main.java.dao.interfaces;

import java.util.List;

import model.Telefone;

public interface ITelefoneDAO {
    
    // declarando os métodos abastractos
    void adiciona(Telefone telefone, int idContacto);
    void remover(int id);
    List<Telefone> listarPorContacto(int idContacto);
}
