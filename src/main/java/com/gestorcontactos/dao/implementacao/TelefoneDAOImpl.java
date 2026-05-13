package com.gestorcontactos.dao.implementacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gestorcontactos.dao.interfaces.ITelefoneDAO;
import com.gestorcontactos.dao_singleton.ConexaoDB;
import com.gestorcontactos.model.Telefone;
import com.gestorcontactos.model.TipoTelefone;

public class TelefoneDAOImpl implements ITelefoneDAO {

    private Connection conexao;
    
    public TelefoneDAOImpl() {
        this.conexao = ConexaoDB.getInstancia().getConnection();
    }
    
    @Override
    public void adicionar(Telefone telefone, int idContacto) {
        String sql =
            "INSERT INTO manager.telefone (id_contacto, numero, tipo) " +
            "VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idContacto);
            stmt.setString(2, telefone.getNumero());
            // Converte o enum para String para guardar na base de dados
            stmt.setString(3, telefone.getTipo() != null
                ? telefone.getTipo().name() : null);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao adicionar telefone.");
            e.printStackTrace();
        }
    }
   

    @Override
    public void remover(int id) {

        String sql = "DELETE FROM manager.telefone WHERE id_telefone = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao remover telefone.");
            e.printStackTrace();
        }
    }

    @Override
    public List<Telefone> listarPorContacto(int idContacto) {

        List<Telefone> telefones = new ArrayList<>();
        
        String sql =
            "SELECT id_telefone, numero, tipo " +
            "FROM manager.telefone WHERE id_contacto = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idContacto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Telefone telefone = new Telefone();
                    telefone.setId(rs.getInt("id_telefone"));
                    telefone.setNumero(rs.getString("numero"));

                    // Converte a String de volta para o enum TipoTelefone
                    String tipoStr = rs.getString("tipo");
                    if (tipoStr != null) {
                        telefone.setTipo(TipoTelefone.valueOf(tipoStr));
                    }

                    telefones.add(telefone);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar telefones do contacto.");
            e.printStackTrace();
        }

        return telefones;
    }

}