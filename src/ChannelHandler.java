import java.util.concurrent.ConcurrentHashMap;

public class ChannelHandler {

    private final ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<>();
    private final IRCServer ircServer;

    public ChannelHandler(IRCServer ircServer){
        this.ircServer = ircServer;

    }


    public boolean channelExists(String channelName){
        return channels.containsKey(channelName);
    }

    public void addChannels(String name){
        channels.put(name, new Channel(name));
    }

    /**
     * Remove user from all channels.
     *
     * @param user the user
     */
    public void removeUser(User user){
        for(String string : channels.keySet()){
            Channel channel = channels.get(string);
            channel.remove(user);
        }
    }


    public void removeChannel(String name){
        channels.remove(name);
    }

    public Channel getChannel(String name){
        return channels.get(name);
    }

    public void addUserToChannel(User user, String name){
        channels.get(name).add(user);
    }



}
