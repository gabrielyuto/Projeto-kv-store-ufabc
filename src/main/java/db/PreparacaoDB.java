package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

//  Aqui ocorre apenas a criação das tabelas no banco de dados.
//  Vale ressaltar que para o banco, esta sendo utilizado o docker, que cria um container para subir um banco PostgreSQL.
//  Por isso, antes de executar a criação, é preciso que o docker crie o banco. Para isso, o docker precisa estar instalado e é necessário somente executar o comando na console "docker-compose up -d".
public class PreparacaoDB {
    public static void main(String[] args) {
        Connection connection;
        Statement statement;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/kv_store_service", "postgres", "postgres");
            String query1 = "create table servidor_mestre(id SERIAL, key VARCHAR(200), value VARCHAR(200), timestamp TIMESTAMP);";
            statement = connection.createStatement();
            statement.executeUpdate(query1);

            String query2 = "create table servidor_um(id SERIAL, key VARCHAR(200), value VARCHAR(200), timestamp TIMESTAMP);";
            statement = connection.createStatement();
            statement.executeUpdate(query2);

            String query3 = "create table servidor_dois(id SERIAL, key VARCHAR(200), value VARCHAR(200), timestamp TIMESTAMP);";
            statement = connection.createStatement();
            statement.executeUpdate(query3);

            System.out.println("Table created");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
