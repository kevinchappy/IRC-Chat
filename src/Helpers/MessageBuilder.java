package Helpers;


/**
 * Static class that contains method to build messages according to the following format:
 * <command> <parameters> crlf
 * Where the command is the message code, parameters a list strings with arbitrary length and crlf marks the end of message.
 */
public final class MessageBuilder {
    public static final char DELIMITER = ' ';

    /**
     * @see MessageBuilder#build(String, String[], String)
     */
    public static String build(String code) {
        return build(code, null, null);
    }

    /**
     * @see MessageBuilder#build(String, String[], String)
     */
    public static String build(String code, String[] params) {
        return build(code, params, null);
    }

    /**
     * @see MessageBuilder#build(String, String[], String)
     */
    public static String build(String code, String trailing) {
        return build(code, null, trailing);
    }

    /**
     * Builds a formatted string that adheres to the message format.
     * Appends the message code, parameters and trailing into one string and delimits them with blank spaces.
     * Checks that the trailing part starts with a ':' and that the message ends with crlf
     *
     * @param code     The message code
     * @param params   List of message parameters
     * @param trailing Trailing parameter.
     * @return The formatted IRC message.
     */
    public static String build(String code, String[] params, String trailing) {
        StringBuilder sb = new StringBuilder();
        sb.append(code).append(DELIMITER);

        if (params != null) {
            for (String param : params) {
                sb.append(param).append(DELIMITER);
            }
        }

        if (trailing != null) {
            if (!trailing.startsWith(":")) {
                trailing = ":" + trailing;
            }
            sb.append(trailing);
        }

        if (sb.charAt(sb.length() - 1) == DELIMITER) {
            sb.setLength(sb.length() - 1);
        }

        sb.append("\\r\\n");
        return sb.toString();
    }
}
