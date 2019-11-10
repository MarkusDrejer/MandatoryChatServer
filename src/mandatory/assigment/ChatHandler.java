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
                    String[] commandSplit = userInput.split(" ", 2);
                    String command = commandSplit[0];

                    /**
                     * The switch represents the command-set the server understands and can act upon.
                     * The JOIN command is special and should only be able to happen once,
                     * therefore it has a special else case to make sure that it will never happen more than once.
                     */
                    switch (command) {
                        case "DATA":
                            JErrorStatus status = ServerCommandHandler.dataCommand(commandSplit[1], clientName);
                            if(status == JErrorStatus.OK) {
                                notifyClients(clientName + ":" + commandSplit[1].split(":", 0)[1]);
                            } else {
                                output.println(status);
                            }
                            break;
                        case "IMAV":
                            break;
                        case "QUIT":
                            notifyClients(this.clientName + " har forladt chatten!");
                            Server.nameList.remove(this.clientName);
                            Server.clientList.remove(this);
                            broadcastNewlyUpdatedList();
                            isRunning = false;
                            break;
                        default:
                            output.println(JErrorStatus.NO_SUCH_COMMAND);
                    }
                    /**
                     * The else underneath goes through the JOIN process of the connection, by checking if the user is adhering to the protocol
                     * and makes various checks on the username chosen in the ServerCommandHandler Class.
                     */
                } else {
                    output.println("Please input a username");
                    String userInput = input.readLine();
                    JErrorStatus status = ServerCommandHandler.joinCheck(userInput);
                    output.println(status);
                    if(status == JErrorStatus.OK) {
                        String usernameSplit = userInput.split(",", 0)[0];
                        String username = usernameSplit.split(" ", 0)[1];
                        Server.nameList.put(username, client);
                        broadcastNewlyUpdatedList();
                        this.clientName = username;
                        isLoggedIn = true;
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

    private void notifyClients(String message) {
        for (ChatHandler client : Server.clientList) {
            if (client.isLoggedIn) client.output.println(message);
        }
    }

    private void broadcastNewlyUpdatedList() {
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
