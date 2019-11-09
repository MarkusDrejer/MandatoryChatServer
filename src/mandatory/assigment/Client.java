package mandatory.assigment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("127.0.0.1", 1234);

        BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

        Thread messageToServer = new Thread(() -> {
            while (true) {
                try {
                    System.out.println(">> ");
                    String msg = keyboardInput.readLine();
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
