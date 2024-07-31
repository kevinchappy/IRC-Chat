package Client;

import helper.MessageBuilder;
import helper.MessageCodes;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class ClientGUIController {
    private final ClientGUI gui;
    private final PrintWriter writer;
    private final UserList allUserList;
    private final ChannelList allChannelList;
    private String name;

    public ClientGUIController(UserList allUserList, ChannelList allChannelList, PrintWriter writer) {
        this.writer = writer;
        this.gui = new ClientGUI();
        this.allUserList = allUserList;
        this.allChannelList = allChannelList;

        setListeners();
        gui.show();
    }

    private void handleCommand(String command, String msg) {
        String formattedMessage = null;
        switch (command) {
            case "/join":
                formattedMessage = MessageBuilder.build(MessageCodes.JOIN, new String[]{msg});
                break;

            case "/leave":
                formattedMessage = MessageBuilder.build(MessageCodes.PART, new String[]{msg});
                break;

            case "/nick":
                formattedMessage = MessageBuilder.build(MessageCodes.NICKNAME, new String[]{msg});
                break;

            case "/exit":
                formattedMessage = MessageBuilder.build(MessageCodes.EXIT);
                break;

            case "/msg":
                formattedMessage = MessageBuilder.build(MessageCodes.ESTABLISH_PRIVATE_CHAT, new String[]{msg});
                break;
            default:

        }
        if (formattedMessage != null) {
            writer.println(formattedMessage);
        }
    }

    public void show(){
        gui.show();
    }

    public void printMessage(String trailing, String channelName, String userName, String timeAndDate) {
        ClientChannel ch = getChannelByName(channelName);
        if (ch != null) {
            String msg;
            if (ch.hasNoMessages()) {
                msg = timeAndDate + " : " + userName + " " + trailing;
            } else {
                msg = "\n" + timeAndDate + " : " + userName + " " + trailing;
            }
            ch.addMessage(msg);
            gui.printToTextArea(msg, ch);
        }
    }

    public void addUserToChannel(String userName, String channelName) {
        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            ClientChannel ch = gui.getChannelListModel().get(i);
            if (ch.compareName(channelName)) {
                ch.addUser(userName);
                printMessage(" joined the channel", channelName, userName, "");
                return;
            }
        }
    }

    public void removeUserFromChannel(String userName, String channelName) {
        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            ClientChannel ch = gui.getChannelListModel().get(i);
            if (ch.compareName(channelName)) {
                ch.removeUser(userName);
                printMessage(" left the channel", channelName, userName, "");
                return;
            }
        }
    }

    public void removeUser(String userName) {
        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            ClientChannel ch = gui.getChannelListModel().get(i);
            if (ch.removeUser(userName)) {
                printMessage(" left the channel", ch.toString(), userName, "");
                if (ch.getName().equals(userName)){
                    ch.setName("##dead## " + ch.getName());
                    gui.getChannelListModel().setElementAt(ch, i);
                }
            }
        }
    }


    public void removeChannel(String s) {
        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            ClientChannel ch = gui.getChannelListModel().get(i);
            if (ch.compareName(s)) {
                gui.getChannelListModel().remove(i);
                gui.resetUserList();
                return;
            }
        }
    }

    public void addChannel(String name, List<String> users, boolean isPrivateChannel) {
        gui.getChannelListModel().addElement(new ClientChannel(name, users, isPrivateChannel));
    }

    public void handleNameChange(String oldName, String newName) {

        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            gui.getChannelListModel().get(i).updateUser(oldName, newName);

        }
    }

    private void handleSendMessage(String msg) {
        if (gui.getActiveChannel() != null) {
            String formattedMessage = MessageBuilder.build(MessageCodes.MESSAGE, new String[]{gui.getActiveChannel().toString()}, msg);
            writer.println(formattedMessage);
        }
    }

    private ClientChannel getChannelByName(String name) {
        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            if (gui.getChannelListModel().get(i).compareName(name)) {
                return gui.getChannelListModel().get(i);
            }
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void createPrivateChannelIfNotExists(String userName) {
        if (!channelExists(userName)) {
            addChannel(userName, Arrays.asList(userName, this.name), true);
        }
    }

    public boolean channelExists(String channelName) {
        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            if (gui.getChannelListModel().get(i).compareName(channelName)) {
                return true;
            }
        }
        return false;
    }

    public void dispose() {
        gui.dispose();
    }

    private void setListeners() {

        gui.sendButtonActionListener(e -> setButtonListener());

        gui.messageTextFieldActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonListener();
            }
        });

        gui.seeAllChannelsActionListener(e -> allChannelList.show());

        gui.seeAllUsersActionListener(e -> allUserList.show());

        gui.addChannelListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ClientChannel ch = gui.getSelectedChannel();
                if (ch != null) {
                    gui.setUserListModel(ch.getUsersListModel());
                    gui.clearMessageArea();
                    gui.populateMessageArea(ch.getMessages());
                }
                gui.setActiveChannel(ch);
            }
        });
    }

    private void setButtonListener() {
        String rawString = gui.getText();
        if (!rawString.isEmpty()) {
            gui.clearTextField();
            if (rawString.charAt(0) == '/') {
                String[] arr = rawString.split(" ", 2);
                handleCommand(arr[0], arr[1]);
            } else {
                handleSendMessage(rawString);
            }
        }
    }
}
