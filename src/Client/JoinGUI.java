package Client;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Class handles taking user input for initiating server connection.
 * Input is sent to an instance of Client to initiate connection
 */
public class JoinGUI {
    private final JFrame frame;
    private JTextField textField1;
    private JTextField textField2;
    private JButton button1;
    private JPanel panel1;

    public JoinGUI(){
        frame = new JFrame("Join");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(400,250);
        setButtonAction();
        setPortTextField();
    }

    public void show(){
        frame.setVisible(true);
    }

    /**
     * Sets button action to initiate connection to server based on user input
     */
    private void setButtonAction(){
        button1.addActionListener(v ->{
            String ip = textField1.getText();
            int port = -1;
            try{
            port = Integer.parseInt(textField2.getText());
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
        textField2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if(!Character.isDigit(c) || textField2.getText().length() > 4){
                    e.consume();
                }
            }
        });

        textField2.setTransferHandler(null);
        textField2.setText(Client.DEFAULT_PORT);
    }



    /**
     * Handles initiating connection to server.
     *
     *
     * @param address ip address for server
     * @param port    port number for server connection
     */
    public void initiateConnection(String address, int port) {
        try {
            Socket socket = new Socket(address, port);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),
                    StandardCharsets.ISO_8859_1), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            UserList userList = new UserList(writer);
            ChannelList channelList = new ChannelList(writer);
            ClientGUIController clientGUI = new ClientGUIController(userList, channelList, writer);
            Thread t = new Thread(new ServerMessageHandler(socket, reader, clientGUI, userList, channelList));


            t.start();
            frame.dispose();
            clientGUI.show();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error: Unable to connect to server", "Dialog", JOptionPane.ERROR_MESSAGE);
        }
    }

}
