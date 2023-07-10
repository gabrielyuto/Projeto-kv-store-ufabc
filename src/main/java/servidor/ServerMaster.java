package servidor;

import clientes.Mensagem;
import services.ServicesDatabase;
import thread.ServerMasterThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMaster {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(10097);
        Socket socket = serverSocket.accept();

        update(socket);

        ServerMasterThread serverThread = new ServerMasterThread(10097);
        serverThread.start();
    }

    private static void update(Socket socket) {
        try{
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            Mensagem mensagem = (Mensagem) input.readObject();

            ServicesDatabase servicesDatabase = new ServicesDatabase();
            Mensagem response = servicesDatabase.update(mensagem);

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
