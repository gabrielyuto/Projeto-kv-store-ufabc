package thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket socket;

    public ServerThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            System.out.println("THREAD One");
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String x;
            while((x = bufferedReader.readLine()) != null){
                System.out.println("Servidor: " + x);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
