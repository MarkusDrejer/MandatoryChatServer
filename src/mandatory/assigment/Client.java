package mandatory.assigment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    enum Status {
        STARTUP,
        CONNECTED,
        DISCONNECTED
    }

    private String IP;
    private int port;
    private String username;
    private int heartBeatInterval = 0;
    private Status clientStatus;

    private BufferedReader keyboardInput = null;
    private BufferedReader input = null;
    private PrintWriter output = null;

    public Client(String IP, int port) {
        this.IP = IP;
        this.port = port;
        clientStatus = Status.STARTUP;
        try {
            Socket socket = new Socket(IP, port);
            keyboardInput = new BufferedReader(new InputStreamReader(System.in));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException error) {
            System.out.println("Noget gik galt: " + error.getMessage());
        }

        /**
         * A thread running a heartbeat which is sent to the server every minute to prevent a time-out if the user is inactive,
         * it will only send if the user has not sent anything him/her-self in the last 60 seconds.
         */
        Thread heartbeat = new Thread(() -> {
            while (clientStatus != Status.DISCONNECTED) {
                try {
                    if (heartBeatInterval == 60) {
                        output.println("IMAV");
                        heartBeatInterval = 0;
                    } else {
                        heartBeatInterval++;
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         * Thread to send messages to the server, will run until the user types "QUIT" and makes sure the user inputs,
         * are wrapped the correct way so the server understands it depending on whether the client is connected or not
         */
        Thread messageToServer = new Thread(() -> {
            System.out.println("Please input a username");
            try {
                String userInput = keyboardInput.readLine();
                while (!userInput.equals("QUIT") && clientStatus != Status.DISCONNECTED) {
                    System.out.print(">> ");
                    output.println(wrapper(userInput));
                    heartBeatInterval = 0;
                    userInput = keyboardInput.readLine();
                }
                output.println(userInput);
            } catch (IOException error) {
                System.out.println("Noget gik galt: " + error.getMessage());
            }
        });
        messageToServer.start();

        /**
         * Thread to receive messages from the server, will run until the server sends a disconnect message to the client,
         * will also set connected to true after the username is accepted by the server after which it will just listen.
         */
        Thread messageFromServer = new Thread(() -> {
            try {
                String serverBroadcast = input.readLine();
                while (clientStatus != Status.DISCONNECTED) {
                    if (serverBroadcast.equals(JErrorStatus.OK.toString()) && clientStatus == Status.STARTUP) {
                        clientStatus = Status.CONNECTED;
                        heartbeat.start();
                    }
                    if (serverBroadcast.equals(JErrorStatus.TIMEOUT.toString()) || serverBroadcast.equals(JErrorStatus.DISCONNECTED.toString())) {
                        clientStatus = Status.DISCONNECTED;
                        System.out.println(serverBroadcast);
                        break;
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

    /**
     * Method to wrap any user input into a understandable command by the server, this means that this client type does not,
     * need to worry about the actual server commands, rather just types and the rest is taken care of.
     */
    private String wrapper(String input) {
        String wrappedText = "";
        switch (clientStatus) {
            case STARTUP:
                username = input;
                wrappedText = "JOIN " + input + ", " + IP + ":" + port;
                break;
            case CONNECTED:
                wrappedText = "DATA " + username + ": " + input;
                break;
        }
        return wrappedText;
    }

    /**
     * Runs the client
     */
    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 1234);
    }
}