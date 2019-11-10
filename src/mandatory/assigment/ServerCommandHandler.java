package mandatory.assigment;

public class ServerCommandHandler {

    /**
     * This method checks various parameters of the JOIN command sent to the server and will return a variety of error depending on the input.
     * If all is acknowledged by the server it will return J_OK and the client will get an active connection.
     */
    public static JErrorStatus joinCheck(String input) {
        String[] commandSplit = input.split(" ", 2);
        String[] nameSplit = commandSplit[1].split(",", 0);
        String username = nameSplit[0];
        String[] infoSplit = nameSplit[1].split(":", 0);

        if(!commandSplit[0].equals("JOIN")) {
            return JErrorStatus.NO_SUCH_COMMAND;
        }
        if (!infoSplit[0].matches("(.*)\\.(.*)\\.(.*)\\.(.*)") && !infoSplit[1].matches("^[0-9]*$")) {
            return JErrorStatus.IP_PORT_PROBLEM;
        }
        if (!username.matches("^[a-zA-Z0-9\\-_]*$")) {
            return JErrorStatus.ILLEGAL_CHARACTERS;
        }
        if (username.length() > 11) {
            return JErrorStatus.USERNAME_TO_LONG;
        }
        if (Server.nameList.containsKey(username)) {
            return JErrorStatus.USERNAME_ALREADY_EXISTS;
        }
        return JErrorStatus.OK;
    }

    /**
     * This method makes checks on the DATA command and will make sure the clients adhere to the rules regarding free-text.
     */
    public static JErrorStatus dataCommand(String input, String clientName) {
        String[] freeTextSplit = input.split(":", 0);
        String freeText = freeTextSplit[1].substring(1);

        if(!freeTextSplit[0].equals(clientName)) {
            return JErrorStatus.CLIENT_NAME_MISMATCH;
        }
        if (freeText.length() > 249) {
            return JErrorStatus.TEXT_TO_LONG;
        }
        return JErrorStatus.OK;
    }
}