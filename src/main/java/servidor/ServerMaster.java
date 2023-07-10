package servidor;

import thread.ServerMasterThread;

import java.io.IOException;

public class ServerMaster {
    public static void main(String[] args) throws IOException {

        ServerMasterThread serverThread = new ServerMasterThread(10097);
        serverThread.start();
    }
}
