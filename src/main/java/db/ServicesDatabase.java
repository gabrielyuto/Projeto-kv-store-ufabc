package db;

import clientes.Mensagem;

import java.sql.*;
import java.time.LocalDateTime;

public class ServicesDatabase {
    private Statement statement;
    private Connection connection;
    private PreparedStatement preparedStatement;

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

    public Mensagem create(Mensagem mensagem) {
        Mensagem retorno = new Mensagem();

        LocalDateTime timestamp = LocalDateTime.now();
        mensagem.setTimestamp(timestamp);

        try{
            connection = connect("kv_store_service", "postgres", "postgres");
            String query = String.format("insert into kv_store(key, value, timestemp) values('%s','%s', '%s');", mensagem.getKey(), mensagem.getValue(), mensagem.getTimestamp());
            try {
                statement = connection.createStatement();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                statement.executeUpdate(query);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            retorno.setStatus("PUT_NOT_OK");

            return retorno;
        }

        retorno.setIp(mensagem.getIp());
        retorno.setPort(mensagem.getPort());
        retorno.setKey(mensagem.getKey());
        retorno.setValue(mensagem.getValue());
        retorno.setStatus("PUT_OK");
        retorno.setTimestamp(mensagem.getTimestamp());

        return retorno;
    }

    public Mensagem update(Mensagem mensagem) {
        LocalDateTime timestamp = LocalDateTime.now();
        mensagem.setTimestamp(timestamp);

        try {
            connection = connect("kv_store_service", "postgres", "postgres");
            String query = String.format("insert into kv_store(key, value, time) values('%s','%s', '%s');", mensagem.getKey(), mensagem.getValue(), mensagem.getTimestamp());
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            mensagem.setStatus("PUT_NOT_OK");

            return mensagem;
        }
        mensagem.setStatus("PUT_OK");

        return null;
    }

    public Mensagem get(Mensagem mensagem) {
        Mensagem retorno = new Mensagem();

        try {
            connection = connect("kv_store_service", "postgres", "postgres");
            preparedStatement = connection.prepareStatement("select * from kv_store where key=?");
            preparedStatement.setString(1, mensagem.getKey());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()){
                return null;
            }

            while(resultSet.next()) {
                retorno.setKey(resultSet.getString("key"));
                retorno.setValue(resultSet.getString("value"));

                Timestamp timestamp = resultSet.getTimestamp("timestemp");
                LocalDateTime localDateTime = timestamp.toLocalDateTime();
                retorno.setTimestamp(localDateTime);
            }
        } catch(SQLException ex){
            ex.printStackTrace();
        }

        return retorno;
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
}
