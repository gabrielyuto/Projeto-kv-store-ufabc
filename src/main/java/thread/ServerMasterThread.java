package thread;

import clientes.Mensagem;
import services.ServicesDatabase;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMasterThread extends Thread {
    private int port;

    public ServerMasterThread(int port){
        this.port = port;
    }

    @Override
    public void run() {
        try{
            ServerSocket serverSocket = new ServerSocket(port);

            while(true){
                Socket socket = serverSocket.accept();

                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Mensagem mensagem = (Mensagem) input.readObject();

                ServicesDatabase servicesDatabase = new ServicesDatabase();
                Mensagem response = servicesDatabase.update(mensagem);

                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.writeObject(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
