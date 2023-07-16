package clientes;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Mensagem mensagem = new Mensagem();
        Optional<Mensagem> response;
        Optional<String> valueReturned;

        String ipClient, ip1, ip2, ip3, key, value;
        int portClient, port1, port2, port3;

        while(true) {
            System.out.println("1-INIT | 2-GET | 3-PUT");
            int menu_choice = scanner.nextInt();
            System.out.println();

            switch (menu_choice) {
                case 1:

                System.out.println("IP Servidor 1: ");
                ip1 = scanner.next();
                System.out.println("PORT Servidor 1: ");
                port1 = scanner.nextInt();

//                System.out.println("IP Servidor 2: ");
//                ip2 = scanner.next();
//                System.out.println("PORT Servidor 2: ");
//                port2 = scanner.nextInt();

//                System.out.println("IP Servidor 3: ");
//                ip3 = scanner.next();
//                System.out.println("PORT Servidor 3: ");
//                port3 = scanner.nextInt();

//                String[] ips = {ip1,ip2, ip3};
//                int[] ports = {port1,port2, port3};
//
//                Random random = new Random();
//                int indiceAleatorio = random.nextInt(ports.length);
//                ip = ips[indiceAleatorio];
//                port = ports[indiceAleatorio];

//                    mensagem.setIpClient(ipClient);
//                    mensagem.setPortClient(portClient);
                    mensagem.setIpServerClientRequest(ip1);
                    mensagem.setPortServerClientRequest(port1);
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

