package helper;

public final class MessageBuilder {


    public static final char DELIMITER = ' ';

    public static String build(String code) {
        return build(code, null, null);
    }

    public static String build(String code, String[] params) {
        return build(code, params, null);
    }

    public static String build(String code, String trailing){
        return build(code,null, trailing);
    }

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
