package mandatory.assigment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

    //TODO: Make heartbeat work on server side, add more comments

public class ChatHandler implements Runnable {

    private Socket client;
    private BufferedReader input;
    private PrintWriter output;
    private boolean isLoggedIn;
    private boolean isRunning = true;
    //private int heartBeatInterval = 0;
    private String clientName;

    ChatHandler(Socket clientSocket) throws IOException {
        this.client = clientSocket;
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        output = new PrintWriter(client.getOutputStream(), true);
    }

    /*private Thread heartbeat = new Thread(() -> {
        while (isRunning) {
            try {
                if (heartBeatInterval == 60) {
                    disconnectClient(JErrorStatus.TIMEOUT);
                } else {
                    heartBeatInterval++;
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });*/

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
                            //heartBeatInterval = 0;
                            break;
                        case "IMAV":
                            //heartBeatInterval = 0;
                            //TODO: implement server being able to understand heartbeat sent by clients and extend their timeouts and timeout any client who hasn't sent anything in a minute
                            break;
                        case "QUIT":
                            notifyClients(this.clientName + " har forladt chatten!");
                            disconnectClient(JErrorStatus.DISCONNECTED);
                            break;
                        default:
                            output.println(JErrorStatus.NO_SUCH_COMMAND);
                            //heartBeatInterval = 0;
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
                        //heartbeat.start();
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
     * Method for disconnecting the client and send a given JErrorStatus to the client to clarify why the client got disconnected
     */
    private void disconnectClient(JErrorStatus status) {
        output.println(status);
        Server.nameList.remove(this.clientName);
        Server.clientList.remove(this);
        broadcastNewlyUpdatedList();
        isRunning = false;
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
                client.output.println("\n ---USERLIST---");
                for (String name : Server.nameList.keySet()) {
                    client.output.println(name);
                }
            }
        }
    }
}