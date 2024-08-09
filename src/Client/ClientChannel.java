package Client;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a channel in the client.
 * Contains the channel name, a list model of users in the channel and a list of messages.
 */
public class ClientChannel {
    private final DefaultListModel<String> usersListModel;
    private final ArrayList<String> messages = new ArrayList<>();
    private String name;
    private boolean hasNoMessages = true;


    /**
     * Instantiates a new Client channel.
     *
     * @param name channel name
     * @param arr  array of users
     */
    public ClientChannel(String name, List<String> arr) {
        this.name = name;
        this.usersListModel = new DefaultListModel<>();

        synchronized (this) {
            this.usersListModel.addAll(arr);
        }
    }


    /**
     * Gets name of channel.
     *
     * @return the channels name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the channels name.
     *
     * @param name The channels new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets users list model.
     *
     * @return the users list model
     */
    public DefaultListModel<String> getUsersListModel() {
        return usersListModel;
    }

    /**
     * Gets list of messages.
     *
     * @return the messages
     */
    public ArrayList<String> getMessages() {
        return messages;
    }


    /**
     * Add new user to list model.
     *
     * @param name the name of the new user.
     */
    public synchronized void addUser(String name) {
        usersListModel.addElement(name);
    }

    /**
     * Removes user from users list model.
     * Returns boolean based on operation success.
     *
     * @param name The name of the user to be removed.
     * @return true if successful, false if failed.
     */
    public synchronized boolean removeUser(String name) {
        return usersListModel.removeElement(name);
    }

    /**
     * Updates username if it exists in users list model.
     *
     * @param oldName the old username
     * @param newName the new username
     */
    public synchronized void updateUser(String oldName, String newName) {
        int index = usersListModel.indexOf(oldName);
        if (index != -1) {
            System.out.println(usersListModel.get(index));
            usersListModel.setElementAt(newName, index);
            System.out.println(usersListModel.get(index) + "\n");

        }
    }

    /**
     * Compares channel name with name string.
     *
     * @param name to be compared with channel name
     * @return the boolean
     */
    public boolean compareName(String name) {
        return this.name.equals(name);
    }

    /**
     * Adds new message to message list.
     *
     * @param msg the message to be added.
     */
    public void addMessage(String msg) {
        messages.add(msg);
    }

    /**
     * Returns no messages boolean
     *
     * @return true if no messages, false if there are messages.
     */
    public boolean hasNoMessages() {
        return hasNoMessages;
    }

    /**
     * Sets hasNoMessages to false. For when adding first message to message list.
     */
    public void setFirstMessage() {
        hasNoMessages = false;
    }

    /**
     * To string.
     *
     * @return channel name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Equals. Uses channel name for comparison
     *
     * @param obj the object to be compared
     * @return the boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ClientChannel ch)) {
            return false;
        }

        return ch.name.equals(this.name);
    }

    /**
     * Hash code. Uses channel name hash code.
     *
     * @return name hash code
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
