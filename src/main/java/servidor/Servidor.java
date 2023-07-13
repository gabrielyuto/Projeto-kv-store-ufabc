package servidor;

import thread.ThreadServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(10097);

        try {
            while (true) {
                Socket socket = serverSocket.accept();

                Thread thread = new Thread(new ThreadServer(socket));
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
