package Client;

import helper.MessageCodes;
import helper.MessageBuilder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.util.*;

public class ClientGUI {
    private String name;
    private final JFrame frame;
    private final PrintWriter writer;
    private JTextArea messageArea;
    private JPanel panel1;
    private JTextField messageTextField;
    private JButton sendButton;
    private JList<String> userList;
    private JList<ClientChannel> channelList;
    private JButton seeAllChannelsButton;
    private JButton seeAllUsersButton;
    private JScrollPane scrollPane;
    private final DefaultListModel<ClientChannel> channelListModel = new DefaultListModel<>();
    private ClientChannel selectedChannel;
    private final UserList allUserList;
    private final ChannelList allChannelList;


    public ClientGUI( UserList allUserList,ChannelList allChannelList,  PrintWriter writer) {

        this.writer = writer;

        this.allUserList = allUserList;
        this.allChannelList = allChannelList;

        setListeners();

        channelList.setModel(channelListModel);

        frame = new JFrame("IRC Client");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000, 800);
    }


    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }

    public void dispose() {
        frame.dispose();
    }

    public void printMessage(String trailing, String channelName, String userName, String timeAndDate) {
        ClientChannel ch = getChannelByName(channelName);
        if (ch != null) {
            String msg = timeAndDate + " : " + userName + " " + trailing + "\n";
            ch.addMessage(msg);
            if (selectedChannel != null && selectedChannel.compareName(channelName)) {
                messageArea.append(msg);
            }
        }
    }


    private void setListeners() {

        sendButton.addActionListener(e -> {
            setButtonListener();
        });

        seeAllChannelsButton.addActionListener(e ->{
            allChannelList.show();
        });

        seeAllUsersButton.addActionListener(e -> {
            allUserList.show();
        });

        messageTextField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonListener();
            }
        });

        userList.addListSelectionListener(e -> {

        });

        channelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ClientChannel ch = channelList.getSelectedValue();
                if (ch != null) {
                    userList.setModel(ch.getUsersListModel());
                    messageArea.setText("");
                    for (String message : ch.getMessages()) {
                        messageArea.append(message);
                    }
                }
                selectedChannel = ch;
            }
        });
    }

    private void setButtonListener() {
        if (!messageTextField.getText().isEmpty()) {
            String rawString = messageTextField.getText();
            messageTextField.setText("");

            if (rawString.charAt(0) == '/') {
                String[] arr = rawString.split(" ", 2);
                handleCommand(arr[0], arr[1]);
            } else {
                handleSendMessage(rawString);
            }
        }
    }

    public void addUserToChannel(String userName, String channelName) {
        for (int i = 0; i < channelListModel.size(); i++) {
            ClientChannel ch = channelListModel.get(i);
            if (ch.compareName(channelName)) {
                ch.addUser(userName);
                printMessage(" joined the channel", channelName, userName, "");
                return;
            }
        }
    }

    public void removeUserFromChannel(String userName, String channelName) {
        for (int i = 0; i < channelListModel.size(); i++) {
            ClientChannel ch = channelListModel.get(i);
            if (ch.compareName(channelName)) {
                ch.removeUser(userName);
                printMessage(" left the channel", channelName, userName, "");
                return;
            }
        }
    }

    public void removeUser(String userName) {
        for (int i = 0; i < channelListModel.size(); i++) {
            ClientChannel ch = channelListModel.get(i);
            if (ch.removeUser(userName)) {
                printMessage(" left the channel", ch.toString(), userName, "");
            }
        }
    }


    public void removeChannel(String s) {
        for (int i = 0; i < channelListModel.size(); i++) {
            ClientChannel ch = channelListModel.get(i);
            if (ch.compareName(s)) {
                channelListModel.remove(i);
                userList.setModel(new DefaultListModel<>());
                return;
            }
        }
    }

    public void addChannel(String name, List<String> users, boolean isPrivateChannel) {
        channelListModel.addElement(new ClientChannel(name, users, isPrivateChannel));
    }

    public void handleNameChange(String oldName, String newName) {

        for (int i = 0; i < channelListModel.size(); i++) {
            channelListModel.get(i).updateUser(oldName, newName);

        }
    }

    private void handleSendMessage(String msg) {
        if (selectedChannel != null) {
            String formattedMessage = MessageBuilder.build(MessageCodes.MESSAGE, new String[]{selectedChannel.toString()}, msg);
            writer.println(formattedMessage);
        }
    }

    private ClientChannel getChannelByName(String name) {
        for (int i = 0; i < channelListModel.size(); i++) {
            if (channelListModel.get(i).compareName(name)) {
                return channelListModel.get(i);
            }
        }
        return null;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void createChannelIfNotExists(String userName) {
        if (!channelExists(userName)) {
            addChannel(userName, Arrays.asList(userName, this.name), true);
        }
    }

    public boolean channelExists(String channelName) {
        for (int i = 0; i < channelListModel.size(); i++) {
            if (channelListModel.get(i).compareName(channelName)) {
                return true;
            }
        }
        return false;
    }
}
