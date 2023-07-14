package servidor;

import thread.ThreadServidorUm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServidorUm {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("IP SERVIDOR: ");
        String ip = scanner.next();

        System.out.println("PORT SERVIDOR: ");
        int port = scanner.nextInt();

        System.out.println("IP LIDER: ");
        String ipLeader = scanner.next();

        System.out.println("PORT LIDER: ");
        int portLeader = scanner.nextInt();

        ServerSocket serverSocket = new ServerSocket(port);

        try {
            while (true) {
                Socket socket = serverSocket.accept();

                Thread thread = new Thread(new ThreadServidorUm(socket, ipLeader, portLeader));
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
