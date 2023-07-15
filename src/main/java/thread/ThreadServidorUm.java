package thread;

import clientes.Mensagem;
import db.ServicesDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

public class ThreadServidorUm implements Runnable {
    private final Socket clientSocket;
    private final String tableMaster = "servidor_mestre";
    private final String tableOne = "servidor_um";
    private Mensagem mensagem;
    private Mensagem response;
    private Optional<Mensagem> optionalResponse;


    public ThreadServidorUm(Socket clientSocket, Mensagem mensagem){
        this.clientSocket = clientSocket;
        this.mensagem = mensagem;
    }

    @Override
    public void run() {
        try{
            switch (mensagem.getRequest()) {
                case "PUT":
                    System.out.println(
                        "Encaminhando PUT key:" + mensagem.getKey() + " value:" + mensagem.getValue()
                    );

                    optionalResponse = put(mensagem);

                    if(optionalResponse.isPresent()){
                        Optional<Mensagem> retorno = insertLocal(optionalResponse.get());
                        if(retorno.get().getRequest().equals("REPLICATION_OK")){
                            response = retorno.get();
                        }
                    } else {
                        response = null;
                    }

                    sendBackToClient(response);

                    break;
                case "GET":
                    optionalResponse = get(mensagem);

                    if(optionalResponse.isPresent()){
                        response = optionalResponse.get();

                        System.out.println(
                                "Cliente " + response.getIpClient() + ":" + response.getPortClient()
                                        + " GET key:" + response.getKey()
                                        + "ts:" + response.getTimestampClient()
                                        + ". Meu ts é " + response.getTimestampServer()
                                        + ", portanto devolvendo " + response.getValue()
                        );
                    } else {
                        response = null;
                    }

                    sendBackToClient(response);

                    break;
                case "REPLICATION":
                    break;
                case "REPLICATION_OK":
                    break;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Optional<Mensagem> put(Mensagem mensagem) {
        try{
            mensagem.setRequest("REPLICATION");

            Optional<Mensagem> response = sendToServerMaster(mensagem);

            return response;

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Mensagem> get(Mensagem mensagem) {
        try{
            ServicesDatabase servicesDatabase = new ServicesDatabase();

            Optional<Mensagem> response = servicesDatabase.get(mensagem, tableMaster);

            return response;
        } catch (Exception e){
            return Optional.empty();
        }
    }

    private Optional<Mensagem> insertLocal(Mensagem mensagem){
        try{
            ServicesDatabase servicesDatabase = new ServicesDatabase();

            servicesDatabase.insertLocal(mensagem, tableOne);

            mensagem.setRequest("REPLICATION_OK");

            Optional<Mensagem> response = sendToServerMaster(mensagem);

            return response;
        } catch(Exception e){
            return Optional.empty();
        }
    }

    private void sendBackToClient(Mensagem response) throws IOException {
        ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
        output.writeObject(response);
    }

    private Optional<Mensagem> sendToServerMaster(Mensagem mensagem) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(mensagem.getIpServerMaster(), mensagem.getPortServerMaster());

        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(mensagem);

        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        response = (Mensagem) input.readObject();

        return Optional.of(response);
    }
}
