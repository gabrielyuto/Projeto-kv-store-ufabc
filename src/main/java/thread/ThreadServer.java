package thread;

import clientes.Mensagem;
import db.ServicesDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadServer implements Runnable {
    private final Socket clientSocket;

    public ThreadServer(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        Mensagem response = null;

        try{
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            Mensagem mensagem = (Mensagem) input.readObject();

            if(mensagem.getRequest().equals("PUT")){
               response = put(mensagem);
            } else if (mensagem.getRequest().equals("GET")) {
               response = get(mensagem);
            }

            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.writeObject(response);

            } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Mensagem put(Mensagem mensagem) {
        Mensagem response = null;

        try{
            ServicesDatabase servicesDatabase = new ServicesDatabase();

            Mensagem findRegistry = servicesDatabase.get(mensagem);

            if(findRegistry == null){
                response = servicesDatabase.create(mensagem);
            } else {
                response = servicesDatabase.update(mensagem);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private Mensagem get(Mensagem mensagem) {
        ServicesDatabase servicesDatabase = new ServicesDatabase();

        return servicesDatabase.get(mensagem);
    }
}
