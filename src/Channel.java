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

    public void broadcast(String msg) {
        System.out.println("before channel.broadcast() " + msg);
        for (User user : users) {
            user.broadcastMessage(msg);
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
}
