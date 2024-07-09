import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientHandler implements Runnable {
    private final User user;
    private final IRCServer ircServer;
    private final MessageParser parser = new MessageParser();

    ClientHandler(User user, IRCServer ircServer) {
        this.user = user;
        this.ircServer = ircServer;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(user.getInput()));

            String msg;
            while ((msg = in.readLine()) != null) {

                ParsedMessage parsedMessage = parser.parse(msg);
                if (parsedMessage != null) {
                    System.out.println(user.getName());
                    handleMessage(parsedMessage, user);
                }
            }
            in.close();
            user.close();
        } catch (IOException ignored) {
        }


        ircServer.removeUser(user);
    }


    //TODO: Implement: Get users, get channels, message of the day and responses to client
    private void handleMessage(ParsedMessage msg, User user) {
        ChannelHandler ch = ircServer.getChannelHandler();

        switch (msg.getCommand()) {
            //Sends message to all users within given channels
            case "PRIVMSG":
                //Ignore message if no body
                if (msg.getTrailing() != null) {
                    for (String target : msg.getParams()) {
                        if (ch.channelExists(target)) {
                            Channel channel = ch.getChannel(target);
                            if (msg.getTarget() == null) {
                                channel.broadcast("1" + "§" + channel.getName() + "§" + user.getName() + "§" + msg.getTrailing().replaceAll("§", "_"));
                            } else {
                                channel.broadcast("1" + "§" + channel.getName() + "§" + msg.getTarget() + "§" + msg.getTrailing().replaceAll("§", "_"));
                            }
                            //Send message to specific user if no channel available
                        } else if (ircServer.userExists(target) && !target.equalsIgnoreCase("guest")) {
                            ircServer.getUserByName(target).broadcastMessage("1" + "§" + user.getName() + "§" + msg.getTrailing().replaceAll("§", "_"));
                        }
                    }
                }
                break;
            //User to join channel
            //Create new channel if no channel already exists
            //Channel has to start with #
            case "JOIN":
                for (String param : msg.getParams()) {
                    if (param.startsWith("#")) {
                        if (ch.channelExists(param)) {
                            ch.addUserToChannel(user, param);
                        } else {
                            ch.addChannels(param);
                            ch.addUserToChannel(user, param);
                        }
                    }else{
                        //TODO: error message if invalid channel name format
                    }
                }
                break;

                //User leave channel
            case "PART":
                for(String param : msg.getParams()){
                    if(param.startsWith("#")){
                        ch.getChannel(param).remove(user);
                        //TODO: reply on success
                    }
                }


                break;
            case "NICK":
                if (!msg.getParams().isEmpty()){
                    String newName = msg.getParams().getFirst();

                    //TODO: Add more robust string checking
                    if (!newName.startsWith("#") && !newName.equalsIgnoreCase("guest")){
                        user.setName(newName);
                        //TODO: Add response code on success
                    }else{
                        //TODO: error response to client if failed
                    }
                }
                System.out.println("NICK");
                break;
            default:
                System.out.println("Default");
        }
    }

}
