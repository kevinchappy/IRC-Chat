import java.io.*;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
    private final Socket socket;
    private final OutputStream outputStream;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Vector<Channel> channels = new Vector<>();
    private String name;
    private InputStream input;
    private PrintWriter writer;



    public User(Socket socket, String name) throws IOException {
        this.socket = socket;
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
        lock.writeLock().lock();
        this.name = name;
        lock.writeLock().unlock();
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
}
