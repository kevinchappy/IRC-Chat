package Server;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Class that wraps a vector of users and provides some helpful methods for
 */
public class UserManager {
    private final Vector<User> users = new Vector<>();
    private final ChannelManager ch;

    public UserManager(ChannelManager ch){
        this.ch = ch;
    }

    public void add(User user){
        users.add(user);
    }

    public void removeUser(User user){
        users.remove(user);
        ch.removeUser(user);
    }

    public boolean userExists(String name){
        for (User user : users){
            if (user.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public User getUserByName(String name){
        for (User user : users){
            if (user.getName().equals(name)){
                return user;
            }
        }
        return null;
    }
    public void broadCastToAllUsers(String msg){
        for (User user : users){
            user.broadcastMessage(msg);
        }
    }

    public ArrayList<String> getAllUserNames(){
        ArrayList<String> names = new ArrayList<>();
        for (User user : users){
            names.add(user.getName());
        }
        return names;
    }
}
