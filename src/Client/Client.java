package Client;


import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Main class.
 * Handles initiating connection to server and spawning server reading thread and all GUI components.
 */
public class Client {
    public static final String DEFAULT_PORT = "6667";

    public static void main(String[] args) {
        JoinGUI joinGUI = new JoinGUI();
        joinGUI.show();
    }
}
