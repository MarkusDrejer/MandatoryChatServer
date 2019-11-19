package mandatory.assigment;


import java.io.PrintWriter;

public class Heartbeat {

    private int heartBeatInterval = 0;
    private String heartBeatResponse = "IMAV";
    private PrintWriter output = null;

    public boolean isReadyToSendHeartbeat() {
        if (heartBeatInterval == 2) {
            return true;
        }
        return false;
    }

    public String sendHeartbeatToServer() {
        return this.heartBeatResponse;
//        output.println("IMAV");
    }

    public void reset() {
        heartBeatInterval = 0;
    }

    public void increaseHeartbeatInterval() {
        heartBeatInterval++;
    }

}
