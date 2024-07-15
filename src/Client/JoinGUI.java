package Client;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JoinGUI {
    private final Client client;
    private final JFrame frame;
    private JTextField textField1;
    private JTextField textField2;
    private JButton button1;
    private JPanel panel1;






    public JoinGUI(Client client){
        this.client = client;
        frame = new JFrame("Join");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(400,250);
        setButtonAction();
        setPortTextField();
    }



    private void setButtonAction(){
        button1.addActionListener(v ->{
            String ip = textField1.getText();
            int port = -1;
            try{
            port = Integer.parseInt(textField2.getText());
            }catch (NumberFormatException e){
                showErrorMessage();
            }
            if(port != -1){
                client.initiateConnection(ip,port);
            }
        });
    }

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




    public void showErrorMessage(){
        JOptionPane.showMessageDialog(new JFrame(), "ERROR", "Dialog", JOptionPane.ERROR_MESSAGE);
    }

    public void show(){
        frame.setVisible(true);
    }

    public void hide(){
        frame.setVisible(false);
    }

    public void dispose(){
        frame.dispose();
    }
}
