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
                case "GET":
                    optionalResponse = get(mensagem);

                    if(optionalResponse.isPresent()){
                        if(optionalResponse.get().getStatus().equals("TRY_OTHER_SERVER_OR_LATER")){
                            response = new Mensagem();
                        } else {
                            response = optionalResponse.get();

                            System.out.println(
                                    "Cliente " + response.getIpFrom() + ":" + response.getPortFrom()
                                            + " GET key:" + response.getKey()
                                            + " ts:" + response.getTimestampClient()
                                            + ". Meu ts é " + response.getTimestampServer()
                                            + ", portanto devolvendo " + response.getValue()
                            );
                        }
                    } else {
                        response = new Mensagem();
                    }

                    sendBack(response);

                    break;
                case "PUT":
                    System.out.println(
                            "Encaminhando PUT key:" + mensagem.getKey() + " value:" + mensagem.getValue()
                    );

                    mensagem.setRequest("REPLICATION");

                    response = sendToServerMaster(mensagem);

                    insertLocal(response);

                    sendBack(response);

                    mensagem.setRequest("REPLICATION_OK");
                    response = sendToServerMaster(mensagem);

                    break;
                case "REPLICATION":
                    System.out.println(
                            "REPLICATION key:" + mensagem.getKey()
                                    + " value:" + mensagem.getValue()
                                    + " ts:" + mensagem.getTimestampServer()
                    );

                    insertLocal(mensagem);

                    mensagem.setRequest("REPLICATION_OK");
                    sendBack(mensagem);
                    break;
            }
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Optional<Mensagem> get(Mensagem mensagem) {
        try{
            ServicesDatabase servicesDatabase = new ServicesDatabase();

            Optional<Mensagem> response = servicesDatabase.get(mensagem, tableMaster);

            if(response.get().getTimestampServer().isAfter(response.get().getTimestampClient()) ||
                    response.get().getTimestampServer().isEqual(response.get().getTimestampClient())) {
                response.get().setStatus("TRY_OTHER_SERVER_OR_LATER");
            } else {
                response.get().setStatus("OK");
            }

            return response;
        } catch (Exception e){
            return Optional.empty();
        }
    }

    private void insertLocal(Mensagem mensagem){
        try{
            ServicesDatabase servicesDatabase = new ServicesDatabase();

            servicesDatabase.insertLocal(mensagem, tableOne);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendBack(Mensagem response) throws IOException {
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(response);
    }

    private Mensagem sendToServerMaster(Mensagem mensagem) throws IOException, ClassNotFoundException {
        try{
            Socket socketServerMaster = new Socket(mensagem.getIpServerMaster(), mensagem.getPortServerMaster());

            ObjectOutputStream output = new ObjectOutputStream(socketServerMaster.getOutputStream());
            output.writeObject(mensagem);

            ObjectInputStream input = new ObjectInputStream(socketServerMaster.getInputStream());
            Mensagem response = (Mensagem) input.readObject();

            return response;
        } catch (Exception ex){
            return null;
        }
    }
}
