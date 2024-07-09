import java.util.ArrayList;

public class ParsedMessage {
    private final String target;
    private final String command;
    private final ArrayList<String> params;
    private final String trailing;

    public ParsedMessage(String target, String command, ArrayList<String> params, String trailing){
        this.target = target;
        this.command = command;
        this.params = params;
        this.trailing = trailing;
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public String getCommand() {
        return command;
    }

    public String getTarget() {
        return target;
    }

    public String getTrailing() {
        return trailing;
    }

    @Override
    public String toString() {
        return target + ", " + command + ", " + params + ", " + trailing;
    }
}
