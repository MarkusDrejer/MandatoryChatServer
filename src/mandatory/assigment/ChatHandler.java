package mandatory.assigment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

    //TODO: Make heartbeat work on both client and server side, add more comments

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
                /**
                 * Takes user inputs and splits it into smaller strings so just the command can be extracted,
                 * interpreted and acted upon.
                 */
                String userInput = input.readLine();
                String[] commandSplit = userInput.split("[\\s,:]", 3);
                String command = commandSplit[0];
                if (isLoggedIn) {
                    /**
                     * The switch represents the command-set the server understands and can act upon.
                     * The JOIN command is special and should only be able to happen once,
                     * therefore it has a special else case to make sure that it will never happen more than once.
                     */
                    switch (command) {
                        case "DATA":
                            JErrorStatus status = ServerCommandHandler.dataCommand(userInput, clientName);
                            if(status == JErrorStatus.OK) {
                                notifyClients(clientName + ":" + commandSplit[2]);
                            } else {
                                output.println(status);
                            }
                            break;
                        case "IMAV":
                            //TODO: implement server being able to understand heartbeat sent by clients and extend their timeouts
                            break;
                        case "QUIT":
                            notifyClients(this.clientName + " har forladt chatten!");
                            output.println(JErrorStatus.DISCONNECTED);
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
                    JErrorStatus status = ServerCommandHandler.joinCheck(userInput);
                    output.println(status);
                    if(status == JErrorStatus.OK) {
                        Server.nameList.put(commandSplit[1], client);
                        this.clientName = commandSplit[1];
                        isLoggedIn = true;
                        broadcastNewlyUpdatedList();
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

    /**
     * Runs through the list of all clients and broadcasts whichever message a user has sent to the server.
     */
    private void notifyClients(String message) {
        for (ChatHandler client : Server.clientList) {
            if (client.isLoggedIn) client.output.println(message);
        }
    }

    /**
     * Whenever a new client connects to the server this method will broadcast to all connected clients, which clients
     * are connected at this moment in a user list by running through the map of all user names active on the server.
     */
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