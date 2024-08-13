package Server;

import Helpers.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Threaded class that handles connection to client.
 * Accepts messages from client and handles them accordingly.
 */
public class ClientHandler implements Runnable {
    private final User user;
    private final UserManager userManager;
    private final ChannelManager channelManager;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private boolean isAlive = true;

    /**
     * Instantiates new ClientHandler.
     *
     * @param user           User handled to be handled
     * @param userManager    Object that handles all users connected to server
     * @param channelManager Object that handles all channel on server
     */
    public ClientHandler(User user, UserManager userManager, ChannelManager channelManager) {
        this.user = user;
        this.userManager = userManager;
        this.channelManager = channelManager;
    }


    /**
     * Main loop that handles user connection and accepting messages..
     * Does not accept messages if user has not changed their username.
     * Handles closing connection if user disconnects.
     */
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(user.getInput()));
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.JOINED_SERVER, new String[]{user.getName()}, "Welcome to the server!"));

            String msg;
            while ((msg = in.readLine()) != null && isAlive) {
                ParsedMessage parsedMessage = MessageParser.parse(msg);
                if (parsedMessage != null) {
                    if (!user.getName().equalsIgnoreCase("guest") ||
                            parsedMessage.command().equalsIgnoreCase(MessageCodes.NICKNAME)) {
                        handleMessage(parsedMessage, user);
                    } else {
                        user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_NO_USER_NAME, "Please provide a valid username before any other commands"));
                    }
                }
            }
            in.close();
            user.close();
            handleExit(user);

        } catch (IOException e) {
            isAlive = false;
            handleExit(user);
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
            case MessageCodes.MESSAGE:
                handleSendMessage(msg, user);
                break;

            case MessageCodes.JOIN:
                handleJoin(msg, user);
                break;

            case MessageCodes.PART:
                handlePart(msg, user);
                break;

            case MessageCodes.NICKNAME:
                handleNickname(msg, user);
                break;

            case MessageCodes.NAMES:
                handleNames(user);
                break;

            case MessageCodes.CHANNELS:
                handleChannel(user);
                break;

            case MessageCodes.ESTABLISH_PRIVATE_CHAT:
                handleEstablishPrivateChat(msg, user);
                break;

            case MessageCodes.EXIT:
                isAlive = false;
                break;
            default:
                user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_INVALID_MESSAGE, "Invalid message."));
        }
    }

    /**
     * Handles establish private chat between two users.
     * Checks if user that requester wants to private message exists and sends back appropriate response
     *
     * @param msg  The message that contains request information
     * @param user The user that the request originates from
     */
    private void handleEstablishPrivateChat(ParsedMessage msg, User user) {
        String target = msg.params().getFirst();

        if (userManager.userExists(target) && !target.equals(user.getName())) {
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.ESTABLISH_PRIVATE_RES, new String[]{target}));
        } else {
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_NO_SUCH_USER, target + " does not exist."));
        }
    }

    /**
     * Handles sending list of all usernames to requesting user.
     * Sends list of all usernames or error message if no names are available
     *
     * @param user The user that the request originates from
     */
    private void handleNames(User user) {
        ArrayList<String> names = userManager.getAllUserNames();

        if (names.isEmpty()) {
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_NO_AVAILABLE_NAMES, "No available names."));
        } else {
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.NAME_LIST, names.toArray(new String[0])));
        }
    }

    /**
     * Handles sending list of all channel names to requesting user.
     * Sends list of all channel names or error message if no names are available.
     *
     * @param user The user that the request originates from
     */
    private void handleChannel(User user) {
        user.broadcastMessage(MessageBuilder.build(ResponseCodes.CHANNEL_NAMES, channelManager.getAllChannelNames()));
    }

    /**
     * Handles changing nickname for user.
     * Checks that no other user has name and the new name adheres to name format
     * Broadcasts name change to all users.
     *
     * @param msg  The message containing users new name
     * @param user The user that the request originates from
     */
    private void handleNickname(ParsedMessage msg, User user) {
        if (!msg.params().isEmpty()) {
            String oldName = user.getName();
            user.lock();

            String newName = msg.params().getFirst();
            if (newName.length() >= 3 && newName.length() <= 12 && !newName.contains("#") &&
                    !newName.contains(":") && !newName.contains(" ") && !newName.equalsIgnoreCase("guest") &&
                    userManager.getUserByName(newName) == null) {

                user.setName(newName);

                userManager.broadCastToAllUsers(MessageBuilder.build(ResponseCodes.CHANGED_NAME, new String[]{oldName, newName}));

                user.broadcastMessage(MessageBuilder.build(ResponseCodes.NAME_SUCCESS, new String[]{newName}));

            } else {
                user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_INVALID_NAME, "Invalid name."));
            }
            user.unlock();
        }
    }

    /**
     * Handles user leaving channel
     * Deletes channel if no users are left.
     * Message is broadcast to all users in channel if user successfully leaves channel
     *
     * @param msg  The message containing channel name parameter
     * @param user The user the message originates from
     */
    private void handlePart(ParsedMessage msg, User user) {
        String channelName = msg.params().getFirst();
        Channel channel = channelManager.getChannel(channelName);

        if (channel != null) {
            boolean removed = channel.remove(user);
            user.removeChannel(channel);
            if (channel.isEmpty()) {
                channelManager.removeChannel(channel.getName());
            } else if (removed) {
                channel.broadcast(MessageBuilder.build(ResponseCodes.USER_LEFT_CHANNEL,
                        new String[]{channelName, user.getName()}), null);
            }

            user.broadcastMessage(MessageBuilder.build(ResponseCodes.LEFT_CHANNELS, new String[]{channelName}));

        } else {
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_NO_SUCH_CHANNEL, "No such channel."));
        }
    }

    /**
     * Handles user joining channel.
     * Validates that channel name is in accepted format.
     * Creates channel if it does not already exist
     * Broadcasts to all users in channel that new user has joined
     *
     * @param msg  The msg containing channel name parameter
     * @param user The user the request originates from
     */
    private void handleJoin(ParsedMessage msg, User user) {
        String param = msg.params().getFirst();

        if (param.length() <= 50 && param.startsWith("#") && !param.substring(1).contains("#") &&
                !param.substring(1).contains(" ")) {

            Channel channel = channelManager.getChannel(param);

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
                channelManager.addChannels(channel);
                channel.add(user);
                user.addChannel(channel);
                user.broadcastMessage(MessageBuilder.build(ResponseCodes.CREATED_CHANNEL, new String[]{channel.getName(), user.getName()}));
            }
        } else {
            user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_INVALID_CHANNEL_NAME, "Invalid channel name."));
        }

    }

    /**
     * Handles sending message from a user to all users in a channel or as private message to specific user
     * Checks if message target is a channel or user.
     * Sends error if no recipient is found
     *
     * @param msg  Message containing parameters and trailing message
     * @param user User that the message originates from
     */
    private void handleSendMessage(ParsedMessage msg, User user) {
        //Ignore message if no body
        if (msg.trailing() != null) {
            String param = msg.params().getFirst();
            User target;
            Channel channel;
            if ((channel = channelManager.getChannel(param)) != null && user.getChannels().contains(channelManager.getChannel(param))) {

                channel.broadcast(MessageBuilder.build(ResponseCodes.CHANNEL_MSG,
                        new String[]{dateFormat.format(LocalDateTime.now()), user.getName(), channel.getName()}, msg.trailing()), null);

            } else if ((target = userManager.getUserByName(param)) != null && !target.getName().equalsIgnoreCase("guest") && !target.equals(user)) {

                String time = dateFormat.format(LocalDateTime.now());
                target.broadcastMessage(MessageBuilder.build(ResponseCodes.USER_MSG, new String[]{time, user.getName()}, msg.trailing()));
                user.broadcastMessage(MessageBuilder.build(ResponseCodes.CHANNEL_MSG, new String[]{time, user.getName(), target.getName()}, msg.trailing()));

            } else {
                user.broadcastMessage(MessageBuilder.build(ResponseCodes.ERR_NO_RECIPIENT, "Recipient does not exist."));
            }
        }
    }

    /**
     * Handles user disconnecting from the chat server.
     * Removes user from all channels and the user list.
     * Broadcasts this to all users.
     *
     * @param user The user that disconnected
     */
    private void handleExit(User user) {
        userManager.removeUser(user);
        userManager.broadCastToAllUsers(MessageBuilder.build(ResponseCodes.USER_EXIT, new String[]{user.getName()}));
        System.out.println("Current users: " + userManager.size());
    }
}
