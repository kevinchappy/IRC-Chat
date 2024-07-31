package Client;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.List;

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

    public ClientGUI() {
        channelList.setModel(channelListModel);

        frame = new JFrame("IRC Client");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000, 800);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void dispose() {
        frame.dispose();
    }

    public DefaultListModel<ClientChannel> getChannelListModel() {
        return channelListModel;
    }

    public void printToTextArea(String msg, ClientChannel channel){
        if (activeChannel != null && activeChannel.equals(channel)) {
            messageArea.append(msg);
        }
    }

    public void addChannelListSelectionListener(ListSelectionListener e){
        channelList.addListSelectionListener(e);
    }

    public void sendButtonActionListener(ActionListener e){
        sendButton.addActionListener(e);
    }

    public void messageTextFieldActionListener (ActionListener e){
        messageTextField.addActionListener(e);
    }

    public void seeAllUsersActionListener(ActionListener e){
        seeAllUsersButton.addActionListener(e);
    }

    public void seeAllChannelsActionListener(ActionListener e){
        seeAllChannelsButton.addActionListener(e);
    }

    public void resetUserList(){
        userList.setModel(new DefaultListModel<>());
    }


    public ClientChannel getActiveChannel(){
        return activeChannel;
    }



    public void setActiveChannel(ClientChannel channel){
        activeChannel = channel;
    }

    public ClientChannel getSelectedChannel(){
        return channelList.getSelectedValue();
    }

    public void clearTextField(){
        messageTextField.setText("");
    }

    public void clearMessageArea(){
        messageArea.setText("");
    }
    public String getText(){
        return messageTextField.getText();
    }

    public void setUserListModel(DefaultListModel<String> model){
        userList.setModel(model);
    }

    public void populateMessageArea(List<String> messages){
        for (String msg : messages){
            messageArea.append(msg);
        }
    }
}
