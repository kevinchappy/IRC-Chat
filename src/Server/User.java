package Server;

import java.io.*;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
    private final OutputStream outputStream;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Vector<Channel> channels = new Vector<>();
    private final InputStream input;
    private String name;
    private PrintWriter writer;



    public User(Socket socket, String name) throws IOException {
        this.name = name;
        this.input = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.writer = new PrintWriter(outputStream);

    }


    public String getName() {
        lock.readLock().lock();
        try {
            return name;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addChannel(Channel channel){
        channels.add(channel);
    }

    public void removeChannel(Channel channel){
        channels.remove(channel);
    }

    public Vector<Channel> getChannels(){
        return channels;
    }

    public InputStream getInput() {
        return input;
    }

    public void lock(){
        lock.writeLock().lock();
    }

    public void unlock(){
        lock.writeLock().unlock();
    }



    public void broadcastMessage(String msg) {
        if (writer == null){
            writer = new PrintWriter(outputStream);
        }
        writer.println(msg);
        writer.flush();
    }

    public void close() throws IOException {
        input.close();
        outputStream.close();
        writer.close();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }

        if(!(obj instanceof User other)){
            return false;
        }

        return name.equals(other.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
