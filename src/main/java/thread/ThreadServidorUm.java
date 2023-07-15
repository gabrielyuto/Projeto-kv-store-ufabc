package thread;

import clientes.Mensagem;
import db.ServicesDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

public class ThreadServidorUm implements Runnable {
    private final String tableMaster = "servidor_mestre";
    private final String tableOne = "servidor_um";
    private final Socket socket;
    private Mensagem mensagem;
    private Mensagem response;
    private Optional<Mensagem> optionalResponse;

    public ThreadServidorUm(Socket socket, Mensagem mensagem){
        this.mensagem = mensagem;
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            switch (mensagem.getRequest()) {
                case "PUT":
                    System.out.println(
                        "Encaminhando PUT key:" + mensagem.getKey() + " value:" + mensagem.getValue()
                    );

                    resend(mensagem);
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
                    insertLocal(mensagem);

                    Mensagem resposta = sendToServerMaster(mensagem);
                    sendBackToClient(resposta);

                    mensagem.setRequest("REPLICATION_OK");

                    sendToServerMaster(mensagem);
                    break;
            }
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void resend(Mensagem mensagem) {
        try{
            mensagem.setRequest("REPLICATION");

            sendToServerMaster(mensagem);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void insertLocal(Mensagem mensagem){
        try{
            ServicesDatabase servicesDatabase = new ServicesDatabase();

            servicesDatabase.insertLocal(mensagem, tableOne);

            mensagem.setRequest("REPLICATION_OK");

            sendToServerMaster(mensagem);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendBackToClient(Mensagem response) throws IOException {
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(response);
    }

    private Mensagem sendToServerMaster(Mensagem mensagem) throws IOException, ClassNotFoundException {
        try{
            Socket socketServerMaster = new Socket(mensagem.getIpServerMaster(), mensagem.getPortServerMaster());

            ObjectOutputStream output = new ObjectOutputStream(socketServerMaster.getOutputStream());
            output.writeObject(mensagem);
            output.flush();

            ObjectInputStream input = new ObjectInputStream(socketServerMaster.getInputStream());
            Mensagem response = (Mensagem) input.readObject();

            return response;
        } catch (Exception ex){
            return null;
        }
    }
}
