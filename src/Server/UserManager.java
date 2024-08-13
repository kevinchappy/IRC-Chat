package Server;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Class that wraps a vector of users.
 * Provides some helpful methods to manipulate the collection.
 */
public class UserManager {
    private final Vector<User> users = new Vector<>();
    private final ChannelManager ch;

    /**
     * Instantiates a new user manager.
     *
     * @param ch the ChannelManager for the server.
     */
    public UserManager(ChannelManager ch) {
        this.ch = ch;
    }

    /**
     * Adds user to the server
     *
     * @param user the user to be added
     */
    public void add(User user) {
        users.add(user);
    }

    /**
     * Removes user from server and all channel objects.
     *
     * @param user the user to be removed
     */
    public void removeUser(User user) {
        users.remove(user);
        ch.removeUser(user);
    }

    /**
     * Checks if a user exists on the server
     *
     * @param name the name of the user
     * @return 'true' if user exists, otherwise 'false'
     */
    public boolean userExists(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets user by name.
     *
     * @param name the name of the user
     * @return the User or null if it does not exist
     */
    public User getUserByName(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Broadcasts a message to all users on the server
     *
     * @param msg the msg
     */
    public void broadCastToAllUsers(String msg) {
        for (User user : users) {
            user.broadcastMessage(msg);
        }
    }

    /**
     * Gets an arraylist containing all usernames
     *
     * @return ArrayList containing all usernames
     */
    public ArrayList<String> getAllUserNames() {
        ArrayList<String> names = new ArrayList<>();
        for (User user : users) {
            names.add(user.getName());
        }
        return names;
    }
}
