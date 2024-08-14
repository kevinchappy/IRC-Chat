package Client;

import Helpers.MessageParser;
import Helpers.ParsedMessage;
import Helpers.ResponseCodes;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class ServerMessageHandler implements Runnable {

    private final ClientGUIController client;
    private final UserListController userListController;
    private final ChannelListController channelListController;
    private final BufferedReader reader;
    private final Socket socket;

    /**
     * Instantiates ServerMessageHandler
     *
     * @param socket                Server's socket
     * @param reader                Server input stream reader
     * @param client                Main program GUI component
     * @param userListController    List of all users GUI component
     * @param channelListController List of all channels GUI component
     */
    public ServerMessageHandler(Socket socket, BufferedReader reader, ClientGUIController client, UserListController userListController, ChannelListController channelListController) {
        this.reader = reader;
        this.client = client;
        this.userListController = userListController;
        this.channelListController = channelListController;
        this.socket = socket;
    }

    /**
     * Main loop for handling incoming messages from server. Parses messages and sends them to be handled.
     * Restarts program if connection to server is closed or has error.
     */
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
                break;
            }
        }
        kill();
    }

    /**
     * Closes connection to server. Destroys all ui elements and resets program to the start.
     */
    public void kill() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }

        client.dispose();
        userListController.dispose();
        channelListController.dispose();

        Client.main(new String[]{});
    }

    /**
     * Handles incoming messages from the server.
     * Handles different actions based on response code from the server.
     *
     * @param parsedMessage message to be handled
     */
    private void handleMessage(ParsedMessage parsedMessage) {
        ArrayList<String> params = parsedMessage.params();
        String trailing = parsedMessage.trailing();
        String channelName;
        String userName;
        String timeAndDate;
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
                    params.add(client.getCurrentUserName());
                    client.addChannel(channelName, params);
                }
                break;

            case ResponseCodes.JOINED_CHANNEL:
                channelName = params.removeFirst();
                client.addChannel(channelName, params);
                break;

            case ResponseCodes.LEFT_CHANNELS:
                channelName = params.getFirst();
                client.removeChannel(channelName);
                break;

            case ResponseCodes.CREATED_CHANNEL:
                channelName = params.removeFirst();
                ArrayList<String> list = new ArrayList<>();
                list.add(params.getFirst());
                client.addChannel(channelName, list);
                break;

            case ResponseCodes.NAME_SUCCESS:
                userName = params.getFirst();
                JOptionPane.showMessageDialog(new JFrame(), "Name changed to: " + userName);
                client.setCurrentUserName(userName);
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
                client.setCurrentUserName(params.getFirst());
                break;

            case ResponseCodes.NAME_LIST:
                userListController.setList(params);
                break;

            case ResponseCodes.CHANNEL_NAMES:
                channelListController.setList(params);
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
