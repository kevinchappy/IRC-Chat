package Helpers;

import java.util.ArrayList;

/**
 * A record representing a parsed message. Contains the message code, parameters and trailing message.
 * @param command
 * @param params
 * @param trailing
 */
public record ParsedMessage(String command, ArrayList<String> params, String trailing) {

    @Override
    public String toString() {
        return command + ", " + params + ", " + trailing;
    }
}
