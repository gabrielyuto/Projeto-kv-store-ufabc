import mensagem.Mensagem;
import thread.ThreadServidorUm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

//  Aqui temos a inicialização do servidor UM.
//  Em um primeiro momento, o servidor precisa das informações sobre qual o seu IP e PORTA e quais os mesmos do servidor mestre para que consiga se comunicar.
//  Após isso, através de um ServerSocket, o servidor entra num loop para atender as requisições que chegam até o mesmo.
//  Quando recebe uma chamada, ele cria uma thread para tratar da requisição.
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

                String ipFrom = socket.getInetAddress().getHostAddress();
                int portFrom = socket.getPort();

                mensagem.setIpFrom(ipFrom);
                mensagem.setPortFrom(portFrom);

                mensagem.setIpServerMaster(ipServerMaster);
                mensagem.setPortServerMaster(portServerMaster);

                Thread thread = new Thread(new ThreadServidorUm(socket, mensagem));
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
