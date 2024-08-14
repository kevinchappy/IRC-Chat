package Client;

import Client.Gui.ClientGUI;
import Helpers.MessageBuilder;
import Helpers.MessageCodes;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * The type Client gui controller.
 */
public class ClientGUIController {
    private final Socket socket;
    private final ClientGUI gui;
    private final PrintWriter writer;
    private final UserListController allUserListController;
    private final ChannelListController allChannelListController;
    private String name;

    /**
     * Instantiates a new Client gui controller.
     *
     * @param socket         Server socket
     * @param allUserListController    The user list GUI component
     * @param allChannelListController The channel list GUI component
     * @param writer         Writer to server
     */
    public ClientGUIController(Socket socket, UserListController allUserListController, ChannelListController allChannelListController, PrintWriter writer) {
        this.socket = socket;
        this.writer = writer;
        this.gui = new ClientGUI();
        this.allUserListController = allUserListController;
        this.allChannelListController = allChannelListController;

        setListeners();
        gui.show();
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
     * Sets listeners for GUI components.
     */
    private void setListeners() {

        gui.sendButtonActionListener(e -> setButtonListener());

        gui.messageTextFieldActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonListener();
            }
        });

        gui.seeAllChannelsActionListener(e -> allChannelListController.show());

        gui.seeAllUsersActionListener(e -> allUserListController.show());

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

    /**
     * Button listener logic.
     * If input starts with '/' it is handles as a command.
     * Otherwise, it is broadcast in current channel as a message.
     */
    private void setButtonListener() {
        String rawString = gui.getText();
        if (!rawString.isEmpty()) {
            gui.clearTextField();
            if (rawString.charAt(0) == '/') {
                String[] arr = rawString.split(" ", 2);
                handleCommand(arr);
            } else {
                handleSendMessage(rawString);
            }
        }
    }


    /**
     * Handle commands and formats them according to their type.
     * Valid commands are: "/join", "/leave", "/nick", "/exit".
     * Special check is done for "/leave" to see if it is a private message channel or not. Private channel is only left locally.
     *
     * @param args command arguments
     */
    private void handleCommand(String[] args) {
        String command = args[0];
        String msg = "";
        if (args.length == 2) {
            msg = args[1];
        }
        String formattedMessage = null;
        switch (command) {
            case "/join":
                formattedMessage = MessageBuilder.build(MessageCodes.JOIN, new String[]{msg});
                break;

            case "/leave":
                if (msg.startsWith("##dead##") || !msg.startsWith("#")) {
                    removeChannel(msg);
                } else {
                    formattedMessage = MessageBuilder.build(MessageCodes.PART, new String[]{msg});
                }
                break;

            case "/nick":
                formattedMessage = MessageBuilder.build(MessageCodes.NICKNAME, new String[]{msg});
                break;

            case "/exit":
                formattedMessage = MessageBuilder.build(MessageCodes.EXIT);
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
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


    /**
     * Formats message and prints to specified channel.
     * Adds message to channel's message list and prints to GUI.
     *
     * @param trailing    the trailing message
     * @param channelName the channel name
     * @param userName    the username
     * @param timeAndDate the time and date
     */
    public void printMessage(String trailing, String channelName, String userName, String timeAndDate) {
        ClientChannel ch = getChannelByName(channelName);
        if (ch != null) {
            String msg;
            if (ch.hasNoMessages()) {
                msg = timeAndDate + " : " + userName + " " + trailing;
                ch.setFirstMessage();
            } else {
                msg = "\n" + timeAndDate + " : " + userName + " " + trailing;
            }
            ch.addMessage(msg);
            gui.printToTextArea(msg, ch);
        }
    }

    /**
     * Adds user to specified channel by name.
     *
     * @param userName    the username
     * @param channelName the channel name
     */
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

    /**
     * Removes user from specified channel by name.
     *
     * @param userName    the username
     * @param channelName the channel name
     */
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

    /**
     * Removes specified user from all channels.
     * Changes name of user's private message channel if any. This is so that there are no duplicate private message channels
     *
     * @param userName the username
     */
    public void removeUser(String userName) {
        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            ClientChannel ch = gui.getChannelListModel().get(i);
            if (ch.removeUser(userName)) {
                printMessage(" left the channel", ch.toString(), userName, "");
                if (ch.getName().equals(userName)) {
                    ch.setName("##dead## " + ch.getName());
                    gui.getChannelListModel().setElementAt(ch, i);
                }
            }
        }
    }

    /**
     * Removes specified channel from the GUI's channel list model.
     *
     * @param channelName the channel's name
     */
    public void removeChannel(String channelName) {
        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            ClientChannel ch = gui.getChannelListModel().get(i);
            if (ch.compareName(channelName)) {
                gui.getChannelListModel().remove(i);
                gui.resetUserList();
                return;
            }
        }
    }

    /**
     * Adds channel to the GUI's channel list model.
     *
     * @param name  the name
     * @param users the users
     */
    public void addChannel(String name, List<String> users) {
        gui.getChannelListModel().addElement(new ClientChannel(name, users));
    }

    /**
     * Changes specified channel's name to new name.
     *
     * @param oldName the old channel name
     * @param newName the new channel name
     */
    public void handleNameChange(String oldName, String newName) {
        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            gui.getChannelListModel().get(i).updateUser(oldName, newName);
        }
    }

    /**
     * Handles sending message to active channel.
     * Formats message with MessageBuilder.build() and sends formatted message to server.
     *
     * @param msg the message to be sent
     */
    private void handleSendMessage(String msg) {
        if (gui.getActiveChannel() != null) {
            String formattedMessage = MessageBuilder.build(MessageCodes.MESSAGE, new String[]{gui.getActiveChannel().toString()}, msg);
            writer.println(formattedMessage);
        }
    }

    /**
     * Gets channel by name.
     *
     * @param name the name of the channel
     * @return the channel if exists or null
     */
    private ClientChannel getChannelByName(String name) {
        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            if (gui.getChannelListModel().get(i).compareName(name)) {
                return gui.getChannelListModel().get(i);
            }
        }
        return null;
    }

    /**
     * Sets current username.
     *
     * @param name the new username
     */
    public void setCurrentUserName(String name) {
        this.name = name;
    }

    /**
     * Gets current username.
     *
     * @return the current username
     */
    public String getCurrentUserName() {
        return name;
    }

    /**
     * Checks if private server exists and creates one if not.
     *
     * @param userName the username of other client
     */
    public void createPrivateChannelIfNotExists(String userName) {
        if (!channelExists(userName)) {
            addChannel(userName, Arrays.asList(userName, this.name));
        }
    }

    /**
     * Checks if channel exists by name.
     *
     * @param channelName the channel name
     * @return 'true' if channel exists, 'false' otherwise
     */
    public boolean channelExists(String channelName) {
        for (int i = 0; i < gui.getChannelListModel().size(); i++) {
            if (gui.getChannelListModel().get(i).compareName(channelName)) {
                return true;
            }
        }
        return false;
    }
}
