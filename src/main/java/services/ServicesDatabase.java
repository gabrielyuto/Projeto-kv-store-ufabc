package services;

import clientes.Mensagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicesDatabase {
    private static Statement statement;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public void createTable() {
        try {
            connection = connect("kv_store_service", "postgres", "postgres");
            String query = "create table kv_store(id SERIAL, key VARCHAR(200), value VARCHAR(200), timestemp TIMESTAMP);";
            statement = connection.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table created");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Mensagem update(Mensagem mensagem) {
        Mensagem response = new Mensagem();

        try {
            connection = connect("kv_store_service", "postgres", "postgres");
            String query = String.format("insert into kv_store(key, value) values('%s','%s');", mensagem.getKey(), mensagem.getValue());
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            response.setStatus("PUT_NOT_OK");

            return response;
        }
        response.setStatus("PUT_OK");

        return response;
    }

    public List<Mensagem> get(Mensagem mensagem) {
        List<Mensagem> list = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement("select * from kv_store where key=?");
            preparedStatement.setString(1, mensagem.getKey());
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                mensagem = new Mensagem();

                mensagem.setValue(resultSet.getString("value"));

                list.add(mensagem);
            }
        } catch(SQLException ex){
            return null;
        }

        return listClientWithFile;
    }

    private Connection connect(String dbname, String user, String pass) {
        Connection connection = null;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbname, user, pass);
        } catch (Exception e) {
            System.out.println(e);
        }

        return connection;
    }

//    public String updateNewRequestDownload(Connection connection, Client client){
//        PreparedStatement preparedStatement;
//
//        try {
//            preparedStatement = connection.prepareStatement("update files set status='DOWNLOADED' where port = ? AND file=?");
//            preparedStatement.setString(1, String.valueOf(client.getDestiny_port()));
//            preparedStatement.setString(2, client.getFile_request());
//            preparedStatement.execute();
//
//        } catch (SQLException ex) {
//            return "UPDATE_NOT_OK";
//        }
//
//        System.out.println("UPDATE_OK");
//        return "UPDATE_OK";
//    }
}
