package Client.Gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

/**
 * Class handles taking user input for initiating server connection.
 * Input is sent to an instance of Client to initiate connection
 */
public class JoinGUI {
    private final JFrame frame;
    private JTextField addressTextField;
    private JTextField portTextField;
    private JButton button1;
    private JPanel panel1;

    public JoinGUI(){
        frame = new JFrame("Join");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(400,250);
        portTextField.setTransferHandler(null);
    }

    public void show(){
        frame.setVisible(true);
    }

    public void dispose(){
        frame.dispose();
    }

    public void setPortTextField(String portNumber){
        portTextField.setText(portNumber);
    }

    public void addButtonListener(ActionListener e){
        button1.addActionListener(e);
    }

    public void addKeyListener(KeyListener e){
        portTextField.addKeyListener(e);
    }

    public String getPortText(){
        return portTextField.getText();
    }

    public String getAdressText(){
        return addressTextField.getText();
    }

}
