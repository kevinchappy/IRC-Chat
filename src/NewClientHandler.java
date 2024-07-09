import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

public class NewClientHandler implements Runnable {
    private final IRCServer ircServer;
    public NewClientHandler(IRCServer ircServer) {
        this.ircServer = ircServer;
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(IRCServer.defaultPort)) {
            while (ircServer.isAlive()) {
                try {
                    Socket socket = server.accept();
                    User user = new User(socket, "Guest");
                    ircServer.getUsers().add(user);
                    Runnable clientHandler = new ClientHandler(user, ircServer);
                    IRCServer.EXECUTOR.submit(clientHandler);

                } catch (IOException ex) {
                    System.err.println("IOEXCEPTION");
                }
            }
        } catch (IOException ex) {
            System.err.println("IOEXCEPTION");
        }
    }
}
