package Client;

import Server.Channel;
import helper.MessageCodes;
import helper.ResponseBuilder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientGUI {
    private final JFrame frame;
    private PrintWriter writer;
    private JTextArea messageArea;
    private JPanel panel1;
    private JTextField messageTextField;
    private JButton sendButton;
    private JList userList;
    private JList channelList;
    private JButton addChannelButton;
    private ResponseBuilder responseBuilder = new ResponseBuilder();
    private DefaultListModel<ClientChannel> channelListModel = new DefaultListModel<>();
    //private DefaultListModel<String> userListModel = new DefaultListModel<>();
    private ClientChannel selectedChannel;


    public ClientGUI(Socket socket) {

        try {
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),
                    StandardCharsets.ISO_8859_1), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setListeners();


        //channelListModel.addElement(new ClientChannel("#anime"));
        //channelListModel.addElement(new ClientChannel("#main"));


        channelList.setModel(channelListModel);
        //userList.setModel(userListModel);


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
        System.out.println (channelName + " : ");
        System.out.println(ch);
        if (ch != null) {
            String msg = userName + " " + timeAndDate + " " + trailing+ "\n";
            ch.addMessage(msg);
            if (selectedChannel.compareName(channelName)) {
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
                ClientChannel ch = (ClientChannel) channelList.getSelectedValue();
                if (ch != null) {
                    userList.setModel(ch.getUsersListModel());
                    messageArea.setText("");
                    for (String message : ch.getMessages()){
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
                return;
            }
        }
    }

    public void removeUserFromChannel(String userName, String channelName) {
        for (int i = 0; i < channelListModel.size(); i++) {
            ClientChannel ch = channelListModel.get(i);
            if (ch.compareName(channelName)) {
                ch.removeUser(userName);
                return;
            }
        }
    }

    public void removeUser(String s) {
        for (int i = 0; i < channelListModel.size(); i++) {
            ClientChannel ch = channelListModel.get(i);
            ch.removeUser(s);
        }
    }

    public void removeChannel(String s) {
        for (int i = 0; i < channelListModel.size(); i++) {
            ClientChannel ch = channelListModel.get(i);
            if (ch.compareName(s)) {
                channelListModel.remove(i);
                return;
            }
        }
    }

    public void addChannel(String name, List<String> users) {
        channelListModel.addElement(new ClientChannel(name, users));
    }

    private void handleCommand(String command, String msg) {
        String formattedMessage = null;
        switch (command) {
            case "/join":
                System.out.println("join");
                formattedMessage = responseBuilder.build(MessageCodes.JOIN, new String[]{msg});
                break;

            case "/leave":
                formattedMessage = responseBuilder.build(MessageCodes.PART, new String[]{msg});
                break;

            case "/nick":
                formattedMessage = responseBuilder.build(MessageCodes.NICKNAME, new String[]{msg});
                break;

            default:
                break;
        }
        if (formattedMessage != null) {
            System.out.println("sending: " + formattedMessage);
            writer.println(formattedMessage);
        }
    }

    private void handleSendMessage(String msg) {
        if (selectedChannel != null) {
            String formattedMessage = responseBuilder.build(MessageCodes.MESSAGE,
                    new String[]{selectedChannel.toString()}, msg);
            System.out.println("sending message: " + formattedMessage);
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
}
