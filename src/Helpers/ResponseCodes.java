package Helpers;

/**
 * Defines constant values of response codes for response messages from the server to users.
 */
public class ResponseCodes {
    public static final String JOINED_SERVER = "100";
    public static final String CHANNEL_MSG = "101";
    public static final String USER_MSG = "102";
    public static final String NAME_SUCCESS = "103";
    public static final String LEFT_CHANNELS = "104";
    public static final String USER_JOINED_CHANNEL = "105";
    public static final String CREATED_CHANNEL = "106";
    public static final String USER_LEFT_CHANNEL = "107";
    public static final String JOINED_CHANNEL = "108";
    public static final String CHANGED_NAME = "109";
    public static final String NAME_LIST = "110";
    public static final String CHANNEL_NAMES = "111";
    public static final String ESTABLISH_PRIVATE_RES = "112";
    public static final String USER_EXIT = "113";

    public static final String ERR_NO_SUCH_USER = "200";
    public static final String ERR_NO_SUCH_CHANNEL = "201";
    public static final String ERR_INVALID_NAME = "202";
    public static final String ERR_INVALID_CHANNEL_NAME = "203";
    public static final String ERR_INVALID_MESSAGE = "204";
    public static final String ERR_NO_RECIPIENT = "205";
    public static final String ERR_NO_AVAILABLE_NAMES = "206";
    public static final String ERR_NO_USER_NAME = "207";
}
