package mandatory.assigment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    private String IP;
    private int port;
    private String username;

    private BufferedReader keyboardInput = null;
    private BufferedReader input = null;
    private PrintWriter output = null;

    private AtomicBoolean connected = new AtomicBoolean(false);

    public Client(String IP, int port) {
        this.IP = IP;
        this.port = port;
        try {
            Socket socket = new Socket(IP, port);
            keyboardInput = new BufferedReader(new InputStreamReader(System.in));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException error) {
            System.out.println("Noget gik galt: " + error.getMessage());
        }


        Thread messageToServer = new Thread(() -> {
            System.out.println("Please input a username");
            try {
                String userInput = keyboardInput.readLine();
                while (!userInput.equals("QUIT")) {
                        System.out.print(">> ");
                        if (!connected.get()) {
                            username = userInput;
                            userInput = wrapper("JOIN", userInput);
                        } else {
                            userInput = wrapper("DATA", userInput);
                        }
                        output.println(userInput);
                        userInput = keyboardInput.readLine();
                    }
                output.println(userInput);
                } catch (IOException error) {
                System.out.println("Noget gik galt: " + error.getMessage());
                }
        });
        messageToServer.start();

        Thread messageFromServer = new Thread(() -> {
            try {
                String serverBroadcast = input.readLine();
                while (!serverBroadcast.equals(JErrorStatus.DISCONNECTED.toString())) {
                        if (serverBroadcast.equals("J_OK") && !connected.get()) {
                            connected.set(true);
                        }
                        System.out.println(serverBroadcast);
                        serverBroadcast = input.readLine();
                    }
                } catch (IOException error) {
                System.out.println("Noget gik galt: " + error.getMessage());
            }
        });
        messageFromServer.start();
    }

    private String wrapper(String type, String input) {
        String wrappedText = "";
        switch (type) {
            case "JOIN":
                wrappedText = "JOIN " + input + ", " + IP + ":" + port;
                break;
            case "DATA":
                wrappedText = "DATA " + username + ": " + input;
                break;
        }
        return wrappedText;
    }

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



    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 1234);
    }
}