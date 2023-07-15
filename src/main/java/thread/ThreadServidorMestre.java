package thread;

import clientes.Mensagem;
import db.ServicesDatabase;

import java.io.IOException;
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
                            "Cliente " + mensagem.getIpClient() + ":" + mensagem.getPortClient()
                            + " PUT key:" + mensagem.getKey()
                            + " value:" + mensagem.getValue()
                    );

                    optionalResponse = put(mensagem);

                    if(optionalResponse.isPresent()){
                        response = optionalResponse.get();
                    } else {
                        response = null;
                    }

                    sendBack(response);

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

                    sendBack(response);

                    break;

                case "REPLICATION":
                    System.out.println("Replicação: ");

                    optionalResponse = put(mensagem);

                    if(optionalResponse.isPresent()){
                        response = optionalResponse.get();
                    } else {
                        response = null;
                    }

                    response.setRequest("REPLICATION");
                    sendBack(response);

                    break;
                case "REPLICATION_OK":
                    mensagem.setRequest("REPLICATION_OK");

                    sendBack(mensagem);

                    System.out.println("REPLICATION_OK");
                    break;
            }

        } catch (IOException ex) {
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

            return response;
        } catch (Exception e){
            return Optional.empty();
        }
    }

    private void sendBack(Mensagem response) throws IOException {
        try{
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(response);
            output.flush();

            output.close();
            socket.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
