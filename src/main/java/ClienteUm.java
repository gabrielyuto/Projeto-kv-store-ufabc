import mensagem.Mensagem;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

// Aqui temos o cliente um que será responsável por enviar as requisições para os servidores.
// Inicialmente, o cliente um precisa informar o IP e a PORTA dos três servidores.
// Depois de informar, é apresentado três opções de ação: INIT (1), GET (2) e PUT (3).
// No caso, antes de realizar as requisições, o cliente um precisa inicializar (1- INIT), no qual é definido o destino da requisição. Nesta etapa, é importante
// destacar que o cliente não saberá para qual IP e PORTA esta solicitando a requisição (o valor é escolhido randomicamente).
// Depois de inicializar, o cliente pode solicitar a busca por um valor, inserindo a chave.
// Ou pode incluir um valor no servidor, solicitando pelo método PUT e inserindo qual a KEY e VALUE.
public class ClienteUm {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Mensagem mensagem = new Mensagem();
        Optional<Mensagem> response;
        Optional<String> valueReturned;
        String ip_client, ip1, ip2, ip3, key, value;
        int port_client, port1, port2, port3, menu_choice;

        System.out.println("IP Servidor 1: ");
        ip1 = scanner.next();
        System.out.println("PORT Servidor 1: ");
        port1 = scanner.nextInt();

        System.out.println("IP Servidor 2: ");
        ip2 = scanner.next();
        System.out.println("PORT Servidor 2: ");
        port2 = scanner.nextInt();

        System.out.println("IP Servidor 3: ");
        ip3 = scanner.next();
        System.out.println("PORT Servidor 3: ");
        port3 = scanner.nextInt();

        String[] ips = {ip1,ip2, ip3};
        int[] ports = {port1,port2, port3};

//      Aqui é construído um loop para expor um painel com as informações sobre as três possibildades mencionadas anteriormente.
        while(true) {
            System.out.println("1-INIT | 2-GET | 3-PUT");
            menu_choice = scanner.nextInt();
            System.out.println();

            switch (menu_choice) {
//              Caso a opção escolhida seja inicializar, é definido aleatoriamente o ip e porta de destino.
                case 1:
                    Random random = new Random();
                    int indiceRandom = random.nextInt(ports.length);
                    ip_client = ips[indiceRandom];
                    port_client = ports[indiceRandom];

                    mensagem.setIpServerClientRequest(ip_client);
                    mensagem.setPortServerClientRequest(port_client);

                    break;
//              Caso a opção seja realizar um GET, é inserido na mensagem a chave que o cliente esta pedindo e qual o tipo de request para o servidor entender a requisição.
//              Além disso, junto a mensagem também é enviado o timestamp do momento que o cliente esta pedindo a informação.
//              A mensagem, depois de preparada, é enviada por meio do método "requestToServer", no qual criara a conexão com o servidor por meio de Sockets.
//              Depois de retornar, é analisado o conteudo da resposta. Se a mensagem estiver vazia, é escrito na console "NOT FOUND". Se não for este o caso, é verificado a regra do timestamp ("o cliente NUNCA deverá obter um value anterior ao que já viu"),
//              e então é escrito na console o resultado. Por fim, se a mensagem retornar sem ser alguma dessas regras, é escrito na console o retorno.
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
//              Caso a opção seja realizar um PUT, o cliente deve informar a chave e o valor que deseja inserir. Essas informações são inseridas na mensagem que vai para o servidor.
//              Da mesma forma que no método GET, é definido na mensagem o tipo de requisição para que o servir entenda como processar a informação.
//              Depois de pronta, a mensagem é dispara pelo método "requestToServer", como no GET.
//              Após o retorno da mensagem, é validado se existe ou não conteúdo na mensagem (confirmando se deu certo ou nao o UPDATE). Se existir, é exibido na console, se não, é escrito a mensagem "NOT UPDATED".
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

//  Este método é chamado durante os momentos em que são realizados os GETs e PUTs.
//  Ele serve como a comunicação TCP que o cliente um faz com os servidores utilizando os sockets, e depois de receber a informação do servidor, retorna a mensagem recebida.
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

