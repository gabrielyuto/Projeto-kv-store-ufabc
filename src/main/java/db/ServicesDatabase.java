package db;

import clientes.Mensagem;

import java.sql.*;
import java.time.LocalDateTime;

public class ServicesDatabase {
    private Connection connection;
    private PreparedStatement preparedStatement;

    public Mensagem get(Mensagem mensagem, String table) {
        try {
            Mensagem retorno = new Mensagem();

            connection = connect();

            if(table.equals("servidor_mestre")){
                preparedStatement = connection.prepareStatement("select * from servidor_mestre where key=?");
            } else if (table.equals("servidor_um")) {
                preparedStatement = connection.prepareStatement("select * from servidor_um where key=?");
            } else if (table.equals("servidor_dois")){
                preparedStatement = connection.prepareStatement("select * from servidor_dois where key=?");
            }

            preparedStatement.setString(1, mensagem.getKey());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                retorno.setKey(resultSet.getString("key"));
                retorno.setValue(resultSet.getString("value"));
                retorno.setIpClient(mensagem.getIpClient());
                retorno.setPortClient(mensagem.getPortClient());
                retorno.setIpServer(mensagem.getIpServer());
                retorno.setPortServer(mensagem.getPortServer());
                retorno.setTimestampClient(mensagem.getTimestampClient());
                retorno.setTimestampServer(resultSet.getTimestamp("timestamp").toLocalDateTime());

                return  retorno;
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public Mensagem create(Mensagem mensagem, String table) {
        Mensagem retorno = new Mensagem();
        LocalDateTime time = LocalDateTime.now();

        try{
            connection = connect();

            if(table.equals("servidor_mestre")){
                preparedStatement = connection.prepareStatement("insert into servidor_mestre (key, value, timestamp) values(?,?,?)");
            } else if (table.equals("servidor_um")) {
                preparedStatement = connection.prepareStatement("insert into servidor_um (key, value, timestamp) values(?,?,?)");
            } else if (table.equals("servido_dois")){
                preparedStatement = connection.prepareStatement("insert into servidor_dois (key, value, timestamp) values(?,?,?)");
            }

            preparedStatement.setString(1, mensagem.getKey());
            preparedStatement.setString(2, mensagem.getValue());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(time));
            preparedStatement.executeUpdate();


            if(table.equals("servidor_mestre")){
                preparedStatement = connection.prepareStatement("select * from servidor_mestre where key=?");
            } else if (table.equals("servidor_um")) {
                preparedStatement = connection.prepareStatement("select * from servidor_um where key=?");
            } else if (table.equals("servidor_dois")){
                preparedStatement = connection.prepareStatement("select * from servidor_dois where key=?");
            }

            preparedStatement.setString(1, mensagem.getKey());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                retorno.setKey(resultSet.getString("key"));
                retorno.setValue(resultSet.getString("value"));
                retorno.setTimestampServer(resultSet.getTimestamp("timestamp").toLocalDateTime());
                retorno.setIpServer(mensagem.getIpServer());
                retorno.setPortServer(mensagem.getPortServer());
                retorno.setStatus("PUT_OK");

                return retorno;
            } else {
                return null;
            }
        } catch (Exception e) {
            retorno.setStatus("PUT_NOT_OK");

            return retorno;
        }

    }

    public Mensagem update(Mensagem mensagem, String table) {
        Mensagem retorno = new Mensagem();
        LocalDateTime time = LocalDateTime.now();

        try {
            connection = connect();

            if(table.equals("servidor_mestre")){
                preparedStatement = connection.prepareStatement("update servidor_mestre set value=?, timestamp=? WHERE key=?");
            } else if (table.equals("servidor_um")) {
                preparedStatement = connection.prepareStatement("insert into servidor_um (key, value, timestamp) values(?,?,?)");
            } else if (table.equals("servido_dois")){
                preparedStatement = connection.prepareStatement("insert into servidor_dois (key, value, timestamp) values(?,?,?)");
            }

            preparedStatement.setString(1, mensagem.getValue());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(time));
            preparedStatement.setString(3, mensagem.getKey());
            preparedStatement.executeUpdate();


            if(table.equals("servidor_mestre")){
                preparedStatement = connection.prepareStatement("select * from servidor_mestre where key=?");
            } else if (table.equals("servidor_um")) {
                preparedStatement = connection.prepareStatement("select * from servidor_um where key=?");
            } else if (table.equals("servidor_dois")){
                preparedStatement = connection.prepareStatement("select * from servidor_dois where key=?");
            }

            preparedStatement.setString(1, mensagem.getKey());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                retorno.setKey(resultSet.getString("key"));
                retorno.setValue(resultSet.getString("value"));
                retorno.setIpServer(mensagem.getIpServer());
                retorno.setPortServer(mensagem.getPortServer());
                retorno.setTimestampClient(mensagem.getTimestampClient());
                retorno.setTimestampServer(resultSet.getTimestamp("timestamp").toLocalDateTime());
                retorno.setStatus("PUT_OK");

                return  retorno;
            } else {
                return null;
            }
        } catch (SQLException e) {
            retorno.setStatus("PUT_NOT_OK");

            return retorno;
        }
    }

    public void insertLocal(Mensagem mensagem, String table) {
        try{
            connection = connect();

            if (table.equals("servidor_um")) {
                preparedStatement = connection.prepareStatement("insert into servidor_um (key, value, timestamp) values(?,?,?)");
            } else if (table.equals("servido_dois")){
                preparedStatement = connection.prepareStatement("insert into servidor_dois (key, value, timestamp) values(?,?,?)");
            }

            preparedStatement.setString(1, mensagem.getKey());
            preparedStatement.setString(2, mensagem.getValue());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(mensagem.getTimestampServer()));
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection connect() {
        Connection connection = null;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/kv_store_service", "postgres", "postgres");
        } catch (Exception e) {
            System.out.println(e);
        }

        return connection;
    }
}
