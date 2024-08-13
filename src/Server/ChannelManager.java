package Server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that wraps a Concurrent hashmap with channel names as keys and channel as values.
 * Provides some helpful methods to manipulate the collection.
 */
public class ChannelManager {
    private final ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<>();

    /**
     * Adds a channel to the map and uses its name as key.
     *
     * @param channel The channel to be added.
     */
    public void addChannels(Channel channel) {
        channels.put(channel.getName(), channel);
    }

    /**
     * Removes specified channel from the map.
     *
     * @param name The name of the channel to be removed.
     */
    public void removeChannel(String name) {
        channels.remove(name);
    }

    /**
     * Retrieves a channel from the collection by its name.
     *
     * @param name The name of the channel to retrieve.
     * @return The channel mapped to the specified name, otherwise `null` if not found.
     */
    public Channel getChannel(String name) {
        return channels.get(name);
    }


    /**
     * Creates an array of all channel names from the channels map.
     *
     * @return An array containing the names of all channels.
     */
    public String[] getAllChannelNames() {
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
