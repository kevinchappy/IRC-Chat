package Client;

import Client.Gui.listGUI;
import Helpers.MessageBuilder;
import Helpers.MessageCodes;

import java.io.PrintWriter;
import java.util.List;

/**
 * Class for handling getting and displaying a list of all existing channels on the server.
 * Also allows for joining channels by selecting channels in the list.
 */
public class ChannelListController {
    private final listGUI gui = new listGUI();
    private final PrintWriter writer;

    /**
     * Instantiates a new Channel list.
     * Sets titles and names for GUI elements.
     * Sets button listeners.
     *
     * @param writer the writer to server socket's output stream
     */
    public ChannelListController(PrintWriter writer) {
        this.writer = writer;
        gui.setLeftButtonName("Join");
        gui.setRightButtonName("Refresh");
        gui.setTitle("All Channels");

        setButtonListeners();
    }

    /**
     * Shows GUI.
     */
    public void show() {
        gui.show();
    }

    /**
     * Disposes GUI.
     */
    public void dispose() {
        gui.dispose();
    }

    /**
     * Sets the list in the GUI with list of all channels.
     *
     * @param list the list of all channels
     */
    public void setList(List<String> list) {
        gui.setList(list);
    }

    /**
     * Sets listeners for the GUI's left and right buttons.
     * The left button listener sends a JOIN request with the selected channel.
     * The right button listener sends a CHANNELS command to retrieve a list of all channels from the server
     */
    private void setButtonListeners() {
        gui.setLeftButtonListener(e -> {
            String str = gui.getSelected();
            if (str != null) {
                String formattedMessage = MessageBuilder.build(MessageCodes.JOIN, new String[]{str});
                writer.println(formattedMessage);
            }
        });

        gui.setRightButtonListener(e -> {
            String formattedMessage = MessageBuilder.build(MessageCodes.CHANNELS);
            writer.println(formattedMessage);
        });
    }
}
