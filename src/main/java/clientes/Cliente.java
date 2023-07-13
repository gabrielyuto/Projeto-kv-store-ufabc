package clientes;

import java.io.IOException;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        Mensagem mensagem = new Mensagem();

        while(true) {
            System.out.println("1-INIT | 2-PUT | 3-GET");
            int menu_choice = scanner.nextInt();
            System.out.println();

            if(menu_choice == 1){
                int port = scanner.nextInt();
                String ip = scanner.next();

                mensagem.setIp(ip);
                mensagem.setPort(port);
            }
            else if(menu_choice == 2){
                String key = scanner.next();
                String value = scanner.next();

                mensagem.setKey(key);
                mensagem.setValue(value);
                mensagem.setRequest("PUT");

                ClienteService clienteService = new ClienteService();
                Mensagem response = clienteService.update(mensagem, mensagem.getPort());

                System.out.println(
                        "PUT_OK key: " + response.getKey()
                        + " value " + response.getValue()
                        + " timestamp " + response.getTimestamp()
                        + " realizada no servidor " + response.getIp()
                        + ":" + response.getPort());
            }
            else if(menu_choice == 3){
                String key = scanner.next();

                mensagem.setKey(key);
                mensagem.setRequest("GET");

                ClienteService clienteService = new ClienteService();
                Mensagem response = clienteService.get(mensagem);

                System.out.println(
                        "GET key: " + response.getKey()
                        + " value " + response.getValue()
                        + " obtido do servidor " + response.getIp()
                        + ":" + response.getPort()
                        + ", meu timestamp " + response.getTimestamp()
                        + " e do servidor "
                        + response.getTimestamp());
            }
            else {
                System.exit(0);
            }
        }
    }
}

