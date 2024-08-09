package Client;

/**
 * Main class. Entry point for Client program.
 */
public class Client {
    public static final String DEFAULT_PORT = "6667";

    /**
     * Initializes and shows GUI for joining the server.
     * @param args ignored.
     */
    public static void main(String[] args) {
        JoinGUIController joinGUI = new JoinGUIController();
        joinGUI.show();
    }
}
