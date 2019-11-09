package mandatory.assigment;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    static volatile List<ChatHandler> clientList = new ArrayList<>();
    static volatile HashMap<String, Socket> nameList = new HashMap<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {

        //Opretter Server Socket
        ServerSocket serverSocket = new ServerSocket(1234);

        while (true) {

            System.out.println("Serveren venter på klient....");

            //Client ACK
            Socket client = serverSocket.accept();
            System.out.println("Forbindelse er oprettet mellem server og klient...");

            //Laver Client thread og tilføjer til List
            ChatHandler clientThread = new ChatHandler(client);

            clientList.add(clientThread);
            pool.execute(clientThread);
        }
    }
}
