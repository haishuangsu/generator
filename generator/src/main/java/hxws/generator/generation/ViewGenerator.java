package hxws.generator.generation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import hxws.generator.annotations.lifecycle.afterInject;
import hxws.generator.annotations.lifecycle.onCreate;
import hxws.generator.annotations.lifecycle.onDestroy;
import hxws.generator.annotations.lifecycle.onPause;
import hxws.generator.annotations.lifecycle.onResume;
import hxws.generator.annotations.lifecycle.onStart;
import hxws.generator.annotations.lifecycle.onStop;
import hxws.generator.annotations.onClick;
import hxws.generator.annotations.onItemClick;
import hxws.generator.annotations.onItemLongClick;
import hxws.generator.annotations.onLongClick;

/**
 * Created by suhaishuang
 */
public class ViewGenerator {

    private String packageName;
    private String className;
    private int layoutId;
    private Listener after;

    private Map<Integer,ViewBindListener> viewMap = new HashMap<>();
    private Set<Listener> lifeListeners = new HashSet<>();
    private Queue<RequestVo> requestVos = new LinkedList<>();

    public ViewGenerator(String packageName,String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public void setAfter(Listener after) {
        this.after = after;
    }

    public void addViewAttr(int id,String name,String type){
        getOrGreate(id, name, type);
    }

    public void addListener(int id,Listener listener){
        getOrGreate(id).addListener(listener);
    }

    public void addRequestVo(RequestVo vo){
        requestVos.add(vo);
    }

    private ViewBindListener getOrGreate(int id){
       return getOrGreate(id,null,null);
    }

    private ViewBindListener getOrGreate(int id,String name,String type){
        ViewBindListener viewBindListener = viewMap.get(id);
        if(viewBindListener == null){
            viewBindListener = new ViewBindListener(id,name,type);
            viewMap.put(id,viewBindListener);
        }
        return viewBindListener;
    }

    public void addLifeListenner(Listener listener){
        lifeListeners.add(listener);
    }

    public Map<Integer, ViewBindListener> getViewMap() {
        return viewMap;
    }

    public Queue<RequestVo> getRequestVos() {
        return requestVos;
    }

    public String getPackageClass(){
        return packageName+"."+className+"_";
    }


    public String generateSource(String type){
        StringBuilder builder = new StringBuilder();
        builder.append("package " + packageName + ";\n \n");
        builder.append("import android.os.Bundle;\n");
        builder.append("import android.view.View;\n");
        builder.append("import com.android.volley.Request;\n");
        builder.append("import com.android.volley.Response;\n");
        builder.append("import com.android.volley.toolbox.JsonRequest;\n");
        builder.append("import com.android.volley.VolleyError;\n");
        builder.append("import com.android.volley.toolbox.StringRequest;\n");
        builder.append("import com.android.volley.toolbox.ImageRequest;\n");
        builder.append("import com.android.volley.AuthFailureError;\n");
        builder.append("import org.json.JSONException;\n");
        builder.append("import org.json.JSONObject;\n");
        builder.append("import java.io.UnsupportedEncodingException;\n");
        builder.append("import com.android.volley.ParseError;\n");
        builder.append("import com.android.volley.NetworkResponse;\n");
        builder.append("import com.android.volley.toolbox.HttpHeaderParser;\n");
        builder.append("import android.graphics.Bitmap;\n");
        builder.append("import com.android.volley.Response.ErrorListener;\n");
        builder.append("import com.alibaba.fastjson.JSON;\n");
        builder.append("import java.util.Map;\n");
        builder.append("import java.util.HashMap;\n");

        if("android.app.Fragment".equals(type) || "android.support.v4.app.Fragment".equals(type)){
            builder.append("import android.view.LayoutInflater;\n");
            builder.append("import android.view.ViewGroup;\n");
        }
        builder.append("\n \n");
        builder.append("public class " + className + "_" + " extends " + className);
        builder.append("{ \n");
        builder.append("\t@Override\n");
        if("android.app.Fragment".equals(type) || "android.support.v4.app.Fragment".equals(type)){
            builder.append("\tpublic View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");
            builder.append("{ \n");
            builder.append("\t//onCreate\n");
            if(layoutId != 0){
                builder.append("\tView view = inflater.inflate("+layoutId+",null);\n");
            }
            builder.append("\tinject(view);\n");
            builder.append("\treturn view;\n");
            builder.append("\t}\n");
            builder.append("\tvoid inject(View view){\n");
            for(ViewBindListener viewBindListener : getViewMap().values()){
                findByid(builder,viewBindListener,false);
                bindListener(builder, viewBindListener.getListeners(), viewBindListener.getId(),viewBindListener.getName());
            }
            afterInject(builder);
            for(Listener lifeListener :lifeListeners){
                lifeCycle(builder,lifeListener,false);
            }
        }else if("android.app.Activity".equals(type) || "android.support.v4.app.FragmentActivity".equals(type)){
            builder.append("\tprotected void onCreate(Bundle savedInstanceState) ");
            builder.append("{ \n");
            builder.append("\tsuper.onCreate(savedInstanceState);\n");
            builder.append("\t//onCreate\n");
            if(layoutId != 0){
                builder.append("\tsetContentView("+layoutId+"); \n");
            }
            builder.append("\tinject();\n");
            builder.append("\n  } \n");
            builder.append("\tvoid inject(){\n");
            for(ViewBindListener viewBindListener : getViewMap().values()){
                findByid(builder,viewBindListener,true);
                bindListener(builder, viewBindListener.getListeners(), viewBindListener.getId(),viewBindListener.getName());
            }
            afterInject(builder);
            for(Listener lifeListener :lifeListeners){
                lifeCycle(builder,lifeListener,true);
            }
        }
        builder.append("\n}");
        dealRequest(builder);
        return builder.toString();
    }

    private void findByid(StringBuilder builder,ViewBindListener viewBindListener,boolean isAct){
        if(isAct){
            builder.append("\t"+viewBindListener.getName()+"="+"("+viewBindListener.getType()+")findViewById("+viewBindListener.getId()+");\n");
        }else{
            builder.append("\t"+viewBindListener.getName()+"="+"("+viewBindListener.getType()+")view.findViewById("+viewBindListener.getId()+");\n");
        }
    }

    private void bindListener(StringBuilder builder,Set<Listener> listeners,int id,String name){
        for(Listener listener : listeners){
            if(listener.getAnnotationClass().equals(onClick.class)){
                builder.append("\t"+name+".setOnClickListener(new View.OnClickListener() {\n");
                builder.append("\t\t@Override\n");
                builder.append("\t\tpublic void onClick(View v) { \n \t\t//"+id+"-"+
                                        listener.getAnnotationClass().getSimpleName()+"\n\t\t"+
                                        listener.getMethod()+"(); \n\t\t}\n    });\n");
            }else if(listener.getAnnotationClass().equals(onLongClick.class)){
                builder.append("\t"+name+".setOnLongClickListener(new View.OnLongClickListener() {\n");
                builder.append("\t\t@Override\n");
                builder.append("\t\tpublic boolean onLongClick(View v) {\n \t\t// "+id+"-"+
                                        listener.getAnnotationClass().getSimpleName()+"\n\t\t"+
                                        listener.getMethod()+"(); return true;\n\t\t}\n    });\n");
            }else if(listener.getAnnotationClass().equals(onItemClick.class)){
                builder.append("\t"+name+".setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {\n");
                builder.append("\t\t@Override\n");
                builder.append("\t\tpublic void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) { \n\t\t//"+
                                        id+"-"+listener.getAnnotationClass().getSimpleName()+"\n\t\t"+
                                        listener.getMethod()+"(parent,view,position,id); \n\t\t}\n    });\n");
            }else if(listener.getAnnotationClass().equals(onItemLongClick.class)){
                builder.append("\t"+name+"..setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {\n");
                builder.append("\t\t@Override\n");
                builder.append("\t\tpublic boolean onItemLongClick(android.widget.AdapterView<?> parent, View view, int position, long id) { \n\t\t//" +
                        id + "-" + listener.getAnnotationClass().getSimpleName() + "\n\t\t" +
                        listener.getMethod() + "(parent,view,position,id); return true;\n\t\t}\n    });\n");
            }
        }
    }

    private void afterInject(StringBuilder builder){
        builder.append("\tafterInject();\n"+
                       "\t}\n");
        builder.append("\tvoid afterInject(){\n"+
                       "\t//"+ afterInject.class.getSimpleName()+"\n"+
                       "\n\t} \n");
        if(after!=null){
            String target = "//"+afterInject.class.getSimpleName();
            int index = builder.indexOf(target);
            builder.insert(index, "\n\t" + after.getMethod() + "();\n");
        }
    }

    private void lifeCycle(StringBuilder builder,Listener lifeListener,boolean isAct){
        String key = isAct ? "protected":"public";
        builder.append("\t@Override\n"+
                       "\t"+key+" void onStart() { \n"+
                       "\tsuper.onStart();\n"+
                       "\t//"+onStart.class.getSimpleName()+"\n"+
                       "\t} \n");
        builder.append("\t@Override\n"+
                       "\t"+key+" void onPause() { \n"+
                       "\tsuper.onPause();\n"+
                       "\t//"+onPause.class.getSimpleName()+"\n"+
                       "\t} \n");
        builder.append("\t@Override\n"+
                       "\t"+key+" void onResume() { \n"+
                       "\tsuper.onResume();\n"+
                       "\t//"+onResume.class.getSimpleName()+"\n"+
                       "\t} \n");
        builder.append("\t@Override\n"+
                       "\t"+key+" void onStop() { \n"+
                       "\tsuper.onStop();\n"+
                       "\t//"+onStop.class.getSimpleName()+"\n"+
                       "\t} \n");
        builder.append("\t@Override\n"+
                       "\t"+key+" void onDestroy() { \n"+
                       "\tsuper.onDestroy();\n"+
                       "\t//" + onDestroy.class.getSimpleName() + "\n"+
                       "\t} \n");
        String target = "";
        if(lifeListener.getAnnotationClass().equals(onStart.class)){
            target = "//"+ onStart.class.getSimpleName();
        }else if(lifeListener.getAnnotationClass().equals(onPause.class)){
            target = "//"+ onPause.class.getSimpleName();
        }else if(lifeListener.getAnnotationClass().equals(onResume.class)){
            target = "//"+ onResume.class.getSimpleName();
        }else if(lifeListener.getAnnotationClass().equals(onStop.class)){
            target = "//"+ onStop.class.getSimpleName();
        }else if(lifeListener.getAnnotationClass().equals(onDestroy.class)){
            target = "//"+ onDestroy.class.getSimpleName();
        }else  if(lifeListener.getAnnotationClass().equals(onCreate.class)){
            target = "//"+ onCreate.class.getSimpleName();
        }
        int index = builder.indexOf(target);
        builder.insert(index, "\n\t" + lifeListener.getMethod() + "();\n");
    }

    private void dealRequest(StringBuilder builder){
        for(RequestVo vo : getRequestVos()){
            String replace = "";
            int index = 0;
            if(vo.getRef_id() !=0 && !vo.getRef().equals("")){
                String target = "//"+vo.getRef_id()+"-"+vo.getRef();
                index = builder.indexOf(target);
            }else if(vo.getRef_id() == 0 && !vo.getRef().equals("")){
                String target = "//"+vo.getRef();
                index = builder.indexOf(target);
            }
            switch (vo.getRequestType()){
                case "String":
                    replace = RequestTemplete.STRING.output(vo);
                    break;
                case "Json":
                    replace = RequestTemplete.JSON.output(vo);
                    break;
                case "Image":
                    replace = RequestTemplete.IMAGE.output(vo);
                    break;
                default:
                    break;
            }
            builder.insert(index,replace);//+"//index:"+index+",method:"+vo.getName()
        }
    }

    enum RequestTemplete {
        STRING{
            @Override
            public String output(RequestVo vo){
                String[] headers =  vo.getHeaders();
                String[] params = vo.getParams();
                String replace = "\n\tStringRequest req"+vo.getUuid()+" = new StringRequest(";
                switch (vo.getMethodType()){
                    case "Post":
                          replace+= "Request.Method.POST,\n";
                        break;
                    case "Get":
                        replace+= "Request.Method.GET,\n";
                        break;
                    case "Put":
                        replace+= "Request.Method.PUT,\n";
                        break;
                    case "Delete":
                        replace+= "Request.Method.DELETE,\n";
                        break;
                    default:
                        break;
                }
                        replace +=
                        "\t\t"+"\""+vo.getUrl()+"\""+", new Response.Listener<String>() {\n" +
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic void onResponse(String result) {\n";
                if(!"".equals(vo.getConvertClass())) {
                    replace +=
                            "\t\t\t" + vo.getConvertClass() + " vo=" + "JSON.parseObject(result," + vo.getConvertClass() + ".class);\n"+
                            "\t\t\t" + vo.getName() + "(vo,null);\n";}else{
                    replace +=
                            "\t\t\t" + vo.getName() + "(result,null);\n"; }
                        replace +=
                        "\t\t\t}\n" +
                        "\t\t}, new ErrorListener() {\n" +
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic void onErrorResponse(VolleyError error) {\n" +
                        "\t\t\t" +vo.getName()+"(null,error);\n"+
                        "\t\t\t}\n" +
                        "\t\t}){\n"+
                        "\t\t\t@Override\n" +
                                "\t\t\tpublic Map<String, String> getHeaders()\n" +
                                "\t\t\tthrows AuthFailureError {\n" +
                                "\t\t\tHashMap< String, String> map = new HashMap<String, String>();\n";
                                for(String header : headers){
                                    String [] head = header.split(":");
                                    replace += "\t\t\tmap.put("+"\""+head[0]+"\",\""+head[1]+"\");\n";
                                }
                                replace+=
                                "\t\t\treturn map;\n" +
                                "\t\t\t}\n"+
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic Map<String, String> getParams()\n" +
                        "\t\t\tthrows AuthFailureError {\n" +
                        "\t\t\tHashMap< String, String> map = new HashMap<String, String>();\n";
                        for(String param : params){
                            String [] par = param.split(":");
                            replace += "\t\t\tmap.put("+"\""+par[0]+"\",\""+par[1]+"\");\n";
                        }
                        replace+=
                        "\t\t\treturn map;\n" +
                        "\t\t\t}\n"+
                        "\t\t};\n"+
                        "\tqueue.add(req"+vo.getUuid()+");\n";
            return replace;
            }
        },
        JSON{
            @Override
            public String output(RequestVo vo){
                String[] headers =  vo.getHeaders();
                String[] params = vo.getParams();
                String replace = "\n\tJsonRequest req"+vo.getUuid()+" = new JsonRequest(";
                switch (vo.getMethodType()){
                    case "Post":
                        replace+= "Request.Method.POST,\n";
                        break;
                    case "Get":
                        replace+= "Request.Method.GET,\n";
                        break;
                    case "Put":
                        replace+= "Request.Method.PUT,\n";
                        break;
                    case "Delete":
                        replace+= "Request.Method.DELETE,\n";
                        break;
                    default:
                        break;
                }
                replace +=
                        "\t\t"+"\""+vo.getUrl()+"\""+
                                ",\""+"jsonString"+"\""+
                                ", new Response.Listener<String>() {\n" +
                                "\t\t\t@Override\n" +
                                "\t\t\tpublic void onResponse(String result) {\n";
                if(!"".equals(vo.getConvertClass())) {
                    replace +=
                            "\t\t\t" + vo.getConvertClass() + " vo=" + "JSON.parseObject(result," + vo.getConvertClass() + ".class);\n"+
                                    "\t\t\t" + vo.getName() + "(vo,null);\n";}else{
                    replace +=
                            "\t\t\t" + vo.getName() + "(result,null);\n"; }
                    replace +=
                        "\t\t\t}\n" +
                        "\t\t}, new ErrorListener() {\n" +
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic void onErrorResponse(VolleyError error) {\n" +
                        "\t\t\t" +vo.getName()+"(null,error);\n"+
                        "\t\t\t}\n" +
                        "\t\t}){\n"+
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic Map<String, String> getHeaders()\n" +
                        "\t\t\tthrows AuthFailureError {\n" +
                        "\t\t\tHashMap< String, String> map = new HashMap<String, String>();\n";
                        for(String header : headers){
                            String [] head = header.split(":");
                            replace += "\t\t\tmap.put("+"\""+head[0]+"\",\""+head[1]+"\");\n";
                        }
                        replace+=
                        "\t\t\treturn map;\n" +
                                "\t\t\t}\n"+
                                "\t\t\t@Override\n" +
                                "\t\t\tpublic Map<String, String> getParams()\n" +
                                "\t\t\tthrows AuthFailureError {\n" +
                                "\t\t\tHashMap< String, String> map = new HashMap<String, String>();\n";
                        for(String param : params){
                            String [] par = param.split(":");
                            replace += "\t\t\tmap.put("+"\""+par[0]+"\",\""+par[1]+"\");\n";
                        }
                        replace+=
                        "\t\t\treturn map;\n" +
                        "\t\t\t}\n"+
                        "\t\t\t@Override\n"+
                        "\t\t\tprotected Response<String> parseNetworkResponse(NetworkResponse response) {\n"+
                        "\t\t\t\ttry {\n"+
                        "\t\t\t\tString parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));\n"+
                        "\t\t\t\treturn Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));\n"+
                        "\t\t\t\t} catch (UnsupportedEncodingException e) {\n"+
                        "\t\t\t\t\treturn Response.error(new ParseError(e));\n"+
                        "\t\t\t\t}\n"+
                        "\n\t\t\t}\n"+
//                        "\t\t\t@Override\n"+
//                        "\t\t\tprotected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {\n"+
//                        "\t\t\t\ttry {\n"+
//                        "\t\t\t\tString jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));\n"+
//                        "\t\t\t\treturn Response.success(new JSONObject(jsonString),HttpHeaderParser.parseCacheHeaders(response));\n"+
//                        "\t\t\t\t} catch (UnsupportedEncodingException e) {\n"+
//                        "\t\t\t\t\treturn Response.error(new ParseError(e));\n"+
//                        "\t\t\t\t} catch (JSONException je) {\n"+
//                        "\t\t\t\t\treturn Response.error(new ParseError(je));\n"+
//                        "\t\t\t\t}\n\t\t\t}\n"+
                        "\t\t};\n"+
                        "\tqueue.add(req"+vo.getUuid()+");\n";
            return replace;
            }
        },
        IMAGE{
            @Override
            public String output(RequestVo vo){
                String[] headers =  vo.getHeaders();
                String[] params = vo.getParams();
                String replace = "\n\tImageRequest req"+vo.getUuid()+" = new ImageRequest("+
                        "\""+vo.getUrl()+"\""+
                        ", new Response.Listener<Bitmap>() {\n" +
                        "\t\t@Override\n" +
                        "\t\t\tpublic void onResponse(Bitmap returnBitmap) {\n" +
                        "\t\t\t" +vo.getName()+"(returnBitmap,null);\n"+
                        "\t\t\t}\n" +
                        "\t\t},0, 0, null ,\n" +
                        "\t\tnew Response.ErrorListener() {\n" +
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic void onErrorResponse(VolleyError error) {\n" +
                        "\t\t\t" +vo.getName()+"(null,error);\n"+
                        "\t\t\t}\n" +
                        "\t\t}){\n" +
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic Map<String, String> getHeaders()\n" +
                        "\t\t\tthrows AuthFailureError {\n" +
                        "\t\t\tHashMap< String, String> map = new HashMap<String, String>();\n";
                        for(String header : headers){
                            String [] head = header.split(":");
                            replace += "\t\t\tmap.put("+"\""+head[0]+"\",\""+head[1]+"\");\n";
                        }
                        replace+=
                        "\t\t\treturn map;\n" +
                        "\t\t\t}\n"+
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic Map<String, String> getParams()\n" +
                        "\t\t\tthrows AuthFailureError {\n" +
                        "\t\t\tHashMap< String, String> map = new HashMap<String, String>();\n";
                        for(String param : params){
                            String [] par = param.split(":");
                            replace += "\t\t\tmap.put("+"\""+par[0]+"\",\""+par[1]+"\");\n";
                        }
                        replace+=
                        "\t\t\treturn map;\n" +
                        "\t\t\t}\n"+
                        "\t\t};\n"+
                        "\tqueue.add(req"+vo.getUuid()+");\n";
            return replace;
            }
        };
        protected abstract String output(RequestVo vo);
    }
}
