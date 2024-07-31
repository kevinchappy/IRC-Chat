package Client;

import helper.MessageBuilder;
import helper.MessageCodes;

import java.io.PrintWriter;
import java.util.List;

/**
 * Class for displaying
 */
public class ChannelList {
    private final listGUI gui = new listGUI();
    private final PrintWriter writer;

    public ChannelList(PrintWriter writer) {
        this.writer = writer;
        gui.setLeftButtonName("Join");
        gui.setRightButtonName("Refresh");
        gui.setTitle("All Channels");

        setButtonListeners();
    }

    public void show() {
        gui.show();
    }

    public void dispose(){
        gui.dispose();
    }

    public void setList(List<String> list ){
        gui.setList(list);
    }

    private void setButtonListeners() {
        System.out.println("set button listeners");
        gui.setLeftButtonListener(e -> {
            String str = gui.getSelected();
            if (str != null) {
                String formattedMessage = MessageBuilder.build(MessageCodes.JOIN, new String[]{str});
                writer.println(formattedMessage);
            }
        });

        gui.setRightButtonListener(e -> {
            String formattedMessage = MessageBuilder.build(MessageCodes.CHANNELS);
            System.out.println("sending: " + formattedMessage);
            writer.println(formattedMessage);
        });
    }
}
