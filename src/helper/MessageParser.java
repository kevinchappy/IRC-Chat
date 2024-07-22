package helper;

import java.util.ArrayList;
import java.util.Arrays;

public final class MessageParser {

    public static final String delimiter = " ";


    /**
     * Parses message following the format:
     * :<prefix> <command> <parameters>
     * Prefix is optional
     * Any parameters after a ':' is considered trailing and joined in one string. This is usually the actual message
     *
     * @param rawMessage Raw message to be parsed
     */
    public static ParsedMessage parse(String rawMessage) {
        String prefix = null;
        String command;
        ArrayList<String> params = new ArrayList<>();
        String trailing = null;
        int currentParameterI;


        //Valid message has to end with \r\n
        if (rawMessage.endsWith("\\r\\n")) {
            rawMessage = rawMessage.substring(0, rawMessage.length() - 4);
        } else {
            return null;
        }

        String[] array = rawMessage.split(delimiter);

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
                trailing = (String.join(delimiter, Arrays.copyOfRange(array, i, array.length)));
                break;
            }
            params.add(array[i]);
        }


        command = command.toUpperCase();


        return new ParsedMessage(prefix, command, params, trailing);
    }
}
