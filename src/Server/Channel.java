package Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class that represents a server channel with a thread-safe implementation.
 */
public class Channel {
    private final String name;
    private final Vector<User> users = new Vector<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Instantiates a new Channel.
     *
     * @param name The name of the channel
     */
    public Channel(String name) {
        this.name = name;
    }


    /**
     * Adds new user to channel. Ensures that no duplicates are added.
     *
     * @param user the user
     */
    public void add(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    /**
     * Returns if users list is empty
     *
     * @return 'true' if users list is empty, otherwise false
     */
    public boolean isEmpty(){
        return users.isEmpty();
    }

    /**
     * Removes user from channel.
     *
     * @param user the user to be removed
     * @return 'true' if user successfully removed, 'false' otherwise
     */
    public boolean remove(User user) {
        return users.remove(user);
    }

    /**
     * Broadcasts message to all users within the channel. Can optionally ignore one user in the channel to be broadcast to.
     *
     * @param msg         Message to be broadcast
     * @param ignoredUser Name of a user that will not be broadcast to. Typically, the sender.
     */
    public synchronized void broadcast(String msg, String ignoredUser) {
        for (User user : users) {
            if (ignoredUser == null || !user.getName().equals(ignoredUser)){
                user.broadcastMessage(msg);
            }
        }
    }

    /**
     * Iterates through users and creates list of all names.
     *
     * @return A list of the names of all users in the channel.
     */
    public List<String> getUserNames(){
        List<String> temp = new ArrayList<>();
        for (User user : users){
            temp.add(user.getName());
        }
        return temp;
    }


    /**
     * Gets name of channel.
     * Takes read lock.
     *
     * @return The name of the channel
     */
    public String getName() {
        lock.readLock().lock();
        try {
            return name;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Equals method. Uses channel name to check equality.
     *
     * @param obj the object to be compared with 'this'
     * @return true if equal, false if not equal
     */
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

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
