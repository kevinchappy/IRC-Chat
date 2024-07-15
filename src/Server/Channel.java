package Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Channel {
    private String name;
    private final Vector<User> users = new Vector<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public Channel(String name) {
        this.name = name;
    }


    public void add(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public boolean isEmpty(){
        return users.isEmpty();
    }

    public void remove(User user) {
        users.remove(user);
    }

    /**
     * Broadcasts message to all users within the channel
     *
     * @param msg         Message to be broadcast
     * @param ignoredUser Name of a user that will not be broadcast to. Typically sender.
     */
    public void broadcast(String msg, String ignoredUser) {
        for (User user : users) {
            if (ignoredUser == null || !user.getName().equals(ignoredUser)){
                user.broadcastMessage(msg);
            }
        }
    }

    public List<String> getUserNames(){
        List<String> temp = new ArrayList<>();
        for (User user : users){
            temp.add(user.getName());
        }
        return temp;
    }

    public void setName(String name){
        lock.writeLock().lock();
        try{
            this.name = name;
        }finally {
            lock.writeLock().unlock();
        }
    }

    public String getName() {
        lock.readLock().lock();
        try {
            return name;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }

        if(!(obj instanceof Channel other)){
            return false;
        }

        return name.equals(other.getName());
    }


    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
