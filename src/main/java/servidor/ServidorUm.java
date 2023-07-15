package servidor;

import clientes.Mensagem;
import thread.ThreadServidorUm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServidorUm {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String ip, ipServerMaster;
        int port, portServerMaster;

        System.out.println("IP SERVIDOR: ");
        ip = scanner.next();

        System.out.println("PORT SERVIDOR: ");
        port = scanner.nextInt();

        System.out.println("IP Servidor Mestre: ");
        ipServerMaster = scanner.next();

        System.out.println("PORT Servidor Mestre: ");
        portServerMaster = scanner.nextInt();

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();

                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Mensagem mensagem = (Mensagem) input.readObject();

                if(mensagem.getRequest().equals("PUT") || mensagem.getRequest().equals("GET")){
                    String ipClient = socket.getInetAddress().getHostAddress();
                    int portClient = socket.getPort();

                    mensagem.setIpClient(ipClient);
                    mensagem.setPortClient(portClient);
                }

                mensagem.setIpServerOthers(ip);
                mensagem.setPortServerOthers(port);
                mensagem.setIpServerMaster(ipServerMaster);
                mensagem.setPortServerMaster(portServerMaster);

                Thread thread = new Thread(new ThreadServidorUm(socket, mensagem));
                thread.start();

                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
