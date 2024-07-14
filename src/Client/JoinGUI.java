package Client;

import javax.swing.*;

public class JoinGUI {
    private JTextField textField1;
    private JTextField textField2;
    private JButton button1;
    private JPanel panel1;


    public static void main(String[] args) {
        new JoinGUI();
    }

    public JoinGUI(){
        JFrame frame = new JFrame("Join");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000,800);
        frame.setVisible(true);
    }

}
