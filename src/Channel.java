import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Channel {
    private String name;
    private Vector<User> users = new Vector<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public Channel(String name) {
        this.name = name;
    }


    public void add(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
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
        System.out.println("before channel.broadcast() " + msg);
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
}
