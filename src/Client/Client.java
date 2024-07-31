package Client;

/**
 * Main class.
 */
public class Client {
    public static final String DEFAULT_PORT = "6667";

    public static void main(String[] args) {
        JoinGUIController joinGUI = new JoinGUIController();
        joinGUI.show();
    }
}
