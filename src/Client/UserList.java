package Client;

import Client.Gui.listGUI;
import Helpers.MessageBuilder;
import Helpers.MessageCodes;

import java.io.PrintWriter;
import java.util.List;

/**
 * Class for handling getting and displaying a list of all existing users on the server.
 * Also allows for starting private message channels with users in list.
 */
public class UserList {
    private final listGUI gui = new listGUI();
    private final PrintWriter writer;

    /**
     * Instantiates a new User list.
     * Sets titles and names for GUI elements.
     * Sets button listeners.
     *
     * @param writer the writer to server socket's output stream
     */
    public UserList(PrintWriter writer) {
        this.writer = writer;
        gui.setLeftButtonName("Private Message");
        gui.setRightButtonName("Refresh");
        gui.setTitle("All Users");

        setButtonListeners();
    }

    /**
     * Shows GUI component.
     */
    public void show() {
        gui.show();
    }

    /**
     * Disposes GUI component.
     */
    public void dispose() {
        gui.dispose();
    }

    /**
     * Sets the list in the GUI with list of all users.
     *
     * @param list the list of all users
     */
    public void setList(List<String> list) {
        gui.setList(list);
    }

    /**
     * Sets listeners for the GUI's left and right buttons.
     * The left button listener starts a private chat with the selected user.
     * The right button listener sends a USERS command to retrieve a list of all channels from the server
     */
    private void setButtonListeners() {
        gui.setLeftButtonListener(e -> {
            String str = gui.getSelected();
            if (str != null) {
                String formattedMessage = MessageBuilder.build(MessageCodes.ESTABLISH_PRIVATE_CHAT, new String[]{str});
                writer.println(formattedMessage);
            }
        });

        gui.setRightButtonListener(e -> {
            String formattedMessage = MessageBuilder.build(MessageCodes.NAMES);
            writer.println(formattedMessage);
        });
    }
}

