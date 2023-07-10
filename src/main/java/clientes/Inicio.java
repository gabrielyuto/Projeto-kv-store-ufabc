package clientes;

import services.ClienteService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Inicio {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("1-INIT | 2-PUT | 3-GET");
            int menu_choice = scanner.nextInt();
            System.out.println();

            if(menu_choice == 1){
                List<Integer> list = new ArrayList<>();
                list.add(10097);
                list.add(10098);
                list.add(10099);

                Integer selected_port = list.get(new Random().nextInt(list.size()));
            }
            else if(menu_choice == 2){
                String key = scanner.next();
                String value = scanner.next();

                Mensagem mensagem = new Mensagem();
                mensagem.setKey(key);
                mensagem.setValue(value);

                ClienteService clienteService = new ClienteService();
                Mensagem response = clienteService.update(mensagem);

                System.out.println(response.getStatus());
            }
            else if(menu_choice == 3){
                String key = scanner.next();

                Mensagem mensagem = new Mensagem();
                mensagem.setKey(key);

                ClienteService clienteService = new ClienteService();
                Mensagem response = clienteService.get(mensagem);

                System.out.println(response.getKey());
            }
            else {
                System.exit(0);
            }
        }
    }
}

