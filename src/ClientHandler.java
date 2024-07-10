import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ClientHandler implements Runnable {
    private final User user;
    private final IRCServer ircServer;
    private final MessageParser parser = new MessageParser();
    private final ResponseBuilder responseBuilder = new ResponseBuilder();

    ClientHandler(User user, IRCServer ircServer) {
        this.user = user;
        this.ircServer = ircServer;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(user.getInput()));

            String msg;
            while ((msg = in.readLine()) != null && ircServer.isAlive()) {
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


    /**
     * Handles messages sent by users.
     * Makes use of MessageCodes to delineate between different requests.
     * Typically a response with a ResponseCode is sent back to user depending on the type of
     * request and if it was successful
     *
     *
     * @param msg  Message to be handled
     * @param user The user that sent message
     */
//TODO: Implement: Get users, get channels, message of the day and responses to client
    private void handleMessage(ParsedMessage msg, User user) {
        ChannelHandler ch = ircServer.getChannelHandler();

        switch (msg.getCommand()) {
            //Sends message to all users within given channels
            case MessageCodes.MESSAGE:
                //Ignore message if no body
                if (msg.getTrailing() != null) {
                    for (String target : msg.getParams()) {
                        if (ch.channelExists(target)) {
                            Channel channel = ch.getChannel(target);
                            if (msg.getTarget() == null) {
                                channel.broadcast(responseBuilder.build(ResponseCodes.MSG, new String[]{channel.getName(), user.getName()}, msg.getTrailing()), null);
                            } else {
                                channel.broadcast(responseBuilder.build(ResponseCodes.MSG, new String[]{channel.getName(), msg.getTarget()}, msg.getTrailing()), null);

                            }
                            //Send message to specific user if no channel available
                        } else if (ircServer.userExists(target) && !target.equalsIgnoreCase("guest")) {
                            ircServer.getUserByName(target).broadcastMessage(responseBuilder.build(ResponseCodes.MSG, new String[]{user.getName()}, msg.getTrailing()));
                        }else {
                            //TODO: error if no viable user
                        }
                    }
                }
                break;
            //User to join channel
            //Create new channel if no channel already exists
            //Channel has to start with #
            case MessageCodes.JOIN:
                for (String param : msg.getParams()) {
                    if (param.startsWith("#") && !param.contains("#") && param.length() < 12) {
                        if (ch.channelExists(param)) {
                            Channel channel = ch.getChannel(param);
                            channel.add(user);
                            user.addChannel(channel);
                        } else {
                            Channel channel = new Channel(param);
                            ch.addChannels(channel);
                            channel.add(user);
                            user.addChannel(channel);
                        }
                    }else{
                        user.broadcastMessage(responseBuilder.build(ResponseCodes.INVALID_CHANNEL_NAME, new String[]{}));
                        //TODO: error message if invalid channel name format
                    }
                }
                break;

                //User leave channel
            case MessageCodes.PART:
                for(String param : msg.getParams()){
                    ArrayList <String> leftChannels = new ArrayList<>();
                    if(param.startsWith("#")){
                        Channel channel = ch.getChannel(param);
                        if (channel != null){
                            channel.remove(user);
                            user.removeChannel(channel);
                            leftChannels.add(channel.getName());
                        }

                        if (leftChannels.isEmpty()){
                            user.broadcastMessage(responseBuilder.build(ResponseCodes.ERR_NO_SUCH_CHANNEL, new String[]{}));
                        }else{
                            user.broadcastMessage(responseBuilder.build(ResponseCodes.LEFT_CHANNELS, leftChannels.toArray(new String[0])));
                        }

                    }
                }
                break;

                //Change of user nickname
            case MessageCodes.NICKNAME:
                if (!msg.getParams().isEmpty()){
                    String newName = msg.getParams().getFirst();
                    if (!newName.contains("#") && !newName.contains(":") && !newName.equalsIgnoreCase("guest") && newName.length() <= 12 ){
                        user.setName(newName);
                        user.broadcastMessage(responseBuilder.build(ResponseCodes.NAME_SUCCESS, new String[]{newName}));
                        //TODO: Add response code on success
                    }else{
                        //TODO: error response to client if failed
                    }
                }
                break;

                //Sends names of all users that are on the same channels as sender
            //TODO: fix this case
            case "NAMES":
                Set<String> set = new HashSet<>();

                for (Channel channel : user.getChannels()){
                    set.addAll(channel.getUserNames());
                }
                Iterator<String> iter = set.iterator();
                StringBuilder sb = new StringBuilder();
                while(iter.hasNext()){
                    sb.append(iter.next());
                }


            default:
                System.out.println("Default");
        }
    }

}
