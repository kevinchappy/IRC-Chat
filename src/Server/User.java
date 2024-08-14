package Server;

import java.io.*;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class that represents a user connected to the server.
 */
public class User {
    private final Socket socket;
    private final OutputStream outputStream;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Vector<Channel> channels = new Vector<>();
    private final InputStream input;
    private final PrintWriter writer;
    private String name;


    /**
     * Instantiates new User.
     *
     * @param socket User's socket
     * @param name   Name of user
     * @throws IOException possibly thrown when getting input and output streams.
     */
    public User(Socket socket, String name) throws IOException {
        this.socket = socket;
        this.name = name;
        this.input = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.writer = new PrintWriter(outputStream);
    }

    /**
     * Locks user's write lock.
     */
    public void lock() {
        lock.writeLock().lock();
    }

    /**
     * Unlocks user's write lock.
     */
    public void unlock() {
        lock.writeLock().unlock();
    }

    /**
     * Gets users name.
     * Has take user's read lock to return.
     *
     * @return The name of the user
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
     * Sets the name for this user.
     *
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a channel to the user's list of channels.
     *
     * @param channel The channel to be added.
     */
    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    /**
     * Removes a channel from the list of channels.
     *
     * @param channel The channel to be removed.
     */
    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    /**
     * Retrieves the list of channels associated with this user.
     *
     * @return A vector containing the channels the user is part of.
     */
    public Vector<Channel> getChannels() {
        return channels;
    }

    /**
     * Retrieves the input stream for this user.
     *
     * @return The input stream associated with the user.
     */
    public InputStream getInput() {
        return input;
    }

    /**
     * Writes message to the user
     *
     * @param msg The message to be sent
     */
    public synchronized void broadcastMessage(String msg) {
        writer.println(msg);
        writer.flush();
    }

    /**
     * Closes streams and socket for specific user.
     *
     * @throws IOException possibly thrown when closing streams and socket
     */
    public void close() throws IOException {
        input.close();
        outputStream.close();
        writer.close();
        socket.close();
    }

    /**
     * Equals override. Uses name of user to check equality
     *
     * @param obj object to be compared with this
     * @return 'true' if equal, 'false' if not
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof User other)) {
            return false;
        }

        return name.equals(other.getName());
    }

    /**
     * Hash code override. Uses name to generate hashcode.
     *
     * @return the Hashcode of the users name
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * To string. Uses name of user.
     *
     * @return name of user
     */
    @Override
    public String toString() {
        return name;
    }
}
