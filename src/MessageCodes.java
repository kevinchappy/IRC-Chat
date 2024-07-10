
public enum MessageCodes {
    MESSAGE("PRIVMSG"),
    JOIN("JOIN"),
    NICKNAME("NICK"),
    PART("PART")
    ;

    private final String text;

    MessageCodes(String text) {
        this.text = text;

    }

    @Override
    public String toString() {
        return text;
    }


    public static MessageCodes equals(String other) {
        for (MessageCodes messageCode : MessageCodes.values()) {
            if (messageCode.toString().equals(other)) {
                return messageCode;
            }
        }
        return null;
    }
}

