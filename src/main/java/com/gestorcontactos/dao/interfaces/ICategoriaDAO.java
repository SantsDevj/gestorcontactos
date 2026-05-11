package com.gestorcontactos.dao.interfaces;

import java.util.List;

import com.gestorcontactos.model.Categoria;

public interface ICategoriaDAO {
    
    void criar(Categoria categoria);
    List<Categoria> listar();
    void eliminar(int id);
    Categoria buscarPorId(int id);
}
