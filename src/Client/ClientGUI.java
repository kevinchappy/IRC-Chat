package Client;

import helper.MessageCodes;
import helper.MessageBuilder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientGUI {
    private final JFrame frame;
    private PrintWriter writer;
    private JTextArea messageArea;
    private JPanel panel1;
    private JTextField messageTextField;
    private JButton sendButton;
    private JList<String> userList;
    private JList<ClientChannel> channelList;
    private JButton addChannelButton;
    private final DefaultListModel<ClientChannel> channelListModel = new DefaultListModel<>();
    private ClientChannel selectedChannel;
    private final HashSet<String> allUsers = new HashSet<>();


    public ClientGUI(Socket socket) {

        try {
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),
                    StandardCharsets.ISO_8859_1), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            String msg = timeAndDate + " : " + userName + trailing + "\n";
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
                allUsers.add(userName);
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

    public void removeUser(String userName){
        for (int i = 0; i < channelListModel.size(); i++){
            ClientChannel ch = channelListModel.get(i);
            if (ch.removeUser(userName)){
                printMessage(" left the channel", ch.toString(), userName, "");
            }
        }
        allUsers.remove(userName);
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

    public void addChannel(String name, List<String> users) {
        ArrayList<String> userList = new ArrayList<>();
        for (String userName : users) {
            allUsers.add(userName);
            userList.add(userName);
        }
        channelListModel.addElement(new ClientChannel(name, userList));
    }

    public void handleNameChange(String oldName, String newName) {
        if (allUsers.contains(oldName)) {
            allUsers.remove(oldName);
            allUsers.add(newName);
            for (int i = 0; i < channelListModel.size(); i++) {
                channelListModel.get(i).updateUser(oldName, newName);
            }
        }
    }



    private void handleSendMessage(String msg) {
        if (selectedChannel != null) {
            String formattedMessage = MessageBuilder.build(MessageCodes.MESSAGE,
                    new String[]{selectedChannel.toString()}, msg);
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

            default:
                break;
        }
        if (formattedMessage != null) {
            writer.println(formattedMessage);
        }
    }
}
