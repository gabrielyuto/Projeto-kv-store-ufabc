package servidor;

import thread.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTwo {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(10099);
        Socket socket = serverSocket.accept();
        System.out.println("cliente conectou");

        ServerThread serverThread = new ServerThread(socket);
        serverThread.start();
    }
}
