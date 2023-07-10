package services;

import clientes.Cliente;
import clientes.Mensagem;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteService {
    public Mensagem update(Mensagem mensagem){
        Mensagem response = new Mensagem();

        try{
            Socket socket = new Socket("localhost", 10097);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            Mensagem received = (Mensagem) input.readObject();

            response.setStatus(received.getStatus());

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

            response.setStatus(received.getStatus());

        } catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }
}
