package hxws.generator.generation;

import java.util.UUID;

/**
 * Created by hxwstogether
 */
public class RequestVo {
    private String name;
    private String methodType;
    private String requestType;
    private String url;
    private String[] headers;
    private String[] params;
    private String convertClass;
    private int ref_id;
    private String ref;
    private String uuid;

    public RequestVo(String name,String methodType,String requestType, String url, String[] params, String[] headers,String convertClass,int ref_id,String ref) {
        this.name = name;
        this.methodType = methodType;
        this.requestType = requestType;
        this.url = url;
        this.params = params;
        this.headers = headers;
        this.convertClass = convertClass;
        this.ref_id = ref_id;
        this.ref = ref;

        this.uuid = UUID.randomUUID().toString().replaceAll("-","");
    }

    public String getName() {
        return name;
    }

    public String getMethodType() {
        return methodType;
    }

    public String getRequestType() {
        return requestType;
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

    public String getConvertClass() {
        return convertClass;
    }

    public int getRef_id() {
        return ref_id;
    }

    public String getRef() {
        return ref;
    }

    public String getUuid() {
        return uuid;
    }
}
