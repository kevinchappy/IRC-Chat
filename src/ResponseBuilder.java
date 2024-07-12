import java.util.StringJoiner;

public class ResponseBuilder {


    public static final char DELIMITER = ' ';


    public ResponseBuilder() {

    }


    public String build(int code) {
        return build(code, null, null);
    }

    public String build(int code, String[] params) {
        return build(code, params, null);
    }

    public String build(int code, String[] params, String trailing) {
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

        if(sb.charAt(sb.length() - 1) == DELIMITER){
            sb.setLength(sb.length() - 1);
        }

        sb.append("\r\n");
        return sb.toString();
    }
}
