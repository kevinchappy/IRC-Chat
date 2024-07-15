package Client;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientChannel {
    private final String name;
    private DefaultListModel<String> usersListModel;
    private ArrayList<String> messages = new ArrayList<>();

    public ClientChannel(String name, List<String> arr){
        this.name = name;
        this.usersListModel = new DefaultListModel<>();
        synchronized (this){
        this.usersListModel.addAll(arr);
        }
    }


    public DefaultListModel<String> getUsersListModel() {
        return usersListModel;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return name;
    }

    public synchronized void addUser(String name){
        usersListModel.addElement(name);
    }

    public synchronized void removeUser(String name){
        usersListModel.removeElement(name);
    }

    public boolean compareName(String name){
        return this.name.equals(name);
    }



    public void addMessage(String msg){
        messages.add(msg);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }

        if (!(obj instanceof ClientChannel ch)){
            return false;
        }

        return ch.name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
