public class ResponseBuilder {


    private final String delimiter = "§";


    public ResponseBuilder(){

    }


    public String build (int code, String[] params){
       return build(code, params, null);
    }
    public String build(int code, String[] params, String trailing){
        StringBuilder sb = new StringBuilder();
        sb.append(code).append(delimiter);

        for (String param : params){
            sb.append(param).append(delimiter);
        }

        if(trailing != null){
            if (!trailing.startsWith(":")){
                trailing = ":" + trailing;
            }
            sb.append(trailing);
        }

        sb.append("\r\n");
        return sb.toString();
    }

}
