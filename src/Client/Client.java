package Client;


import java.io.IOException;
import java.net.Socket;


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
