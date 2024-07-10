public enum ResponseCodes {
    MSG(1),
    ERR_NO_SUCH_USER(2),
    ERR_NO_SUCH_CHANNEL(3),
    NAME_SUCCESS(4),
    INVALID_CHANNEL_NAME(5)
    ;

    private final int code;

    ResponseCodes(int code) {
        this.code = code;
    }


    public int getCode() {
        return code;
    }

    public static ResponseCodes equals(int other) {
        for (ResponseCodes responseCode : ResponseCodes.values()) {
            if (responseCode.getCode() == other) {
                return responseCode;
            }
        }
        return null;
    }
}
