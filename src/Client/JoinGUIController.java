package Client;

import Client.Gui.JoinGUI;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Handles initiating connection to server and spawning server reading thread and all GUI components.
 */
public class JoinGUIController {

    private final JoinGUI gui = new JoinGUI();

    /**
     * Instantiates a new Join gui controller.
     */
    public JoinGUIController(){
        setButtonListener();
        setPortTextField();
    }

    /**
     * Shows GUI
     */
    public void show(){
        gui.show();
    }

    /**
     * Sets join channel button listener.
     * When pressed, listener will attempt to grab information from
     * the address and port textfields and attempt to initiate server connection.
     */
    private void setButtonListener(){
        gui.addButtonListener(v ->{
            String ip = gui.getAdressText();
            int port = -1;
            try{
                port = Integer.parseInt(gui.getPortText());
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(new JFrame(), "Error: Port number wrong format", "Dialog", JOptionPane.ERROR_MESSAGE);
            }
            if(port != -1){
                initiateConnection(ip,port);
            }
        });
    }

    /**
     * Sets port textfield to only accept digits, not allow copy/paste and have a preset port
     */
    private void setPortTextField() {
        gui.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if(!Character.isDigit(c) || gui.getPortText().length() > 4){
                    e.consume();
                }
            }
        });
        gui.setPortTextField(Client.DEFAULT_PORT);
    }


    /**
     * Handles initiating connection to server.
     * Creates PrintWriter and BufferedReader to server that are used by other components.
     * Initiates and starts all GUI components.
     *
     * @param address ip address for server
     * @param port    port number for server connection
     */
    public void initiateConnection(String address, int port) {
        try {
            Socket socket = new Socket(address, port);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.ISO_8859_1), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            UserList userList = new UserList(writer);
            ChannelList channelList = new ChannelList(writer);
            ClientGUIController clientGUI = new ClientGUIController(socket, userList, channelList, writer);
            Thread t = new Thread(new ServerMessageHandler(socket, reader, clientGUI, userList, channelList));

            t.start();
            gui.dispose();
            clientGUI.show();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error: Unable to connect to server", "Dialog", JOptionPane.ERROR_MESSAGE);
        }
    }

}
