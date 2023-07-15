package clientes;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Mensagem mensagem = new Mensagem();
        Mensagem response = new Mensagem();

        String ipClient, ip1, ip2, ip3, key, value;
        int portClient, port1, port2, port3;

        while(true) {
            System.out.println("1-INIT | 2-GET | 3-PUT");
            int menu_choice = scanner.nextInt();
            System.out.println();

            switch (menu_choice) {
                case 1:
//                    System.out.println("IP Cliente: ");
//                    ipClient = scanner.next();
//                    System.out.println("PORT Cliente: ");
//                    portClient = scanner.nextInt();
//
//                    System.out.println("IP Servidor 1: ");
//                    ip1 = scanner.next();
//                    System.out.println("PORT Servidor 1: ");
//                    port1 = scanner.nextInt();

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
                    mensagem.setIpServerClientRequest("localhost");
                    mensagem.setPortServerClientRequest(10098);
                    break;

                case 2:
                    key = scanner.next();

                    mensagem.setKey(key);
                    mensagem.setRequest("GET");
                    mensagem.setTimestampClient(LocalDateTime.now());

                    response = get(mensagem);

                    if(response == null){
                        System.out.println("NOT FOUND");
                    } else {
                        System.out.println(
                                "GET key: " + response.getKey()
                                        + " value " + response.getValue()
                                        + " obtido do servidor " + response.getIpServerClientRequest() + ":" + response.getPortServerClientRequest()
                                        + ", meu timestamp " + response.getTimestampClient()
                                        + " e do servidor " + response.getTimestampServer()
                        );
                    }
                    break;

                case 3:
                    key = scanner.next();
                    value = scanner.next();

                    mensagem.setKey(key);
                    mensagem.setValue(value);
                    mensagem.setRequest("PUT");

                    response = update(mensagem);

                    if(response == null){
                        System.out.println("NOT UPDATED");
                    }

                    if(response.getStatus().equals("PUT_OK")){
                        System.out.println(
                                "PUT_OK key: " + response.getKey()
                                        + " value " + response.getValue()
                                        + " timestamp " + response.getTimestampServer()
                                        + " realizada no servidor " + response.getIpServerClientRequest()
                                        + ":" + response.getPortServerClientRequest()
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

    private static Mensagem get(Mensagem mensagem) {
        try{
            Socket socket = new Socket(mensagem.getIpServerClientRequest(), mensagem.getPortServerClientRequest());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            return (Mensagem) input.readObject();

        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Mensagem update(Mensagem mensagem){
        try{
            Socket socket = new Socket("localhost", mensagem.getPortServerClientRequest());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            return (Mensagem) input.readObject();

        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

