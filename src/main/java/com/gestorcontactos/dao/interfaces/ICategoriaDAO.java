package main.java.dao.interfaces;

import java.util.List;

import model.Categoria;

public interface ICategoriaDAO {
    
    void criar(Categoria categoria);
    List<Categoria> listar();
    void eliminar(int id);
    Categoria buscarPorId(int id);
}
