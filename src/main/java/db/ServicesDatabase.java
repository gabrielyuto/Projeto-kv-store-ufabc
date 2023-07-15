package db;

import clientes.Mensagem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicesDatabase {
    private Connection connection;
    private PreparedStatement preparedStatement;

    public Optional<Mensagem> get(Mensagem mensagem, String table) {
        try {
            Mensagem retorno = new Mensagem();

            connection = connect();

            switch (table) {
                case "servidor_mestre":
                    preparedStatement = connection.prepareStatement("select * from servidor_mestre where key=?");
                    break;
                case "servidor_um":
                    preparedStatement = connection.prepareStatement("select * from servidor_um where key=?");
                    break;
                case "servidor_dois":
                    preparedStatement = connection.prepareStatement("select * from servidor_dois where key=?");
                    break;
            }

            preparedStatement.setString(1, mensagem.getKey());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Mensagem> resultados = new ArrayList<>();

            while(resultSet.next()){
                retorno.setKey(resultSet.getString("key"));
                retorno.setValue(resultSet.getString("value"));
                retorno.setIpClient(mensagem.getIpClient());
                retorno.setPortClient(mensagem.getPortClient());
                retorno.setIpServerMaster(mensagem.getIpServerMaster());
                retorno.setPortServerMaster(mensagem.getPortServerMaster());
                retorno.setIpServerOthers(mensagem.getIpServerOthers());
                retorno.setPortServerOthers(mensagem.getPortServerOthers());
                retorno.setTimestampClient(mensagem.getTimestampClient());
                retorno.setTimestampServer(resultSet.getTimestamp("timestamp").toLocalDateTime());

                resultados.add(retorno);
            }

            Optional<Mensagem> first = resultados.stream().findFirst();

            return first;

        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public Optional<Mensagem> create(Mensagem mensagem, String table) {
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


            switch (table) {
                case "servidor_mestre":
                    preparedStatement = connection.prepareStatement("select * from servidor_mestre where key=?");
                    break;
                case "servidor_um":
                    preparedStatement = connection.prepareStatement("select * from servidor_um where key=?");
                    break;
                case "servidor_dois":
                    preparedStatement = connection.prepareStatement("select * from servidor_dois where key=?");
                    break;
            }

            preparedStatement.setString(1, mensagem.getKey());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Mensagem> resultados = new ArrayList<>();

            while(resultSet.next()) {
                retorno.setKey(resultSet.getString("key"));
                retorno.setValue(resultSet.getString("value"));
                retorno.setTimestampServer(resultSet.getTimestamp("timestamp").toLocalDateTime());
                retorno.setIpServerMaster(mensagem.getIpServerMaster());
                retorno.setPortServerMaster(mensagem.getPortServerMaster());
                retorno.setIpServerOthers(mensagem.getIpServerOthers());
                retorno.setPortServerOthers(mensagem.getPortServerOthers());
                retorno.setIpClient(mensagem.getIpClient());
                retorno.setPortClient(mensagem.getPortClient());
                retorno.setStatus("PUT_OK");

                resultados.add(retorno);
            }

            Optional<Mensagem> first = resultados.stream().findFirst();

            return first;
        } catch (Exception e) {
            return null;
        }
    }

    public Optional<Mensagem> update(Mensagem mensagem, String table) {
        Mensagem retorno = new Mensagem();
        LocalDateTime time = LocalDateTime.now();

        try {
            connection = connect();

            switch (table) {
                case "servidor_mestre":
                    preparedStatement = connection.prepareStatement("update servidor_mestre set value=?, timestamp=? WHERE key=?");
                    break;
                case "servidor_um":
                    preparedStatement = connection.prepareStatement("insert into servidor_um (key, value, timestamp) values(?,?,?)");
                    break;
                case "servido_dois":
                    preparedStatement = connection.prepareStatement("insert into servidor_dois (key, value, timestamp) values(?,?,?)");
                    break;
            }

            preparedStatement.setString(1, mensagem.getValue());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(time));
            preparedStatement.setString(3, mensagem.getKey());
            preparedStatement.executeUpdate();


            switch (table) {
                case "servidor_mestre":
                    preparedStatement = connection.prepareStatement("select * from servidor_mestre where key=?");
                    break;
                case "servidor_um":
                    preparedStatement = connection.prepareStatement("select * from servidor_um where key=?");
                    break;
                case "servidor_dois":
                    preparedStatement = connection.prepareStatement("select * from servidor_dois where key=?");
                    break;
            }

            preparedStatement.setString(1, mensagem.getKey());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Mensagem> resultados = new ArrayList<>();

            while(resultSet.next()) {
                retorno.setKey(resultSet.getString("key"));
                retorno.setValue(resultSet.getString("value"));
                retorno.setIpServerMaster(mensagem.getIpServerMaster());
                retorno.setPortServerMaster(mensagem.getPortServerMaster());
                retorno.setIpServerOthers(mensagem.getIpServerOthers());
                retorno.setPortServerOthers(mensagem.getPortServerOthers());
                retorno.setIpClient(mensagem.getIpClient());
                retorno.setPortClient(mensagem.getPortClient());
                retorno.setTimestampClient(mensagem.getTimestampClient());
                retorno.setTimestampServer(resultSet.getTimestamp("timestamp").toLocalDateTime());
                retorno.setStatus("PUT_OK");

                resultados.add(retorno);
            }

            Optional<Mensagem> first = resultados.stream().findFirst();

            return first;
        } catch (SQLException e) {
            return null;
        }
    }

    public void insertLocal(Mensagem mensagem, String table) {
        try{
            connection = connect();

            switch (table) {
                case "servidor_um":
                    preparedStatement = connection.prepareStatement("insert into servidor_um (key, value, timestamp) values(?,?,?)");
                    break;
                case "servidor_dois":
                    preparedStatement = connection.prepareStatement("insert into servidor_dois (key, value, timestamp) values(?,?,?)");
                    break;
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
