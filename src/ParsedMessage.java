import java.util.ArrayList;

public record ParsedMessage(String target, String command, ArrayList<String> params, String trailing) {

    @Override
    public String toString() {
        return target + ", " + command + ", " + params + ", " + trailing;
    }
}
