package main.java.dao.interfaces;

import java.util.List;

import model.Contacto;

public interface IContactoDAO {

    // Métodos os
    int cadastrar(Contacto contacto);
    List<Contacto> listar(int idUtilizador);
    void editar(Contacto contacto);
    void eliminar(int id);
    List<Contacto> pesquisar(String termo, int idUtilizador);
    Contacto buscarPorId(int id);
}
