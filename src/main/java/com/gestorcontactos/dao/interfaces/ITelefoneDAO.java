package com.gestorcontactos.dao.interfaces;

import java.util.List;

import com.gestorcontactos.model.Telefone;

public interface ITelefoneDAO {
    
    // declarando os métodos abastractos
    void adicionar(Telefone telefone, int idContacto);
    void remover(int id);
    List<Telefone> listarPorContacto(int idContacto);
}
