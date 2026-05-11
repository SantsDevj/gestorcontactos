package dao.implementacao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.interfaces.IUtilizadorDAO;
import dao_singleton.ConexaoDB;
import model.Utilizador;

public class UtilizadorDAOImpl implements IUtilizadorDAO {

    private Connection conexao;

    public UtilizadorDAOImpl() {
        this.conexao = ConexaoDB.getInstancia().getConnection();
    }

    @Override
    public void cadastrar(Utilizador utilizador) {
        String sql =
            "INSERT INTO manager.utilizador (nome, username, senha, data_criacao) " +
            "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, utilizador.getNome());
            stmt.setString(2, utilizador.getUsername());
            stmt.setString(3, utilizador.getSenha());
            stmt.setDate(4, Date.valueOf(utilizador.getDataCriacao()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar utilizador.");
            e.printStackTrace();
        }
    }

    @Override
    public Utilizador buscarPorUsername(String username) {

        Utilizador utilizador = null;

        String sql =
            "SELECT id_utilizador, nome, username, senha, data_criacao " +
            "FROM manager.utilizador WHERE username = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    utilizador = new Utilizador();
                    utilizador.setId(rs.getInt("id_utilizador"));
                    utilizador.setNome(rs.getString("nome"));
                    utilizador.setUsername(rs.getString("username"));
                    utilizador.setSenha(rs.getString("senha"));

                    Date dataCriacao = rs.getDate("data_criacao");
                    if (dataCriacao != null) {
                        utilizador.setDataCriacao(dataCriacao.toLocalDate());
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar utilizador por username.");
            e.printStackTrace();
        }

        return utilizador;
    }

    @Override
    public boolean validarCredenciais(String username, String senha) {

        String sql =
            "SELECT COUNT(*) FROM manager.utilizador " +
            "WHERE username = ? AND senha = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Se COUNT(*) > 0, as credenciais são válidas
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao validar credenciais.");
            e.printStackTrace();
        }

        return false;
    }
}