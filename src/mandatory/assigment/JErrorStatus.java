package mandatory.assigment;

public enum JErrorStatus {
    NO_SUCH_COMMAND(100, "No such command"),
    IP_PORT_PROBLEM(101, "Problem with given IP or port"),
    ILLEGAL_CHARACTERS(102, "Username contains illegal characters"),
    USERNAME_TO_LONG(103, "Username to long"),
    USERNAME_ALREADY_EXISTS(104, "Username already exists"),
    CLIENT_NAME_MISMATCH(105, "Client name does not match"),
    TEXT_TO_LONG(106, "To many characters in written text"),

    OK(202, "OK");

    private final int code;
    private final String errorMsg;

    private JErrorStatus(int code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String toString() {
        if(this == JErrorStatus.OK) {
            return "J_OK";
        }
        return "J_ER " + this.code + ": " + this.errorMsg;
    }
}
