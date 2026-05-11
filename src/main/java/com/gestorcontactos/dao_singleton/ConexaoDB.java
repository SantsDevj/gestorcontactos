package com.gestorcontactos.dao_singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

    // Única instância da classe — começa como null
    private static ConexaoDB instancia = null;

    // A ligação real à base de dados
    private Connection connection;

    // Constantes de ligação — declaradas como atributos da classe, não dentro do construtor
    private static final String URL =
        "jdbc:sqlserver://localhost;databaseName=sistema_gerenciamento_contactos;" +
        "encrypt=true;trustServerCertificate=true;";
    private static final String USER     = "sa";
    private static final String PASSWORD = "151025";

    // Construtor privado — impede que outras classes usem new ConexaoDB()
    private ConexaoDB() {
        try {
            // Atribui a ligação ao atributo da classe
            // Não usa try-with-resources para que a ligação não seja fechada imediatamente
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Ligação à base de dados estabelecida com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao estabelecer ligação à base de dados.");
            e.printStackTrace();
        }
    }

    // Ponto de acesso único à instância — lazy initialization
    public static ConexaoDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoDB();
        }
        return instancia;
    }

    // Devolve a ligação para uso nos DAOs
    public Connection getConnection() {
        return connection;
    }

    // Fecha a ligação quando o sistema for encerrado
    public void fecharConexao() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Ligação à base de dados encerrada.");
            } catch (SQLException e) {
                System.out.println("Erro ao fechar a ligação.");
                e.printStackTrace();
            }
        }
    }
}