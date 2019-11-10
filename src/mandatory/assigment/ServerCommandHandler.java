package mandatory.assigment;

public class ServerCommandHandler {

    //TODO: Add enums for the error messages so it is possible to write J_ER.(ERROR)

    /**
     * This method checks various parameters of the JOIN command sent to the server and will return a variety of error depending on the input.
     * If all is acknowledged by the server it will return J_OK and the client will get an active connection.
     */
    public static String joinCheck(String input) {
        String[] commandSplit = input.split(" ", 2);
        String[] nameSplit = commandSplit[1].split(",", 0);
        String username = nameSplit[0];
        String[] infoSplit = nameSplit[1].split(":", 0);

        if(!commandSplit[0].equals("JOIN")) {
            return "J_ER - Not following protocol";
        }
        if (!infoSplit[0].matches("(.*)\\.(.*)\\.(.*)\\.(.*)") && !infoSplit[1].matches("^[0-9]*$")) {
            return "J_ER - Problem with IP or Port";
        }
        if (!username.matches("^[a-zA-Z0-9\\-_]*$")) {
            return "J_ER - Username contains illegal characters";
        }
        if (username.length() > 11) {
            return "J_ER - Username is to long";
        }
        if (Server.nameList.containsKey(username)) {
            return "J_ER - Username already exists / Error 401: Brugernavnet findes allerede i listen!";
        }
        return "J_OK";
    }

    /**
     * This method makes checks on the DATA command and will make sure the clients adhere to the rules regarding free-text.
     */
    public static String dataCommand(String input, String clientName) {
        String[] freeTextSplit = input.split(":", 0);
        String freeText = freeTextSplit[1].substring(1);

        if(!freeTextSplit[0].equals(clientName)) {
            return "J_ER - Client name does not match";
        }
        if (freeText.length() > 249) {
            return "J_ER - To many characters";
        }
        return freeText;
    }
}