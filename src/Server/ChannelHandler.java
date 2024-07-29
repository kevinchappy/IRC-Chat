package Server;

import java.util.Iterator;
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

    public void addChannels(Channel channel){
        channels.put(channel.getName(), channel);
    }

    /**
     * Remove user from all channels.
     *
     * @param user the user
     */
    public void removeUser(User user){
        for(Channel channel : channels.values()){
            channel.remove(user);
        }
    }


    public void removeChannel(String name){
        channels.remove(name);
    }

    public Channel getChannel(String name){
        return channels.get(name);
    }


    public Iterator<String> getKeyIterator(){
        return channels.keySet().iterator();
    }



}