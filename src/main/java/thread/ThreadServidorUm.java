package thread;

import clientes.Mensagem;
import db.ServicesDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadServidorUm implements Runnable {
    private final Socket clientSocket;
    private final String ipLeader;
    private final int portLeader;
    private final String table = "servidor_um";

    public ThreadServidorUm(Socket clientSocket, String ipLeader, int portLeader){
        this.clientSocket = clientSocket;
        this.ipLeader = ipLeader;
        this.portLeader = portLeader;
    }

    @Override
    public void run() {
        Mensagem response = null;

        try{
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            Mensagem mensagem = (Mensagem) input.readObject();

            if(mensagem.getRequest().equals("PUT")){
                System.out.println(
                        "Encaminhando PUT key:" + mensagem.getKey() + " value:" + mensagem.getValue()
                );

                put(mensagem);

                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                output.writeObject(response);

            } else if (mensagem.getRequest().equals("GET")) {
                response = get(mensagem);

                System.out.println(
                        "Cliente " + response.getIpClient() + ":" + response.getPortClient()
                        + " GET key:" + response.getKey()
                        + "ts:" + response.getTimestampClient()
                        + ". Meu ts é " + response.getTimestampServer()
                        + ", portanto devolvendo " + response.getValue()
                );

                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                output.writeObject(response);

            } else if (mensagem.getRequest().equals("REPLICATION")) {
                insertLocal(mensagem);
            }

            } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void put(Mensagem mensagem) {
        try{
            Socket socket = new Socket(ipLeader, portLeader);

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(mensagem);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private Mensagem get(Mensagem mensagem) {
        ServicesDatabase servicesDatabase = new ServicesDatabase();

        return servicesDatabase.get(mensagem, table);
    }

    private void insertLocal(Mensagem mensagem){
        ServicesDatabase servicesDatabase = new ServicesDatabase();

        servicesDatabase.insertLocal(mensagem, table);

        try{
            Socket socket = new Socket(ipLeader, portLeader);

            mensagem.setRequest("REPLICATION_OK");
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(mensagem);

        } catch(Exception e){
            e.printStackTrace();
        }

    }
}
