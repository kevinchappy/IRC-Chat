package Client;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ClientChannel {
    private final String name;
    private DefaultListModel<String> usersListModel;
    private ArrayList<String> messages = new ArrayList<>();
    private boolean privateChannel;

    public ClientChannel(String name, List<String> arr, boolean privateChannel) {
        this.name = name;
        this.usersListModel = new DefaultListModel<>();
        this.privateChannel = privateChannel;

        synchronized (this) {
            this.usersListModel.addAll(arr);
        }
    }

    public boolean isPrivateChannel(){
        return privateChannel;
    }

    public DefaultListModel<String> getUsersListModel() {
        return usersListModel;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }


    public synchronized void addUser(String name) {
        usersListModel.addElement(name);
    }

    public synchronized boolean removeUser(String name) {
        return usersListModel.removeElement(name);
    }

    public synchronized void updateUser(String oldName, String newName) {
        int index = usersListModel.indexOf(oldName);
        if (index != -1) {
            System.out.println(usersListModel.get(index));
            usersListModel.setElementAt(newName, index);
            System.out.println(usersListModel.get(index) + "\n");

        }
    }

    public boolean compareName(String name) {
        return this.name.equals(name);
    }

    public void addMessage(String msg) {
        messages.add(msg);
    }

    public void updateList() {

    }

    @Override
    public String toString() {
        return name;
    }

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

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}