package thread;

import clientes.Mensagem;
import db.ServicesDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

//  Essa é a thread do servidor DOIS.
//  Quando é inicializada, ela recebe o socket e a mensagem.
//  Dentro da mensagem, existe o conteúdo referente a qual o tipo de requisição que esta chegando, podendo ser um GET ou um PUT (quando é o Cliente que chama o servidor),
//  ou um REPLICATION (quando o servidor mestre replicar a informação para os demais servidores).
public class ThreadServidorDois implements Runnable {
    private final Socket socket;
    private final String tableTwo = "servidor_dois";
    private Mensagem mensagem;
    private Mensagem response;
    private Optional<Mensagem> optionalResponse;

    public ThreadServidorDois(Socket socket, Mensagem mensagem){
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
//              Caso a requisição seja um PUT, primeiro o servidor exibe uma mensagem de que encaminhará a requisição.
//              Depois, ele sobreescreve na mensagem o tipo de requisição para um REPLICATIOM.
//              Essa etapa é importante porque é dessa forma que o servidor mestre entenderá como processar a mensagem.
//              Depois disso, como o servidor atual nao tem responsabilidade sobre o PUT, ele encaminha para o servidor mestre através do método "sendToServerMaster".
//              Quando receber o retorno do mestre, ele devolve para o cliente a informação.
                case "PUT":
                    System.out.println(
                            "Encaminhando PUT key:" + mensagem.getKey() + " value:" + mensagem.getValue()
                    );

                    mensagem.setRequest("REPLICATION");
                    response = sendToServerMaster(mensagem);

                    sendBack(response);
                    break;
//              Caso a opção seja um REPLICATION, isso significa que o servidor dois está recebendo uma replicação do servidor mestre.
//              Dessa forma, ele primeiro exibe na console que está recebendo uma replication com as informações contidas na mensagem.
//              Depois guardar a informação na tabela local através do "insertLocal()" (essa tabela é referente ao servidor dois).
//              E por fim, envia para o servidor mestre que a replicação funcionou (REPLICATION_OK).
                case "REPLICATION":
                    System.out.println(
                            "REPLICATION key:" + mensagem.getKey()
                                    + " value:" + mensagem.getValue()
                                    + " ts:" + mensagem.getTimestampServer()
                    );

                    insertLocal(mensagem);

                    mensagem.setRequest("REPLICATION_OK");
                    sendBack(mensagem);
                    break;
            }
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

//  Aqui temos o método get(), no qual realiza a busca da chave no banco de dados do servidor dois.
//  Ela funciona por meio de uma conexão com um banco de dados, que é estabelecida pelo serviço ServiceDatabase.
//  Nesse serviço, existem os métodos de get, put e update que o serviço disponibiliza para os servidores.
//  Junto a isso, depois de executar as ações com o banco, é verificado na mensagem o timestamp.
//  Aqui temos a lógica de validar o timestamp que veio do servidor e o que o cliente enviou.
//  Se o timestamp do servidor for maior ou igual ao timestamp do cliente, é setado na mensagem o status "OK".
    private Optional<Mensagem> get(Mensagem mensagem) {
        try{
            ServicesDatabase servicesDatabase = new ServicesDatabase();

            Optional<Mensagem> response = servicesDatabase.get(mensagem, tableTwo);

            if(response.get().getTimestampServer().isAfter(response.get().getTimestampClient()) ||
                    response.get().getTimestampServer().isEqual(response.get().getTimestampClient())) {
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
//  Aqui ocorre o insert da informação repassada pelo replication do servidor mestre na própria tabela do servidor dois.
//  Ele primeiro busca se existe a chave. Se existir, ele atualiza os dados. Se não, cria um novo registro.
    private void insertLocal(Mensagem mensagem){
        try{
            Optional<Mensagem> findRegistry;

            ServicesDatabase servicesDatabase = new ServicesDatabase();

            findRegistry = servicesDatabase.get(mensagem, tableTwo);

            if(!findRegistry.isPresent()){
                servicesDatabase.create(mensagem, tableTwo);
            } else {
                servicesDatabase.update(mensagem, tableTwo);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

//  Aqui temos o retorno para o cliente que requisições algum dos métodos do servidor.
    private void sendBack(Mensagem response) throws IOException {
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(response);
    }

//  Aqui temos a conexão por meio dos sockets para que este servidor consiga se comunicar com o servidor mestre.
    private Mensagem sendToServerMaster(Mensagem mensagem) throws IOException, ClassNotFoundException {
        try{
            Socket socketServerMaster = new Socket(mensagem.getIpServerMaster(), mensagem.getPortServerMaster());

            ObjectOutputStream output = new ObjectOutputStream(socketServerMaster.getOutputStream());
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(socketServerMaster.getInputStream());
            Mensagem response = (Mensagem) input.readObject();

            return response;
        } catch (Exception ex){
            return null;
        }
    }
}
