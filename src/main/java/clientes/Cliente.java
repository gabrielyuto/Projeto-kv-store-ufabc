package clientes;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

// Aqui temos o cliente que será responsável por enviar as requisições para os servidores.
// Inicialmente, é informado para o cliente três opções: Inicialização, Get e Put.
// No caso, antes de realizar as requisições, o cliente precisa inicializar o client (1- INIT), no qual é pedido o IP e a PORTA dos três servidores. Nesta etapa, é importante
// destacar que o cliente somente informa quais os ips e as portas, mas não saberá para qual esta solicitando a requisição (o valor é escolhido randomicamente).
// Depois de inicializar, o cliente pode solicitar a busca por um valor, inserindo a chave.
// Ou pode incluir um valor no servidor, solicitando pelo método PUT e inserindo qual o valor da chave do valor.
public class Cliente {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Mensagem mensagem = new Mensagem();
        Optional<Mensagem> response;
        Optional<String> valueReturned;
        String ip_client, ip1, ip2, ip3, key, value;
        int port_client, port1, port2, port3, menu_choice;

//        System.out.println("IP Servidor 1: ");
//        ip1 = scanner.next();
//        System.out.println("PORT Servidor 1: ");
//        port1 = scanner.nextInt();
//
//        System.out.println("IP Servidor 2: ");
//        ip2 = scanner.next();
//        System.out.println("PORT Servidor 2: ");
//        port2 = scanner.nextInt();
//
//        System.out.println("IP Servidor 3: ");
//        ip3 = scanner.next();
//        System.out.println("PORT Servidor 3: ");
//        port3 = scanner.nextInt();
//
//        String[] ips = {ip1,ip2, ip3};
//        int[] ports = {port1,port2, port3};

//      1. Aqui é construído um loop para expor um painel com as informações sobre as três possibildades mencioanda anteriormente.
        while(true) {
            System.out.println("1-INIT | 2-GET | 3-PUT");
            menu_choice = scanner.nextInt();
            System.out.println();

            switch (menu_choice) {
                case 1:
//                    Random random = new Random();
//                    int indiceRandom = random.nextInt(ports.length);
//                    ip_client = ips[indiceRandom];
//                    port_client = ports[indiceRandom];

                    mensagem.setIpServerClientRequest("localhost");
                    mensagem.setPortServerClientRequest(10097);

                    break;
                case 2:
                    key = scanner.next();

                    mensagem.setKey(key);
                    mensagem.setRequest("GET");
                    mensagem.setTimestampClient(LocalDateTime.now());

                    response = requestToServer(mensagem);

                    valueReturned = Optional.ofNullable(response.get().getValue());

                    if(!valueReturned.isPresent()){
                        System.out.println("NOT FOUND");
                    } else if(response.get().getStatus().equals("TRY_OTHER_SERVER_OR_LATER")) {
                        System.out.println("TRY_OTHER_SERVER_OR_LATER");
                    } else {
                        System.out.println(
                                "GET key: " + response.get().getKey()
                                        + " value: " + response.get().getValue()
                                        + " obtido do servidor " + response.get().getIpServerClientRequest() + ":" + response.get().getPortServerClientRequest()
                                        + ", meu timestamp " + response.get().getTimestampClient()
                                        + " e do servidor " + response.get().getTimestampServer()
                        );
                    }
                    break;

                case 3:
                    key = scanner.next();
                    value = scanner.next();

                    mensagem.setKey(key);
                    mensagem.setValue(value);
                    mensagem.setRequest("PUT");

                    response = requestToServer(mensagem);

                    valueReturned = Optional.ofNullable(response.get().getIpServerClientRequest());

                    if(!valueReturned.isPresent()){
                        System.out.println("NOT UPDATED");
                    }

                    if(response.get().getStatus().equals("PUT_OK")){
                        System.out.println(
                                "PUT_OK key: " + response.get().getKey()
                                        + " value: " + response.get().getValue()
                                        + " timestamp " + response.get().getTimestampServer()
                                        + " realizada no servidor " + response.get().getIpServerClientRequest() + ":" + response.get().getPortServerClientRequest()
                        );
                    } else {
                        System.out.println(mensagem.getStatus());
                    }
                    break;

                default:
                    break;
            }
        }
    }

//  2. Este método é chamado durante os momentos em que são realizados os GETs e PUTs.
//     Ele serve como a comunicação TCP que o cliente faz com os servidores utilizando os sockets, e depois de receber a informação do servidor, retorna a mensagem recebida.
    private static Optional<Mensagem> requestToServer(Mensagem mensagem){
        try{
            Socket socket = new Socket(mensagem.getIpServerClientRequest(), mensagem.getPortServerClientRequest());
            String ipServer = socket.getInetAddress().getHostAddress();
            int portServer = socket.getPort();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            Optional<Mensagem> response = Optional.of((Mensagem) input.readObject());

            response.get().setIpServerClientRequest(ipServer);
            response.get().setPortServerClientRequest(portServer);

            return response;

        } catch(Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }
}

