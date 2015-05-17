package hxws.generator.generation;

/**
 * Created by hxwstogether
 */
public class RequestVo {
    private String name;
    private String methodType;
    private String url;
    private String[] headers;
    private String[] params;

    public RequestVo(String name,String methodType, String url, String[] params, String[] headers) {
        this.name = name;
        this.methodType = methodType;
        this.url = url;
        this.params = params;
        this.headers = headers;
    }

    public String getName() {
        return name;
    }

    public String getMethodType() {
        return methodType;
    }

    public String getUrl() {
        return url;
    }

    public String[] getHeaders() {
        return headers;
    }

    public String[] getParams() {
        return params;
    }
}
