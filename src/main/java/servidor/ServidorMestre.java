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
        String ip, ip1;
        int port, port1;

        System.out.println("IP SERVIDOR: ");
        ip = scanner.next();

        System.out.println("PORT SERVIDOR: ");
        port = scanner.nextInt();

        System.out.println("IP SERVIDOR UM: ");
        ip1 = scanner.next();

        System.out.println("PORT SERVIDOR UM: ");
        port1 = scanner.nextInt();

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();

                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Mensagem mensagem = (Mensagem) input.readObject();

                String ipFrom = socket.getInetAddress().getHostAddress();
                int portFrom = socket.getPort();

                mensagem.setIpFrom(ipFrom);
                mensagem.setPortFrom(portFrom);

                mensagem.setIpServerOne(ip1);
                mensagem.setPortServerOne(port1);

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
