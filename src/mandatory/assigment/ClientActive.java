package mandatory.assigment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientActive {

    private BufferedReader keyboardInput = null;
    private BufferedReader input = null;
    private PrintWriter output = null;

    public ClientActive(String IP, int port) {
        try {
            Socket socket = new Socket(IP, port);
            keyboardInput = new BufferedReader(new InputStreamReader(System.in));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException error) {
            System.out.println("Noget gik galt: " + error.getMessage());
        }


        Thread messageToServer = new Thread(() -> {
            while (true) {
                try {
                    System.out.print(">> ");
                    output.println(keyboardInput.readLine());
                } catch (IOException error) {
                    System.out.println("Noget gik galt: " + error.getMessage());
                }
            }
        });
        messageToServer.start();

        Thread messageFromServer = new Thread(() -> {
            while (true) {
                try {
                    System.out.println(input.readLine());
                } catch (IOException error) {
                    System.out.println("Noget gik galt: " + error.getMessage());
                }
            }
        });
        messageFromServer.start();
    }



    public static void main(String[] args) {
        ClientActive client = new ClientActive("127.0.0.1", 1234);
    }
}