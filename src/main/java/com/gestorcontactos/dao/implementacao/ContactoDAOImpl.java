package dao.implementacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import dao.interfaces.IContactoDAO;
import dao_singleton.ConexaoDB;
import model.Categoria;
import model.Contacto;

public class ContactoDAOImpl implements IContactoDAO {

    private Connection conexao;

    public ContactoDAOImpl() {
        this.conexao = ConexaoDB.getInstancia().getConnection();
    }

    @Override
    public int cadastrar(Contacto contacto) {
        String sql =
            "INSERT INTO manager.contacto " +
            "(id_utilizador, id_categoria, nome_completo, email, endereco, data_nasc) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                 conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, contacto.getIdUtilizador());

            if (contacto.getCategoria() != null) {
                stmt.setInt(2, contacto.getCategoria().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, contacto.getNomeCompleto());
            stmt.setString(4, contacto.getEmail());
            stmt.setString(5, contacto.getEndereco());

            if (contacto.getDataNasc() != null) {
                stmt.setDate(6, Date.valueOf(contacto.getDataNasc()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    int idGerado = chaves.getInt(1);
                    contacto.setId(idGerado);
                    return idGerado;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar contacto.");
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Contacto> listar(int idUtilizador) {
        List<Contacto> contactos = new ArrayList<>();
        String sql =
            "SELECT c.id_contacto, c.id_utilizador, c.nome_completo, c.email, " +
            "       c.endereco, c.data_nasc, c.id_categoria, " +
            "       cat.nome AS nome_categoria, cat.descricao AS descricao_categoria " +
            "FROM manager.contacto c " +
            "LEFT JOIN manager.categoria cat ON c.id_categoria = cat.id_categoria " +
            "WHERE c.id_utilizador = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idUtilizador);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Contacto c = new Contacto();
                    c.setId(rs.getInt("id_contacto"));
                    c.setIdUtilizador(rs.getInt("id_utilizador"));
                    c.setNomeCompleto(rs.getString("nome_completo"));
                    c.setEmail(rs.getString("email"));
                    c.setEndereco(rs.getString("endereco"));
                    Date d = rs.getDate("data_nasc");
                    if (d != null) c.setDataNasc(d.toLocalDate());
                    int idCat = rs.getInt("id_categoria");
                    if (!rs.wasNull()) {
                        Categoria cat = new Categoria();
                        cat.setId(idCat);
                        cat.setNome(rs.getString("nome_categoria"));
                        cat.setDescricao(rs.getString("descricao_categoria"));
                        c.setCategoria(cat);
                    }
                    contactos.add(c);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar contactos.");
            e.printStackTrace();
        }
        return contactos;
    }

    @Override
    public void editar(Contacto contacto) {
        String sql =
            "UPDATE manager.contacto " +
            "SET nome_completo=?, email=?, endereco=?, data_nasc=?, id_categoria=? " +
            "WHERE id_contacto=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, contacto.getNomeCompleto());
            stmt.setString(2, contacto.getEmail());
            stmt.setString(3, contacto.getEndereco());
            if (contacto.getDataNasc() != null)
                stmt.setDate(4, Date.valueOf(contacto.getDataNasc()));
            else stmt.setNull(4, Types.DATE);
            if (contacto.getCategoria() != null)
                stmt.setInt(5, contacto.getCategoria().getId());
            else stmt.setNull(5, Types.INTEGER);
            stmt.setInt(6, contacto.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao editar contacto.");
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM manager.contacto WHERE id_contacto=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao eliminar contacto.");
            e.printStackTrace();
        }
    }

    @Override
    public List<Contacto> pesquisar(String termo, int idUtilizador) {
        List<Contacto> contactos = new ArrayList<>();
        String sql =
            "SELECT DISTINCT c.id_contacto, c.id_utilizador, c.nome_completo, c.email, " +
            "       c.endereco, c.data_nasc, c.id_categoria, " +
            "       cat.nome AS nome_categoria, cat.descricao AS descricao_categoria " +
            "FROM manager.contacto c " +
            "LEFT JOIN manager.categoria cat ON c.id_categoria = cat.id_categoria " +
            "LEFT JOIN manager.telefone t   ON c.id_contacto  = t.id_contacto " +
            "WHERE c.id_utilizador=? AND (c.nome_completo LIKE ? OR t.numero LIKE ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String busca = "%" + termo + "%";
            stmt.setInt(1, idUtilizador);
            stmt.setString(2, busca);
            stmt.setString(3, busca);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Contacto c = new Contacto();
                    c.setId(rs.getInt("id_contacto"));
                    c.setIdUtilizador(rs.getInt("id_utilizador"));
                    c.setNomeCompleto(rs.getString("nome_completo"));
                    c.setEmail(rs.getString("email"));
                    c.setEndereco(rs.getString("endereco"));
                    Date d = rs.getDate("data_nasc");
                    if (d != null) c.setDataNasc(d.toLocalDate());
                    int idCat = rs.getInt("id_categoria");
                    if (!rs.wasNull()) {
                        Categoria cat = new Categoria();
                        cat.setId(idCat);
                        cat.setNome(rs.getString("nome_categoria"));
                        cat.setDescricao(rs.getString("descricao_categoria"));
                        c.setCategoria(cat);
                    }
                    contactos.add(c);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao pesquisar contactos.");
            e.printStackTrace();
        }
        return contactos;
    }

    @Override
    public Contacto buscarPorId(int id) {
        Contacto contacto = null;
        String sql =
            "SELECT c.id_contacto, c.id_utilizador, c.nome_completo, c.email, " +
            "       c.endereco, c.data_nasc, c.id_categoria, " +
            "       cat.nome AS nome_categoria, cat.descricao AS descricao_categoria " +
            "FROM manager.contacto c " +
            "LEFT JOIN manager.categoria cat ON c.id_categoria = cat.id_categoria " +
            "WHERE c.id_contacto=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    contacto = new Contacto();
                    contacto.setId(rs.getInt("id_contacto"));
                    contacto.setIdUtilizador(rs.getInt("id_utilizador"));
                    contacto.setNomeCompleto(rs.getString("nome_completo"));
                    contacto.setEmail(rs.getString("email"));
                    contacto.setEndereco(rs.getString("endereco"));
                    Date d = rs.getDate("data_nasc");
                    if (d != null) contacto.setDataNasc(d.toLocalDate());
                    int idCat = rs.getInt("id_categoria");
                    if (!rs.wasNull()) {
                        Categoria cat = new Categoria();
                        cat.setId(idCat);
                        cat.setNome(rs.getString("nome_categoria"));
                        cat.setDescricao(rs.getString("descricao_categoria"));
                        contacto.setCategoria(cat);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar contacto por ID.");
            e.printStackTrace();
        }
        return contacto;
    }
}