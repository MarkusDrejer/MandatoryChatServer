package mandatory.assigment;

public class ServerCommandHandler {

    /**
     * This method checks various parameters of the JOIN command sent to the server and will return a variety of error depending on the input.
     * If all is acknowledged by the server it will return J_OK and the client will get an active connection.
     */
    public static JErrorStatus joinCheck(String input) {
        try {
            String[] fullSplit = splitString(input, 4);
            String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

            if (!fullSplit[0].equals("JOIN")) {
                return JErrorStatus.NO_SUCH_COMMAND;
            }
            String username = fullSplit[1];
            if (!fullSplit[2].matches((zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255))) {
                return JErrorStatus.NOT_VALID_IP;
            }
            if(!fullSplit[3].matches("^[0-9]*$")) {
                return JErrorStatus.NOT_VALID_PORT;
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
        } catch (IndexOutOfBoundsException e) {
            return JErrorStatus.MISSING_MESSAGE;
        }
        return JErrorStatus.OK;
    }

    /**
     * This method makes checks on the DATA command and will make sure the clients adhere to the rules regarding free-text.
     */
    public static JErrorStatus dataCommand(String input, String clientName) {
        try {
            String[] fullSplit = splitString(input, 3);

            if (!fullSplit[1].equals(clientName)) {
                return JErrorStatus.CLIENT_NAME_MISMATCH;
            }
            if (fullSplit[2].length() > 249) {
                return JErrorStatus.TEXT_TO_LONG;
            }
        } catch (IndexOutOfBoundsException e) {
            return JErrorStatus.MISSING_MESSAGE;
        }
        return JErrorStatus.OK;
    }

    /**
     * Small method for ease of splitting user input strings received by the server to interpret the individual parts.
     */
    private static String[] splitString(String input,  int limit) {
        input = input.replaceFirst("[,:]", "");
        input = input.replaceAll("\\s+", " ");
        return input.split("[\\s,:]", limit);
    }
}