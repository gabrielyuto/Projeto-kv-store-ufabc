package db;

import mensagem.Mensagem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//  Aqui temos todos os serviços para que os servidores consigam se comunicar com o banco de dados.
public class ServicesDatabase {
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

//  Antes de criar a querie, este método é chamado por cada um dos serviços desse service. Ele estabelece a comunicação com o banco de dados.
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

//  Aqui temos o método get, que irá busca a chave no banco.
//  Para preparar a querie, primeiro é definido qual o servidor que está chamando o serviço. A depender, é preparado uma querie distinta que atenda a requisição (servidor_mestre, servidor_um, servidor_dois).
//  Depois de preparada, a querie é executada e o seu retorno é devolvido para o servidor.
    public Optional<Mensagem> get(Mensagem mensagem, String table) {
        Mensagem response = new Mensagem();

        try {
            switch (table) {
                case "servidor_mestre":
                    preparedStatement = connect().prepareStatement("select * from servidor_mestre where key=?");
                    break;
                case "servidor_um":
                    preparedStatement = connect().prepareStatement("select * from servidor_um where key=?");
                    break;
                case "servidor_dois":
                    preparedStatement = connect().prepareStatement("select * from servidor_dois where key=?");
                    break;
            }

            preparedStatement.setString(1, mensagem.getKey());
            resultSet = preparedStatement.executeQuery();

            List<Mensagem> resultados = new ArrayList<>();

            while(resultSet.next()){
                response.setKey(resultSet.getString("key"));
                response.setValue(resultSet.getString("value"));
                response.setIpFrom(mensagem.getIpFrom());
                response.setPortFrom(mensagem.getPortFrom());
                response.setIpServerMaster(mensagem.getIpServerMaster());
                response.setPortServerMaster(mensagem.getPortServerMaster());
                response.setIpServerOne(mensagem.getIpServerOne());
                response.setPortServerOne(mensagem.getPortServerOne());
                response.setIpServerTwo(mensagem.getIpServerTwo());
                response.setPortServerTwo(mensagem.getPortServerTwo());
                response.setTimestampClient(mensagem.getTimestampClient());
                response.setTimestampServer(resultSet.getTimestamp("timestamp").toLocalDateTime());

                resultados.add(response);
            }

            Optional<Mensagem> first = resultados.stream().findFirst();

            return first;

        } catch (SQLException e) {
            return Optional.empty();
        }
    }

//  Aqui temos o método responsável por criar um novo registro no banco.
//  Para preparar a querie, primeiro é definido qual o servidor que está chamando o serviço. A depender, é preparado uma querie distinta que atenda a requisição (servidor_mestre, servidor_um, servidor_dois).
//  Depois de preparada, a querie é executada e o seu retorno é devolvido para o servidor.
    public Optional<Mensagem> create(Mensagem mensagem, String table) {
        Mensagem response = new Mensagem();
        LocalDateTime time = LocalDateTime.now();

        try{
            if(table.equals("servidor_mestre")){
                preparedStatement = connect().prepareStatement("insert into servidor_mestre (key, value, timestamp) values(?,?,?)");
            } else if (table.equals("servidor_um")) {
                preparedStatement = connect().prepareStatement("insert into servidor_um (key, value, timestamp) values(?,?,?)");
            } else if (table.equals("servidor_dois")){
                preparedStatement = connect().prepareStatement("insert into servidor_dois (key, value, timestamp) values(?,?,?)");
            }

            preparedStatement.setString(1, mensagem.getKey());
            preparedStatement.setString(2, mensagem.getValue());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(time));
            preparedStatement.executeUpdate();

            switch (table) {
                case "servidor_mestre":
                    preparedStatement = connect().prepareStatement("select * from servidor_mestre where key=?");
                    break;
                case "servidor_um":
                    preparedStatement = connect().prepareStatement("select * from servidor_um where key=?");
                    break;
                case "servidor_dois":
                    preparedStatement = connect().prepareStatement("select * from servidor_dois where key=?");
                    break;
            }

            preparedStatement.setString(1, mensagem.getKey());
            resultSet = preparedStatement.executeQuery();

            List<Mensagem> resultados = new ArrayList<>();

            while(resultSet.next()) {
                response.setKey(resultSet.getString("key"));
                response.setValue(resultSet.getString("value"));
                response.setTimestampServer(resultSet.getTimestamp("timestamp").toLocalDateTime());
                response.setIpServerClientRequest(mensagem.getIpServerClientRequest());
                response.setPortServerClientRequest(mensagem.getPortServerClientRequest());
                response.setIpServerMaster(mensagem.getIpServerMaster());
                response.setPortServerMaster(mensagem.getPortServerMaster());
                response.setIpServerOne(mensagem.getIpServerOne());
                response.setPortServerOne(mensagem.getPortServerOne());
                response.setIpServerTwo(mensagem.getIpServerTwo());
                response.setPortServerTwo(mensagem.getPortServerTwo());
                response.setIpFrom(mensagem.getIpFrom());
                response.setPortFrom(mensagem.getPortFrom());
                response.setStatus("PUT_OK");

                resultados.add(response);
            }

            Optional<Mensagem> first = resultados.stream().findFirst();

            return first;
        } catch (Exception e) {
            return Optional.empty();
        }
    }


//  Aqui temos o método responsável por atualizar um novo registro no banco.
//  Para preparar a querie, primeiro é definido qual o servidor que está chamando o serviço. A depender, é preparado uma querie distinta que atenda a requisição (servidor_mestre, servidor_um, servidor_dois).
//  Depois de preparada, a querie é executada e o seu retorno é devolvido para o servidor.
    public Optional<Mensagem> update(Mensagem mensagem, String table) {
        Mensagem response = new Mensagem();
        LocalDateTime time = LocalDateTime.now();

        try {
            switch (table) {
                case "servidor_mestre":
                    preparedStatement = connect().prepareStatement("update servidor_mestre set value=?, timestamp=? WHERE key=?");
                    break;
                case "servidor_um":
                    preparedStatement = connect().prepareStatement("update servidor_um set value=?, timestamp=? WHERE key=?");
                    break;
                case "servidor_dois":
                    preparedStatement = connect().prepareStatement("update servidor_dois set value=?, timestamp=? WHERE key=?");
                    break;
            }

            preparedStatement.setString(1, mensagem.getValue());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(time));
            preparedStatement.setString(3, mensagem.getKey());
            preparedStatement.executeUpdate();

            switch (table) {
                case "servidor_mestre":
                    preparedStatement = connect().prepareStatement("select * from servidor_mestre where key=?");
                    break;
                case "servidor_um":
                    preparedStatement = connect().prepareStatement("select * from servidor_um where key=?");
                    break;
                case "servidor_dois":
                    preparedStatement = connect().prepareStatement("select * from servidor_dois where key=?");
                    break;
            }

            preparedStatement.setString(1, mensagem.getKey());
            resultSet = preparedStatement.executeQuery();

            List<Mensagem> resultados = new ArrayList<>();

            while(resultSet.next()) {
                response.setKey(resultSet.getString("key"));
                response.setValue(resultSet.getString("value"));
                response.setIpServerClientRequest(mensagem.getIpServerClientRequest());
                response.setPortServerClientRequest(mensagem.getPortServerClientRequest());
                response.setIpServerMaster(mensagem.getIpServerMaster());
                response.setPortServerMaster(mensagem.getPortServerMaster());
                response.setIpServerOne(mensagem.getIpServerOne());
                response.setPortServerOne(mensagem.getPortServerOne());
                response.setIpServerTwo(mensagem.getIpServerTwo());
                response.setPortServerTwo(mensagem.getPortServerTwo());
                response.setIpFrom(mensagem.getIpFrom());
                response.setPortFrom(mensagem.getPortFrom());
                response.setTimestampClient(mensagem.getTimestampClient());
                response.setTimestampServer(resultSet.getTimestamp("timestamp").toLocalDateTime());
                response.setStatus("PUT_OK");

                resultados.add(response);
            }

            Optional<Mensagem> first = resultados.stream().findFirst();

            return first;
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
}
