package Client;

import helper.MessageParser;
import helper.ParsedMessage;
import helper.ResponseCodes;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class ServerMessageHandler implements Runnable {

    private final ClientGUIController client;
    private final UserList userList;
    private final ChannelList channelList;
    private final BufferedReader reader;
    private final Socket socket;

    public ServerMessageHandler(Socket socket, BufferedReader reader, ClientGUIController client, UserList userList, ChannelList channelList) {
        this.reader = reader;
        this.client = client;
        this.userList = userList;
        this.channelList = channelList;
        this.socket = socket;


    }

    @Override
    public void run() {

        ParsedMessage parsedMessage;
        while (true) {
            try {
                parsedMessage = MessageParser.parse(reader.readLine());
                if (parsedMessage != null) {
                    handleMessage(parsedMessage);
                }

            } catch (IOException e) {
                kill();
                break;
            }
        }
    }

    public void kill() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.dispose();
        userList.dispose();
        channelList.dispose();

        Client.main(new String[]{});
    }

    private void handleMessage(ParsedMessage parsedMessage) {
        ArrayList<String> params = parsedMessage.params();
        String trailing = parsedMessage.trailing();
        String channelName;
        String userName;
        String timeAndDate;
        System.out.println(parsedMessage);
        switch (parsedMessage.command()) {
            case ResponseCodes.CHANNEL_MSG:
                channelName = params.get(2);
                userName = params.get(1);
                timeAndDate = params.get(0);
                client.printMessage(trailing, channelName, userName, timeAndDate);
                break;

            case ResponseCodes.USER_MSG:
                userName = params.get(1);
                client.createPrivateChannelIfNotExists(userName);
                timeAndDate = params.get(0);
                client.printMessage(trailing, userName, userName, timeAndDate);
                break;

            case ResponseCodes.ESTABLISH_PRIVATE_RES:
                channelName = params.getFirst();
                if (!client.channelExists(channelName)) {
                    params.add(client.getName());
                    client.addChannel(channelName, params, true);
                }
                break;

            case ResponseCodes.JOINED_CHANNEL:
                channelName = params.removeFirst();
                client.addChannel(channelName, params, false);
                break;

            case ResponseCodes.LEFT_CHANNELS:
                channelName = params.getFirst();
                client.removeChannel(channelName);
                break;

            case ResponseCodes.CREATED_CHANNEL:
                channelName = params.removeFirst();
                ArrayList<String> list = new ArrayList<>();
                list.add(params.getFirst());
                client.addChannel(channelName, list, false);
                break;

            case ResponseCodes.NAME_SUCCESS:
                userName = params.getFirst();
                JOptionPane.showMessageDialog(new JFrame(), "Name changed to: " + userName);
                client.setName(userName);
                break;

            case ResponseCodes.USER_JOINED_CHANNEL:
                channelName = params.get(0);
                String toAdd = params.get(1);
                client.addUserToChannel(toAdd, channelName);
                break;

            case ResponseCodes.USER_LEFT_CHANNEL:
                channelName = params.get(0);
                String toRemove = params.get(1);
                client.removeUserFromChannel(toRemove, channelName);
                break;

            case ResponseCodes.CHANGED_NAME:
                String oldName = params.get(0);
                String newName = params.get(1);
                client.handleNameChange(oldName, newName);
                break;

            case ResponseCodes.USER_EXIT:
                String name = params.get(0);
                client.removeUser(name);
                break;

            case ResponseCodes.JOINED_SERVER:
                client.setName(params.getFirst());
                break;

            case ResponseCodes.NAME_LIST:
                userList.setList(params);
                break;

            case ResponseCodes.CHANNEL_NAMES:
                channelList.setList(params);
                break;

            default:
                String errorMessage = "";
                if (parsedMessage.trailing() != null) {
                    errorMessage = parsedMessage.trailing().substring(1);
                }
                JOptionPane.showMessageDialog(new JFrame(), "Error " + parsedMessage.command() + ": " + errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }
}
