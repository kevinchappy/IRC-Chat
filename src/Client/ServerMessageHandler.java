package Client;

import helper.MessageParser;
import helper.ParsedMessage;
import helper.ResponseCodes;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;


public class ServerMessageHandler implements Runnable {

    private final Socket socket;
    private final ClientGUI client;
    private BufferedReader reader;

    public ServerMessageHandler(Socket socket, ClientGUI client) {
        this.socket = socket;
        this.client = client;

        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        ParsedMessage parsedMessage;
        while (true) {
            try {
                parsedMessage = MessageParser.parse(reader.readLine());
                handleMessage(parsedMessage);

            } catch (IOException e) {
                e.printStackTrace();
                kill();
                break;
            }
        }
    }

    public void kill() {
        try {
            reader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(ParsedMessage parsedMessage) {
        ArrayList<String> params = parsedMessage.params();
        String trailing = parsedMessage.trailing();
        String channelName;
        switch (parsedMessage.command()) {
            case ResponseCodes.CHANNEL_MSG:
                channelName = params.get(2);
                String userName = params.get(1);
                String timeAndDate = params.get(0);
                client.printMessage(trailing, channelName, userName, timeAndDate);
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
                JOptionPane.showMessageDialog(new JFrame(), "Name changed to: " + params.getFirst());
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

            default:
                break;
        }
    }
}
