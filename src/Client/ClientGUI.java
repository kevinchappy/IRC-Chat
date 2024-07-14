package Client;

import javax.swing.*;

public class ClientGUI {
    private JTextArea messageArea;
    private JPanel panel1;
    private JTextField messageTextField;
    private JButton sendButton;
    private JList userList;
    private JList channelList;
    private JLabel usersLabel;
    private JLabel ChannelLabel;
    private JLabel MessagesLabel;
    private JButton button1;

    public ClientGUI() {
        sendButton.addActionListener(e -> {

        });

        userList.addListSelectionListener(e -> {

        });

        channelList.addListSelectionListener(e ->{

        });

        JFrame frame = new JFrame("IRC Client");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000,800);
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        new ClientGUI();
    }
}
