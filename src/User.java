import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
    private final Socket socket;
    private String name;
    private InputStream input;
    private final OutputStream outputStream;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
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

    public InputStream getInput() {
        return input;
    }

    public void close() throws IOException {
        input.close();
        outputStream.close();
        writer.close();
    }

    public void broadcastMessage(String msg) {
        System.out.println("before user.broadcastMessage() " + msg);
        if (writer == null){
            writer = new PrintWriter(outputStream);
        }
        System.out.println("before writer.println() "  + msg);
        writer.println(msg);
        writer.flush();
    }
}
