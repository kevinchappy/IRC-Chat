package Client.Gui;

import Client.ClientChannel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Class that defined GUI elements and exposes certain methods to be used in other classes.
 */
public class ClientGUI {

    private final JFrame frame;
    private JTextArea messageArea;
    private JPanel panel1;
    private JTextField messageTextField;
    private JButton sendButton;
    private JList<String> userList;
    private JList<ClientChannel> channelList;
    private JButton seeAllChannelsButton;
    private JButton seeAllUsersButton;
    private JScrollPane scrollPane;

    private final DefaultListModel<ClientChannel> channelListModel = new DefaultListModel<>();
    private ClientChannel activeChannel;

    /**
     * Initializes new Client GUI.
     * Configures GUI parameters and sets channel list model.
     */
    public ClientGUI() {
        channelList.setModel(channelListModel);

        frame = new JFrame("IRC Client");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000, 800);
    }

    /**
     * Shows GUI.
     */
    public void show() {
        frame.setVisible(true);
    }

    /**
     * Disposes GUI.
     */
    public void dispose() {
        frame.dispose();
    }

    /**
     * Gets the channel list model.
     *
     * @return The channel list model.
     */
    public DefaultListModel<ClientChannel> getChannelListModel() {
        return channelListModel;
    }

    /**
     * Appends a message to the text area if the specified channel is the active one.
     *
     * @param msg     The message to append.
     * @param channel The channel to check if it is the active one.
     */
    public void printToTextArea(String msg, ClientChannel channel){
        if (activeChannel != null && activeChannel.equals(channel)) {
            messageArea.append(msg);
        }
    }

    /**
     * Adds a selection listener to the channel list.
     *
     * @param e The listener to be added.
     */
    public void addChannelListSelectionListener(ListSelectionListener e){
        channelList.addListSelectionListener(e);
    }

    /**
     * Adds an action listener to the send button.
     *
     * @param e The listener to be added.
     */
    public void sendButtonActionListener(ActionListener e){
        sendButton.addActionListener(e);
    }

    /**
     * Adds an action listener to the message text field.
     *
     * @param e The listener to be added.
     */
    public void messageTextFieldActionListener (ActionListener e){
        messageTextField.addActionListener(e);
    }

    /**
     * Adds an action listener to the "See All Users" button.
     *
     * @param e The listener to be added.
     */
    public void seeAllUsersActionListener(ActionListener e){
        seeAllUsersButton.addActionListener(e);
    }

    /**
     * Adds an action listener to the "See All Channels" button.
     *
     * @param e The listener to be added.
     */
    public void seeAllChannelsActionListener(ActionListener e){
        seeAllChannelsButton.addActionListener(e);
    }

    /**
     * Resets the user list to an empty model.
     */
    public void resetUserList(){
        userList.setModel(new DefaultListModel<>());
    }


    /**
     * Gets the currently active channel.
     *
     * @return The active channel.
     */
    public ClientChannel getActiveChannel(){
        return activeChannel;
    }

    /**
     * Sets the active channel.
     *
     * @param channel The channel to be set as active.
     */
    public void setActiveChannel(ClientChannel channel){
        activeChannel = channel;
    }

    /**
     * Gets the currently selected channel from the channel list.
     *
     * @return The selected channel.
     */
    public ClientChannel getSelectedChannel(){
        return channelList.getSelectedValue();
    }

    /**
     * Clears the message text field.
     */
    public void clearTextField(){
        messageTextField.setText("");
    }

    /**
     * Clears the message area.
     */
    public void clearMessageArea(){
        messageArea.setText("");
    }

    /**
     * Gets the text from the message text field.
     *
     * @return The text entered by the user.
     */
    public String getText(){
        return messageTextField.getText();
    }

    /**
     * Sets the user list model with new model.
     *
     * @param model The model to set for the user list.
     */
    public void setUserListModel(DefaultListModel<String> model){
        userList.setModel(model);
    }

    /**
     * Fills message text area with list of messages.
     * @param messages list of messages to fill message area with
     */
    public void populateMessageArea(List<String> messages){
        for (String msg : messages){
            messageArea.append(msg);
        }
    }
}
