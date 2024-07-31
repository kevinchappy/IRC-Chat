package Server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that wraps a Concurrent hashmap with channel names as keys and channel as values.
 * Provides some helpful methods
 */
public class ChannelHandler {

    private final ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<>();

    public void addChannels(Channel channel) {
        channels.put(channel.getName(), channel);
    }


    public void removeChannel(String name) {
        channels.remove(name);
    }

    public Channel getChannel(String name) {
        return channels.get(name);
    }

    public String[] getAllChannelNames(){
        ArrayList<String> names = new ArrayList<>(channels.keySet());
        return names.toArray(names.toArray(new String[0]));
    }

    /**
     * Remove user from all channels. Removes channel if it is empty after removing user
     *
     * @param user the user
     */
    public void removeUser(User user) {
        for (Iterator<Channel> iter = channels.values().iterator(); iter.hasNext(); ) {
            Channel channel = iter.next();
            channel.remove(user);
            if (channel.isEmpty()) {
                iter.remove();
            }
        }
    }
}
