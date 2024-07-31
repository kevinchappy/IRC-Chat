package Client;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;

public class listGUI {
    private final JFrame frame;
    private JList<String> list1;
    private JPanel panel1;
    private JButton leftButton;
    private JButton rightButton;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    public listGUI(){
        list1.setModel(listModel);

        frame = new JFrame("");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setSize(300, 500);


    }

    public void setTitle(String title){
        frame.setTitle(title);
    }

    public void show(){
        frame.setVisible(true);
    }

    public void dispose(){
        frame.dispose();
    }

    public String getSelected(){
        return list1.getSelectedValue();
    }

    public void setLeftButtonListener(ActionListener e){
        leftButton.addActionListener(e);
    }

    public void setRightButtonListener(ActionListener e){
        rightButton.addActionListener(e);
    }

    public void setRightButtonName(String str){
        rightButton.setText(str);
    }

    public void setLeftButtonName(String str){
        leftButton.setText(str);
    }

    public void setList(List<String> list){
        listModel.clear();
        listModel.addAll(list);
    }
}
