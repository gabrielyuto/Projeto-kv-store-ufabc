package servidor;

import clientes.Mensagem;
import thread.ThreadServidorMestre;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServidorMestre {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String ip;
        int port;

        System.out.println("IP SERVIDOR: ");
        ip = scanner.next();

        System.out.println("PORT SERVIDOR: ");
        port = scanner.nextInt();

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();

                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Mensagem mensagem = (Mensagem) input.readObject();

                mensagem.setIpServerMaster(ip);
                mensagem.setPortServerMaster(port);

                Thread thread = new Thread(new ThreadServidorMestre(socket, mensagem));
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
