package Client;

import helper.MessageBuilder;
import helper.MessageCodes;

import java.io.PrintWriter;
import java.util.List;

public class UserList {
    private final listGUI gui = new listGUI();
    private final PrintWriter writer;

    public UserList(PrintWriter writer) {
        this.writer = writer;
        gui.setLeftButtonName("Private Message");
        gui.setRightButtonName("Refresh");
        gui.setTitle("All Users");

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
                String formattedMessage = MessageBuilder.build(MessageCodes.ESTABLISH_PRIVATE_CHAT, new String[]{str});
                writer.println(formattedMessage);
            }
        });

        gui.setRightButtonListener(e -> {
            String formattedMessage = MessageBuilder.build(MessageCodes.NAMES);
            System.out.println("sending: " + formattedMessage);
            writer.println(formattedMessage);
        });
    }
}

