package thread;

import mensagem.Mensagem;
import db.ServicesDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;


//  Essa é a thread do servidor UM.
//  Quando é inicializada, ela recebe o socket e a mensagem.
//  Dentro da mensagem, existe o conteúdo referente a qual o tipo de requisição que esta chegando, podendo ser um GET ou um PUT (quando é o Cliente que chama o servidor),
//  ou um REPLICATION (neste caso, quando o servidor um ou dois repassar o PUT para o servidor mestre).
public class ThreadServidorMestre implements Runnable {
    private final Socket socket;
    private Mensagem mensagem;
    private Mensagem response;
    private Optional<Mensagem> optionalResponse;
    private Optional<Mensagem> optionalResponseServerOne;
    private Optional<Mensagem> optionalResponseServerTwo;
    private final String table = "servidor_mestre";

    public ThreadServidorMestre(Socket socket, Mensagem mensagem){
        this.mensagem = mensagem;
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            switch (mensagem.getRequest()) {

//              Se a requisição for um GET, a mensagem é repassada para o método get(), que ira processar a informação, e através de uma comunicação com o banco de dados, irá realizar a busca pela chave.
//              Se existir um retorno, uma mensagem é exibida na console e depois retornada para o cliente a resposta. Se não existir, é retornado uma mensagem com conteúdo vazio.
                case "GET":
                    optionalResponse = get(mensagem);

                    if(optionalResponse.isPresent()){
                        response = optionalResponse.get();

                        System.out.println(
                                "Cliente " + response.getIpFrom() + ":" + response.getPortFrom()
                                        + " GET key:" + response.getKey()
                                        + " ts:" + response.getTimestampClient()
                                        + ". Meu ts é " + response.getTimestampServer()
                                        + ", portanto devolvendo " + response.getValue()
                        );
                    } else {
                        response = new Mensagem();
                    }

                    sendBack(response);
                    break;

//              Caso a requisição seja um PUT, primeiro o servidor exibe uma mensagem de que recebeu a requisição do cliente.
//              Depois disso, o método put() se responsabiliza de inserir a informação da mensagem no banco de dados do servidor mestre.
//              Além disso, se tudo estiver certo com a gravação, o servidor mestre sobreescreve na mensagem o tipo de request para "REPLICATION".
//              Com isso, ele replica a informação para os servidores UM e DOIS.
//              Quando receber o retorno dos servidores, é verificado se todos estão com REPLICATION_OK.
//              Se for o caso, ele devolve para o cliente que a requisião put funcionou. Se não, retorna uma mensagem vazia.
                case "PUT":
                    System.out.println(
                            "Cliente " + mensagem.getIpFrom() + ":" + mensagem.getPortFrom()
                            + " PUT key:" + mensagem.getKey()
                            + " value:" + mensagem.getValue()
                    );

                    optionalResponse = put(mensagem);

                    if(optionalResponse.isPresent()){
                        response = optionalResponse.get();
                        response.setRequest("REPLICATION");

                        optionalResponseServerOne = sendToOtherServers(response, mensagem.getIpServerOne(), mensagem.getPortServerOne());
                        optionalResponseServerTwo = sendToOtherServers(response, mensagem.getIpServerTwo(), mensagem.getPortServerTwo());

                        if(optionalResponseServerOne.get().getRequest().equals("REPLICATION_OK") &&
                                optionalResponseServerTwo.get().getRequest().equals("REPLICATION_OK")){
                            System.out.println(
                                    "Enviando PUT_OK ao Cliente " + response.getIpFrom() + ":" + response.getPortFrom()
                                            + " da key:" + response.getKey()
                                            + " ts:" + response.getTimestampServer()
                            );
                        }

                    } else {
                        response = new Mensagem();
                    }

                    sendBack(response);
                    break;

//              Caso a opção seja um REPLICATION, isso significa que o servidor mestre está recebendo uma replicação dos demais servidores.
//              Assim, primeiro é gravado no banco do servidor mestre a informação que chegou.
//              Depois ele exibe na console que está recebendo uma replication com as informações contidas na mensagem.
//              Após isso, o servidor mestre replica para os demais servidores a informação que chegou para que os mesmos consigam registrar nos seus bancos os dados.
//              Quando confirmado que todos os servidores receberam a replicação, o servidor mestre então devolve a resposta para quem o requisitou (os demais servidores).
//              Se nem todos os servidores retornarem a confirmação de que está ok a replicação, o servidor mestre exibe na console a mensagem FAILED REPLICATION.
                case "REPLICATION":
                    optionalResponse = put(mensagem);

                    System.out.println(
                            "REPLICATION key:" + optionalResponse.get().getKey()
                                    + " value:" + optionalResponse.get().getValue()
                                    + " ts:" + optionalResponse.get().getTimestampServer()
                    );

                    if(optionalResponse.isPresent()){
                        response = optionalResponse.get();
                        response.setRequest("REPLICATION");

                        optionalResponseServerOne = sendToOtherServers(response, mensagem.getIpServerOne(), mensagem.getPortServerOne());
                        optionalResponseServerTwo = sendToOtherServers(response, mensagem.getIpServerTwo(), mensagem.getPortServerTwo());

                        if(optionalResponseServerOne.get().getRequest().equals("REPLICATION_OK") &&
                                optionalResponseServerTwo.get().getRequest().equals("REPLICATION_OK")){
                                    sendBack(response);
                        }

                    } else {
                        System.out.println("FAILED REPLICATION");
                    }
                    break;
            }

        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

//  Aqui temos o método get(), no qual realiza a busca da chave no banco de dados do servidor mestre.
//  Ela funciona por meio de uma conexão com um banco de dados, que é estabelecida pelo serviço ServiceDatabase.
//  Nesse serviço, existem os métodos de get, put e update que o serviço disponibiliza para os servidores.
//  Junto a isso, depois de executar as ações com o banco, é verificado na mensagem o timestamp.
//  Aqui temos a lógica de validar o timestamp que veio do servidor e o que o cliente enviou.
//  Se o timestamp do servidor for maior ou igual ao timestamp do cliente, é setado na mensagem o status "OK".
//  Caso contrário, é setado na mensagem o status "TRY_OTHER_SERVER_OR_LATER"
    private Optional<Mensagem> get(Mensagem mensagem) {
        Optional<Mensagem> response;

        try{
            ServicesDatabase servicesDatabase = new ServicesDatabase();
            response = servicesDatabase.get(mensagem, table);

            if(response.get().getTimestampServer().isAfter(response.get().getTimestampClient()) || response.get().getTimestampServer().isEqual(response.get().getTimestampClient())) {
                response.get().setStatus("OK");
            } else {
                response.get().setStatus("TRY_OTHER_SERVER_OR_LATER");
            }

            return response;
        } catch (Exception e){
            return Optional.empty();
        }
    }

//  Aqui temos o método insertLocal. Ele funciona através dos serviços do ServiceDatabase.
//  Aqui ocorre o insert da informação na própria tabela do servidor mestre.
//  Ele primeiro busca se existe a chave. Se existir, ele atualiza os dados. Se não, cria um novo registro.
    private Optional<Mensagem> put(Mensagem mensagem) {
        Optional<Mensagem> findRegistry;
        Optional<Mensagem> response;

        try{
            ServicesDatabase servicesDatabase = new ServicesDatabase();
            findRegistry = servicesDatabase.get(mensagem, table);

            if(!findRegistry.isPresent()){
                response = servicesDatabase.create(mensagem, table);
            } else {
                response = servicesDatabase.update(mensagem, table);
            }

            return response;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

//  Aqui temos o retorno para o cliente que requisições algum dos métodos do servidor.
    private void sendBack(Mensagem response) throws IOException {
        try{
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(response);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

//  Aqui temos a conexão por meio dos sockets para que este servidor consiga se comunicar com os demais servidores.
    private Optional<Mensagem> sendToOtherServers(Mensagem mensagem, String ip, int port) throws IOException, ClassNotFoundException {
        try{
            Socket socketServerMaster = new Socket(ip, port);

            ObjectOutputStream output = new ObjectOutputStream(socketServerMaster.getOutputStream());
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(socketServerMaster.getInputStream());
            Mensagem response = (Mensagem) input.readObject();

            Optional<Mensagem> responseFromServers = Optional.of(response);

            socketServerMaster.close();

            return responseFromServers;
        } catch (Exception ex){
            return Optional.empty();
        }
    }
}
