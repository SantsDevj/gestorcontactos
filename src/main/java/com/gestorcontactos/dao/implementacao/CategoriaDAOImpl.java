package com.gestorcontactos.dao.implementacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gestorcontactos.dao.interfaces.ICategoriaDAO;
import com.gestorcontactos.dao_singleton.ConexaoDB;
import com.gestorcontactos.model.Categoria;

public class CategoriaDAOImpl implements ICategoriaDAO {

    private Connection conexao;

    public CategoriaDAOImpl() {
        this.conexao = ConexaoDB.getInstancia().getConnection();
    }

    @Override
    public void criar(Categoria categoria) {

        String sql =
            "INSERT INTO manager.categoria (nome, descricao) VALUES (?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao criar categoria.");
            e.printStackTrace();
        }
    }

    @Override
    public List<Categoria> listar() {

        List<Categoria> categorias = new ArrayList<>();

        String sql = "SELECT id_categoria, nome, descricao FROM manager.categoria";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id_categoria"));
                categoria.setNome(rs.getString("nome"));
                categoria.setDescricao(rs.getString("descricao"));
                categorias.add(categoria);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar categorias.");
            e.printStackTrace();
        }

        return categorias;
    }

    @Override
    public void eliminar(int id) {

        String sql = "DELETE FROM manager.categoria WHERE id_categoria = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao eliminar categoria.");
            e.printStackTrace();
        }
    }

    @Override
    public Categoria buscarPorId(int id) {

        Categoria categoria = null;

        String sql =
            "SELECT id_categoria, nome, descricao " +
            "FROM manager.categoria WHERE id_categoria = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    categoria = new Categoria();
                    categoria.setId(rs.getInt("id_categoria"));
                    categoria.setNome(rs.getString("nome"));
                    categoria.setDescricao(rs.getString("descricao"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar categoria por ID.");
            e.printStackTrace();
        }

        return categoria;
    }
}