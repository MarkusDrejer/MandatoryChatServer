package mandatory.assigment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatHandler implements Runnable {

    private Socket client;
    private BufferedReader input;
    private PrintWriter output;
    private boolean isLoggedIn;
    private boolean isRunning = true;
    private String clientName;

    ChatHandler(Socket clientSocket) throws IOException {
        this.client = clientSocket;
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        output = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                if (isLoggedIn) {
                    String userInput = input.readLine();
                    if (userInput.equals("QUIT")) {
                        notifyClients(this.clientName + " har forladt chatten!");
                        Server.nameList.remove(this.clientName);
                        Server.clientList.remove(this);
                        broadcastNewlyUpdatedList();
                        isRunning = false;
                        break;
                    } else {
                        notifyClients(this.clientName + ": " + userInput);
                    }
                } else {
                    String name = input.readLine();
                    if (name.startsWith("JOIN")) {
                        name = name.substring(5);
                        String[] nameSplit = name.split(",", 2);
                        name = nameSplit[0];
                        if (!Server.nameList.containsKey(name)) {
                            Server.nameList.put(name, client);
                            output.println("J_OK");
                            broadcastNewlyUpdatedList();
                            this.clientName = name;
                            isLoggedIn = true;
                        } else {
                            output.println("Error 401: Brugernavnet findes allerede i listen!");
                        }
                    } else {
                        output.println("Sent String does not match protocol");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            output.close();
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void notifyClients(String message) {
        for (ChatHandler client : Server.clientList) {
            if (client.isLoggedIn) client.output.println(message);
        }
    }

    void broadcastNewlyUpdatedList() {
        for (ChatHandler client : Server.clientList) {
            if (client.isLoggedIn) {
                client.output.println("---USERLIST---");
                for (String name : Server.nameList.keySet()) {
                    client.output.println(name);
                }
            }
        }
    }
}
