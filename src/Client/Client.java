package Client;

import helper.MessageParser;

import java.io.IOException;
import java.net.Socket;


public class Client {

    public static final String DEFAULT_PORT = "6667";

    private Socket socket;
    private boolean active = true;
    private JoinGUI joinGUI;
    private MessageParser parser;

    public static void main(String[] args) {
        new Client();
    }

    public Client(){
        joinGUI = new JoinGUI(this);
        joinGUI.show();
    }

    public void initiateConnection(String ip, int port){
        try{
            socket = new Socket(ip, port);
            joinGUI.dispose();

            ClientGUI clientGUI = new ClientGUI(socket);
            clientGUI.show();

            Thread t = new Thread(new ServerMessageHandler(socket, clientGUI));
            t.start();

        }catch (IOException | NumberFormatException e){
            joinGUI.showErrorMessage();
        }
    }

}
