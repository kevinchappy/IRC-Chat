package Client.Gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Class that defined GUI elements and exposes certain methods to be used in other classes.
 */
public class listGUI {
    private final JFrame frame;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> list1;
    private JPanel panel1;
    private JButton leftButton;
    private JButton rightButton;

    /**
     * Instantiates new listGUI
     * Configures GUI parameters and sets list model.
     */
    public listGUI() {
        list1.setModel(listModel);

        frame = new JFrame("");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setSize(300, 500);
    }

    /**
     * Sets the GUI title.
     *
     * @param title The new title.
     */
    public void setTitle(String title) {
        frame.setTitle(title);
    }

    /**
     * Shows the GUI.
     */
    public void show() {
        frame.setVisible(true);
    }

    /**
     * Disposes the GUI.
     */
    public void dispose() {
        frame.dispose();
    }

    /**
     * Retrieves the currently selected value from the list.
     *
     * @return The selected value, or null if nothing is selected
     */
    public String getSelected() {
        return list1.getSelectedValue();
    }

    /**
     * Sets an action listener for the left button.
     *
     * @param e The action listener .
     */
    public void setLeftButtonListener(ActionListener e) {
        leftButton.addActionListener(e);
    }

    /**
     * Sets an action listener for the right button.
     *
     * @param e The action listener.
     */
    public void setRightButtonListener(ActionListener e) {
        rightButton.addActionListener(e);
    }

    /**
     * Sets the text for the right button.
     *
     * @param str The text to be displayed on the right button.
     */
    public void setRightButtonName(String str) {
        rightButton.setText(str);
    }

    /**
     * Sets the text for the left button.
     *
     * @param str The text to be displayed on the left button.
     */
    public void setLeftButtonName(String str) {
        leftButton.setText(str);
    }

    /**
     * Updates the list of all users with new values.
     *
     * @param list The new list of values to set.
     */
    public void setList(List<String> list) {
        listModel.clear();
        listModel.addAll(list);
    }
}
