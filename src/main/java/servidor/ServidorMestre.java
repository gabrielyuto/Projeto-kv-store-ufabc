package servidor;

import thread.ThreadServidorMestre;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServidorMestre {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("IP SERVIDOR: ");
        String ip = scanner.next();

        System.out.println("PORT SERVIDOR: ");
        int port = scanner.nextInt();

        System.out.println("IP Servidor Um: ");
        String ipServerOne = scanner.next();

        System.out.println("PORT Servidor Um: ");
        int portServerOne = scanner.nextInt();

        ServerSocket serverSocket = new ServerSocket(port);

        try {
            while (true) {
                Socket socket = serverSocket.accept();

                Thread thread = new Thread(new ThreadServidorMestre(socket, ipServerOne, portServerOne));
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
