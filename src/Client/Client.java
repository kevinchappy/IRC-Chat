package Client;


import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class Client {

    public static final String DEFAULT_PORT = "6667";

    private final JoinGUI joinGUI;

    public static void main(String[] args) {
        new Client();
    }

    public Client(){
        joinGUI = new JoinGUI(this);
        joinGUI.show();
    }

    public void initiateConnection(String address, int port){
        try{
            Socket socket = new Socket(address, port);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),
                    StandardCharsets.ISO_8859_1), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            joinGUI.dispose();
            UserList userList = new UserList(writer);
            ChannelList channelList = new ChannelList(writer);
            ClientGUI clientGUI = new ClientGUI(userList, channelList, writer);
            Thread t = new Thread(new ServerMessageHandler(socket, reader, clientGUI, userList, channelList));

            t.start();
            clientGUI.show();

        }catch (IOException | NumberFormatException e){
            joinGUI.showErrorMessage();
        }
    }

}
