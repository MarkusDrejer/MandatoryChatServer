package mandatory.assigment;

public enum JErrorStatus {
    NO_SUCH_COMMAND(100, "No such command"),
    NOT_VALID_IP(101, "Not a valid IP"),
    NOT_VALID_PORT(102, "Not a valid port"),
    ILLEGAL_CHARACTERS(103, "Username contains illegal characters"),
    USERNAME_TO_LONG(104, "Username to long"),
    USERNAME_ALREADY_EXISTS(105, "Username already exists"),
    CLIENT_NAME_MISMATCH(106, "Client name does not match"),
    TEXT_TO_LONG(107, "Too many characters in written text"),
    MISSING_MESSAGE(108, "Missing parts of message after command"),
    DISCONNECTED(300, "You have been disconnected"),
    TIMEOUT(410, "You have been timed-out"),
    OK(202, "OK");

    private final int code;
    private final String errorMsg;

    private JErrorStatus(int code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public String toString() {
        if(this == JErrorStatus.OK) {
            return "J_OK";
        }
        return "J_ER " + this.code + ": " + this.errorMsg;
    }
}