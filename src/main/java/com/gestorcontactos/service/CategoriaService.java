package service;

import java.util.List;

import dao.interfaces.ICategoriaDAO;
import model.Categoria;

public class CategoriaService {

    private ICategoriaDAO categoriaDAO;

    public CategoriaService(ICategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    public void criarCategoria(Categoria categoria) {

        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da categoria não pode estar vazio.");
        }

        categoriaDAO.criar(categoria);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.listar();
    }

    public void eliminarCategoria(int id) {
        categoriaDAO.eliminar(id);
    }
}