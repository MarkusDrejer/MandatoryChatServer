package mandatory.assigment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    public static void main(String[] args) throws IOException {

        final String IP = "127.0.0.1";
        final int port = 1234;
        Socket socket = new Socket(IP, port);

        AtomicBoolean connected = new AtomicBoolean(false);

        BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

        Thread messageToServer = new Thread(() -> {
            while (true) {
                try {
                    System.out.print(">> ");
                    String msg = keyboardInput.readLine();
                    if (!connected.get()) {
                        msg = "JOIN " + msg + ", " + IP + ":" + port;
                    }
                    output.println(msg);
                } catch (IOException error) {
                    System.out.println("Noget gik galt: " + error.getMessage());
                }
            }
        });

        Thread messageFromServer = new Thread(() -> {
            while (true) {
                try {
                    String serverBroadcast = input.readLine();
                    if (serverBroadcast.equals("J_OK")) {
                        connected.set(true);
                    }
                    System.out.println(serverBroadcast);
                } catch (IOException error) {
                    System.out.println("Noget gik galt: " + error.getMessage());
                }
            }
        });

        //TODO: Implement this.
//        Thread sendHeartbeat = new Thread(() -> {
//            while (true) {
//                try {
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        messageToServer.start();
        messageFromServer.start();
    }

}
