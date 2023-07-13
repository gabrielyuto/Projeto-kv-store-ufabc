package clientes;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteService {
    public Mensagem update(Mensagem mensagem, int port){
        Mensagem response = new Mensagem();

        try{
            Socket socket = new Socket("localhost", port);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            Mensagem received = (Mensagem) input.readObject();

            response.setKey(received.getKey());
            response.setValue(received.getValue());
            response.setStatus(received.getStatus());
            response.setTimestamp(received.getTimestamp());

        } catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public Mensagem get(Mensagem mensagem){
        Mensagem response = new Mensagem();

        try{
            Socket socket = new Socket("localhost", 10097);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            Mensagem received = (Mensagem) input.readObject();

            response.setKey(received.getKey());

        } catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }
}
