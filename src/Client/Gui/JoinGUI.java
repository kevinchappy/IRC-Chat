package Client.Gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

/**
 * Class that defines GUI elements for the server joining window.
 * Exposes certain methods to be used in other classes
 */
public class JoinGUI {
    private final JFrame frame;
    private JTextField addressTextField;
    private JTextField portTextField;
    private JButton button1;
    private JPanel panel1;

    /**
     * Initiates new JoinGUI.
     * Sets parameters for GUI components.
     */
    public JoinGUI() {
        frame = new JFrame("Join");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(400, 250);
        portTextField.setTransferHandler(null);
    }

    /**
     * Shows GUI component.
     */
    public void show() {
        frame.setVisible(true);
    }

    /**
     * Disposes GUI component.
     */
    public void dispose() {
        frame.dispose();
    }

    /**
     * Sets the text of the port number text field.
     *
     * @param portNumber The new port number.
     */
    public void setPortTextField(String portNumber) {
        portTextField.setText(portNumber);
    }

    /**
     * Adds an action listener to the join channel button.
     *
     * @param e The action listener to be added.
     */
    public void addButtonListener(ActionListener e) {
        button1.addActionListener(e);
    }

    /**
     * Adds a key listener to the port text field.
     *
     * @param e The key listener to be added.
     */
    public void addKeyListener(KeyListener e) {
        portTextField.addKeyListener(e);
    }

    /**
     * Gets the text from the port number text field.
     *
     * @return The text entered in the port text field.
     */
    public String getPortText() {
        return portTextField.getText();
    }

    /**
     * Gets the text from the address text field.
     *
     * @return The text in the address text field.
     */
    public String getAdressText() {
        return addressTextField.getText();
    }

}
