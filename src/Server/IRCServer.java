package Server;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class IRCServer {


    public static final int defaultPort = 6667;
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final Vector<User> users = new Vector<>();
    private final ChannelHandler channelHandler = new ChannelHandler(this);
    private boolean alive = true;



    public static void main(String[] args) {
        new IRCServer();
    }

    public IRCServer() {
        Runnable clientHandler = new NewClientHandler(this);
        EXECUTOR.submit(clientHandler);
    }

    public Vector<User> getUsers() {
        return users;
    }

    public void close(){
        alive = false;
    }

    public boolean isAlive(){
        return alive;
    }

    public ChannelHandler getChannelHandler(){
        return channelHandler;
    }

    public void removeUser(User user){
        users.remove(user);
        channelHandler.removeUser(user);
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
}
