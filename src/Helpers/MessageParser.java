package Helpers;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Static class that contains method to parse messages according to the following format:
 * <command> <parameters> crlf
 * Where the command is the message code, parameters an arbitrary list strings and crlf marks the end of message.
 */
public final class MessageParser {
    public static final String delimiter = " ";

    /**
     * Parses raw message string into a ParsedMessage object.
     * Checks that the message ends with `\r\n` and splits it with blank space delimiter.
     * Extracts the command, parameters, and trailing message part if present.
     *
     * @param rawMessage Raw message to be parsed.
     * @return A ParsedMessage object containing the command, parameters and trailing data.
     * Null is returned if message is invalid.
     */
    public static ParsedMessage parse(String rawMessage) {
        //String prefix = null;
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


        command = array[0];
        currentParameterI = 1;

        for (int i = currentParameterI; i < array.length; i++) {
            if (array[i].startsWith(":")) {
                trailing = (String.join(delimiter, Arrays.copyOfRange(array, i, array.length)));
                break;
            }
            params.add(array[i]);
        }


        command = command.toUpperCase();


        return new ParsedMessage(command, params, trailing);
    }
}
