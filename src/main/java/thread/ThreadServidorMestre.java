package thread;

import clientes.Mensagem;
import db.ServicesDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

public class ThreadServidorMestre implements Runnable {
    private final Socket socket;
    private Mensagem mensagem;
    private Mensagem response;
    private Optional<Mensagem> optionalResponse;
    private final String table = "servidor_mestre";

    public ThreadServidorMestre(Socket socket, Mensagem mensagem){
        this.mensagem = mensagem;
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            switch (mensagem.getRequest()) {
                case "PUT":
                    System.out.println(
                            "Cliente " + mensagem.getIpFrom() + ":" + mensagem.getPortFrom()
                            + " PUT key:" + mensagem.getKey()
                            + " value:" + mensagem.getValue()
                    );

                    optionalResponse = put(mensagem);

                    if(optionalResponse.isPresent()){
                        response = optionalResponse.get();
                    } else {
                        response = new Mensagem();
                    }

                    sendBack(response);

                    response.setRequest("REPLICATION");
                    sendToOtherServers(response, mensagem.getIpServerOne(), mensagem.getPortServerOne());
                    sendToOtherServers(response, mensagem.getIpServerTwo(), mensagem.getPortServerTwo());

                    break;
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

                case "REPLICATION":
                    optionalResponse = put(mensagem);

                    if(optionalResponse.isPresent()){
                        response = optionalResponse.get();
                    } else {
                        response = new Mensagem();
                    }

                    response.setRequest("REPLICATION_OK");

                    sendBack(response);

                    System.out.println("REPLICATION_OK");

                    break;
                case "REPLICATION_OK":
                    System.out.println("REPLICATION_OK");
                    break;
            }

        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Optional<Mensagem> put(Mensagem mensagem) {
        Optional<Mensagem> findRegistry;
        Optional<Mensagem> response;

        try{
            ServicesDatabase servicesDatabase = new ServicesDatabase();

            findRegistry = servicesDatabase.get(mensagem, table);

            if(!findRegistry.isPresent()){
                response = servicesDatabase.create(mensagem, table);
            } else {
                response = servicesDatabase.update(mensagem, table);
            }

            return response;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Mensagem> get(Mensagem mensagem) {
        Optional<Mensagem> response;

        try{
            ServicesDatabase servicesDatabase = new ServicesDatabase();

            response = servicesDatabase.get(mensagem, table);

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

    private void sendBack(Mensagem response) throws IOException {
        try{
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(response);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private Mensagem sendToOtherServers(Mensagem mensagem, String ip, int port) throws IOException, ClassNotFoundException {
        try{
            Socket socketServerMaster = new Socket(ip, port);

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
