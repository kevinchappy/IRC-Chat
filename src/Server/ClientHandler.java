package Server;

import helper.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ClientHandler implements Runnable {
    private final User user;
    private final IRCServer ircServer;
    private final ChannelHandler ch;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private boolean isAlive = true;
    ClientHandler(User user, IRCServer ircServer) {
        this.user = user;
        this.ircServer = ircServer;
        this.ch = ircServer.getChannelHandler();
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(user.getInput()));

            ArrayList<String> userNames = new ArrayList<>();
            for (User user : ircServer.getUsers()){
                userNames.add(user.getName());
            }
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.JOINED_SERVER, userNames.toArray(new String[0]), "Welcome to the server!"));


            String msg;
            while ((msg = in.readLine()) != null && isAlive) {
                ParsedMessage parsedMessage = MessageParser.parse(msg);
                if (parsedMessage != null) {
                    if (!user.getName().equalsIgnoreCase("guest") ||
                            parsedMessage.command().equalsIgnoreCase(MessageCodes.NICKNAME)) {
                        handleMessage(parsedMessage, user);
                    } else {
                        user.broadcastMessage(MessageBuilder.build(ResponseCodes.NO_USER_NAME));
                    }
                }
            }
            in.close();
            user.close();
            handleExit(user);
            ircServer.removeUser(user);

        } catch (IOException e) {
            handleExit(user);
            ircServer.removeUser(user);

        }
    }


    /**
     * Handles messages sent by users.
     * Makes use of helper.MessageCodes to delineate between different requests.
     * Typically, a response with a ResponseCode is sent back to user depending on the type of
     * request and if it was successful
     *
     * @param msg  Message to be handled
     * @param user The user that sent the message
     */
//TODO: Implement: Get users, get channels, message of the day and responses to client
    private void handleMessage(ParsedMessage msg, User user) {

        switch (msg.command()) {
            //Sends message to all users within given channels
            case MessageCodes.MESSAGE:
                handleSendMessage(msg, user);
                break;
            //Server.User to join channel
            //Create new channel if no channel already exists
            //Server.Channel has to start with #
            case MessageCodes.JOIN:
                handleJoin(msg, user);
                break;

            //Server.User leave channel
            case MessageCodes.PART:
                handlePart(msg, user);
                break;

            //Change of user nickname
            case MessageCodes.NICKNAME:
                handleNickname(msg, user);
                break;

            //Sends names of all users that are on the same channels as sender
            case MessageCodes.NAMES:
                handleNames(user);
                break;
            case MessageCodes.CHANNELS:
                handleChannel(user);
                break;
            case MessageCodes.EXIT:
                isAlive = false;
                break;
            default:
                user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_INVALID_MESSAGE));
        }
    }

    private void handleNames(User user) {
        ArrayList<String> names = new ArrayList<>();

        for (Channel channel : user.getChannels()) {
            names.add(channel.getName());
            names.addAll(channel.getUserNames());
        }

        if (!names.isEmpty()) {
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.NAME_LIST, names.toArray(new String[0])));
        } else {
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_NO_AVAILABLE_NAMES));
        }
    }

    private void handleChannel(User user) {
        ArrayList<String> names = new ArrayList<>();

        Iterator<String> iter = ch.getKeyIterator();

        while (iter.hasNext()) {
            String name = iter.next();
            names.add(name);
        }

        user.broadcastMessage(MessageBuilder.build(ResponseCodes.CHANNEL_NAMES, names.toArray(new String[0])));

    }

    private void handleNickname(ParsedMessage msg, User user) {
        if (!msg.params().isEmpty()) {
            String oldName = user.getName();
            user.lock();

            String newName = msg.params().getFirst();
            if (newName.length() >= 3 && newName.length() <= 12 && !newName.contains("#") &&
                    !newName.contains(":") && !newName.contains(" ") && !newName.equalsIgnoreCase("guest") &&
                    ircServer.getUserByName(newName) == null) {

                user.setName(newName);
                for (User sendTo : ircServer.getUsers()){

                    sendTo.broadcastMessage(MessageBuilder.build(ResponseCodes.CHANGED_NAME, new String[]{oldName, newName}));

                }

                user.broadcastMessage(MessageBuilder.build(ResponseCodes.NAME_SUCCESS, new String[]{newName}));

            } else {
                user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_INVALID_NAME));
            }
            user.unlock();
        }
    }

    private void handlePart(ParsedMessage msg, User user) {

        String channelName = msg.params().getFirst();
        Channel channel = ch.getChannel(channelName);

        if (channel != null) {
            channel.remove(user);
            user.removeChannel(channel);
            if (channel.isEmpty()) {
                ch.removeChannel(channel.getName());
            } else {
                channel.broadcast(MessageBuilder.build(ResponseCodes.USER_LEFT_CHANNEL,
                        new String[]{channelName, user.getName()}), null);
            }

            user.broadcastMessage(MessageBuilder.build(ResponseCodes.LEFT_CHANNELS, new String[]{channelName}));

        } else {
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_NO_SUCH_CHANNEL));
        }


    }

    private void handleJoin(ParsedMessage msg, User user) {
        String param = msg.params().getFirst();

        if (param.length() <= 50 && param.startsWith("#") && !param.substring(1).contains("#") &&
                !param.substring(1).contains(" ")) {

            Channel channel = ch.getChannel(param);

            if (channel != null && !channel.getUserNames().contains(user.getName())) {

                channel.add(user);
                user.addChannel(channel);
                channel.broadcast(MessageBuilder.build(ResponseCodes.USER_JOINED_CHANNEL,
                        new String[]{channel.getName(), user.getName()}, null), user.getName());

                ArrayList<String> joinedChannelMessage = new ArrayList<>();
                joinedChannelMessage.add(channel.getName());
                joinedChannelMessage.addAll(channel.getUserNames());
                user.broadcastMessage(MessageBuilder.build(ResponseCodes.JOINED_CHANNEL, joinedChannelMessage.toArray(new String[0])));
            } else if (channel == null) {
                channel = new Channel(param);
                ch.addChannels(channel);
                channel.add(user);
                user.addChannel(channel);
                user.broadcastMessage(MessageBuilder.build(ResponseCodes.CREATED_CHANNEL, new String[]{channel.getName(), user.getName()}));
            }
        } else {
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.INVALID_CHANNEL_NAME));
        }

    }

    /**
     * Helper method to handle sending message to all users in a channel or to a specific user.
     *
     * @param msg
     * @param user
     */
    private void handleSendMessage(ParsedMessage msg, User user) {
        //Ignore message if no body
        if (msg.trailing() != null) {
            String param = msg.params().getFirst();
            User target;
            Channel channel;
            if ((channel = ch.getChannel(param)) != null && user.getChannels().contains(ch.getChannel(param))) {

                channel.broadcast(MessageBuilder.build(ResponseCodes.CHANNEL_MSG,
                        new String[]{dateFormat.format(LocalDateTime.now()), user.getName(), channel.getName()}, msg.trailing()), null);
                //user.broadcastMessage(responseBuilder.build(ResponseCodes.MESSAGE_SENT));

            } else if ((target = ircServer.getUserByName(param)) != null && !target.getName().equalsIgnoreCase("guest")) {

                target.broadcastMessage(MessageBuilder.build(ResponseCodes.USER_MSG,
                        new String[]{dateFormat.format(LocalDateTime.now()), user.getName()}, msg.trailing()));
                user.broadcastMessage(MessageBuilder.build(ResponseCodes.MESSAGE_SENT));

            } else {
                user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_NO_RECIPIENT));
            }
        }
    }

    private void handleExit(User user){
        for (Channel channel : user.getChannels()){
            channel.broadcast(MessageBuilder.build(ResponseCodes.USER_EXIT, new String[]{user.getName()}), user.getName());
        }
    }

}
