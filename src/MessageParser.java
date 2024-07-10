import java.util.ArrayList;
import java.util.Arrays;

public class MessageParser {


    /*public static void main(String[] args) {
        MessageParser parser = new MessageParser();


        System.out.println(parser.parse(":Angel PRIVMSG Wiz :Hello are you receiving this message?\r\n"));

    }*/


    public MessageParser() {
    }


    /**
     * Parses message following the IRC format:
     * :<prefix> <command> <parameters>
     * Prefix is optional
     * Any parameters after a ':' is considered trailing and joined in one string. This is usually the actual message
     *
     * @param rawMessage Raw message to be parsed
     */
    public ParsedMessage parse(String rawMessage) {
        String prefix = null;
        String command;
        ArrayList<String> params = new ArrayList<>();
        String trailing = null;
        int currentParameterI;
        System.out.println(rawMessage);

        //Valid message has to end with \r\n
        if (rawMessage.endsWith("\\r\\n")) {
            rawMessage = rawMessage.substring(0, rawMessage.length() - 4);
        } else {
            return null;
        }

        System.out.println(rawMessage);
        String[] array = rawMessage.split(" ");

        if (array[0].startsWith(":")) {
            prefix = array[0];
            command = array[1];
            currentParameterI = 2;
        } else {
            command = array[0];
            currentParameterI = 1;
        }
        for (int i = currentParameterI; i < array.length; i++) {
            if (array[i].startsWith(":")) {
                trailing = (String.join(" ", Arrays.copyOfRange(array, i, array.length)));
                break;
            }
            params.add(array[i]);
        }


        command = command.toUpperCase();


        return new ParsedMessage(prefix, command, params, trailing);
    }
}
