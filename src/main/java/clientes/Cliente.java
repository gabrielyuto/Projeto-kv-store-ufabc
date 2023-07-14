package clientes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        Mensagem mensagem = new Mensagem();
        String ip;
        int port;

        while(true) {
            System.out.println("1-INIT | 2-GET | 3-PUT");
            int menu_choice = scanner.nextInt();
            System.out.println();

            if(menu_choice == 1){
                System.out.println("IP Cliente: ");
                String ipClient = scanner.next();
                System.out.println("PORT Cliente: ");
                int portClient = scanner.nextInt();

                System.out.println("IP Servidor 1: ");
                String ip1 = scanner.next();
                System.out.println("PORT Servidor 1: ");
                int port1 = scanner.nextInt();

                System.out.println("IP Servidor 2: ");
                String ip2 = scanner.next();
                System.out.println("PORT Servidor 2: ");
                int port2 = scanner.nextInt();

//                System.out.println("IP Servidor 3: ");
//                String ip3 = scanner.next();
//                System.out.println("PORT Servidor 3: ");
//                int port3 = scanner.nextInt();

                String[] ips = {ip1,ip2};
                int[] ports = {port1,port2};

                Random random = new Random();
                int indiceAleatorio = random.nextInt(ports.length);
                ip = ips[indiceAleatorio];
                port = ports[indiceAleatorio];

                mensagem.setIpClient(ipClient);
                mensagem.setPortClient(portClient);
                mensagem.setIpServer(ip);
                mensagem.setPortServer(port);
            }
            else if(menu_choice == 2){
                String key = scanner.next();

                mensagem.setKey(key);
                mensagem.setRequest("GET");
                mensagem.setTimestampClient(LocalDateTime.now());

                Mensagem response = get(mensagem);

                System.out.println(
                    "GET key: " + response.getKey()
                    + " value " + response.getValue()
                    + " obtido do servidor " + response.getIpServer() + ":" + response.getPortServer()
                    + ", meu timestamp " + response.getTimestampClient()
                    + " e do servidor " + response.getTimestampServer()
                );
            }
            else if(menu_choice == 3){
                String key = scanner.next();
                String value = scanner.next();

                mensagem.setKey(key);
                mensagem.setValue(value);
                mensagem.setRequest("PUT");

                Mensagem response = update(mensagem);

                if(response.getStatus().equals("PUT_OK")){
                    System.out.println(
                        "PUT_OK key: " + response.getKey()
                        + " value " + response.getValue()
                        + " timestamp " + response.getTimestampServer()
                        + " realizada no servidor " + response.getIpServer()
                        + ":" + response.getPortServer()
                    );
                } else {
                    System.out.println(mensagem.getStatus());
                }

            }
            else {
                System.exit(0);
            }
        }
    }

    private static Mensagem get(Mensagem mensagem) {
        Mensagem received = null;
        try{
            Socket socket = new Socket(mensagem.getIpServer(), mensagem.getPortServer());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            received = (Mensagem) input.readObject();
        } catch(Exception e){
            e.printStackTrace();
        }

        return received;
    }

    private static Mensagem update(Mensagem mensagem){
        Mensagem response = null;

        try{
            Socket socket = new Socket("localhost", mensagem.getPortServer());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            response = (Mensagem) input.readObject();

        } catch(Exception e){
            e.printStackTrace();
        }

        return response;
    }
}

