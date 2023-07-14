package thread;

import clientes.Mensagem;
import db.ServicesDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadServidorMestre implements Runnable {
    private final Socket clientSocket;
    private final String ipServerOne;
    private final int portServerOne;
    private final String table = "servidor_mestre";

    public ThreadServidorMestre(Socket clientSocket, String ipServerOne, int portServerOne){
        this.clientSocket = clientSocket;
        this.ipServerOne = ipServerOne;
        this.portServerOne = portServerOne;
    }

    @Override
    public void run() {
        Mensagem response = null;

        try{
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            Mensagem mensagem = (Mensagem) input.readObject();

            if(mensagem.getRequest().equals("PUT")){
                System.out.println(
                        "Cliente " + mensagem.getIpClient() + ":" + mensagem.getPortClient()
                        + " PUT key:" + mensagem.getKey()
                        + " value:" + mensagem.getValue()
                );

                response = put(mensagem);

                sendToServers(response);

            } else if (mensagem.getRequest().equals("GET")) {
                response = get(mensagem);

                System.out.println(
                        "Cliente " + response.getIpClient() + ":" + response.getPortClient()
                        + " GET key:" + response.getKey()
                        + "ts:" + response.getTimestampClient()
                        + ". Meu ts é " + response.getTimestampServer()
                        + ", portanto devolvendo " + response.getValue()
                );
            } else if(mensagem.getRequest().equals("REPLICATION_OK")){
                response = mensagem;
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

            Mensagem findRegistry = servicesDatabase.get(mensagem, table);

            if(findRegistry == null){
                response = servicesDatabase.create(mensagem, table);
            } else {
                response = servicesDatabase.update(mensagem, table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private Mensagem get(Mensagem mensagem) {
        ServicesDatabase servicesDatabase = new ServicesDatabase();

        return servicesDatabase.get(mensagem, table);
    }

    private void sendToServers(Mensagem mensagem){
        Mensagem received = null;

        try{
            Socket socketServerOne = new Socket(ipServerOne, portServerOne);

            ObjectOutputStream output = new ObjectOutputStream(socketServerOne.getOutputStream());
            mensagem.setRequest("REPLICATION");
            output.writeObject(mensagem);

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
