package hxws.generator.generation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
 * @author 苏海双
 * Created by suhaishuang
 */
public class ViewGenerator {

    private String packageName;
    private String className;
    private int layoutId;
    private Listener after;


    private Map<Integer,ViewBindListener> viewMap = new HashMap<Integer,ViewBindListener>();
    private Set<Listener> lifeListeners = new HashSet<Listener>();
    private Set<RequestVo> requestVos = new HashSet<RequestVo>();

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
        getOrGreate(id,name,type);
    }

    public void addListener(int id,Listener listener){
        getOrGreate(id).addListener(listener);
    }

    public void addReqeust(RequestVo vo){
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

    public Set<RequestVo> getRequestVos() {
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
        builder.append("import com.android.volley.Response.Listener;\n");
        builder.append("import com.android.volley.VolleyError;\n");
        builder.append("import com.android.volley.toolbox.StringRequest;\n");
        builder.append("import com.android.volley.Response.ErrorListener;\n");


        if("android.app.Fragment".equals(type) || "android.support.v4.app.Fragment".equals(type)){
            builder.append("import android.view.LayoutInflater;\n");
            builder.append("import android.view.ViewGroup;\n");
        }
        builder.append("\n \n");
        builder.append("public class " + className + "_" + " extends " + className);
        builder.append("{ \n");
        builder.append("\n  @Override\n");
        if("android.app.Fragment".equals(type) || "android.support.v4.app.Fragment".equals(type)){
            builder.append("  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");
            builder.append("{ \n");
            if(layoutId != 0){
                builder.append("    View view = inflater.inflate("+layoutId+",null);\n");
            }
            builder.append("    inject(view);\n");
            builder.append("    return view;\n");
            builder.append("  }\n");
            builder.append("  void inject(View view){\n");
            for(ViewBindListener viewBindListener : getViewMap().values()){
                findByid(builder,viewBindListener,false);
                bindListener(builder, viewBindListener.getListeners(), viewBindListener.getId(),viewBindListener.getName());
            }
            afterInject(builder);
            for(Listener lifeListener :lifeListeners){
                lifeCycle(builder,lifeListener,false);
            }
        }else if("android.app.Activity".equals(type) || "android.support.v4.app.FragmentActivity".equals(type)){
            builder.append("  protected void onCreate(Bundle savedInstanceState) ");
            builder.append("{ \n");
            builder.append("    super.onCreate(savedInstanceState);\n");
            if(layoutId != 0){
                builder.append("    setContentView("+layoutId+"); \n");
            }
            builder.append("    inject();\n");
            builder.append("\n  } \n");
            builder.append("  void inject(){");
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
        return builder.toString();
    }

    private void findByid(StringBuilder builder,ViewBindListener viewBindListener,boolean isAct){
        if(isAct){
            builder.append("    "+viewBindListener.getName()+"="+"("+viewBindListener.getType()+")findViewById("+viewBindListener.getId()+");\n");
        }else{
            builder.append("    "+viewBindListener.getName()+"="+"("+viewBindListener.getType()+")view.findViewById("+viewBindListener.getId()+");\n");
        }
    }

    private void bindListener(StringBuilder builder,Set<Listener> listeners,int id,String name){
        for(Listener listener : listeners){
            if(listener.getAnnotationClass().equals(onClick.class)){
                builder.append("    "+name+".setOnClickListener(new View.OnClickListener() {\n");
                builder.append("        @Override\n");
                builder.append("        public void onClick(View v) { "+listener.getMethod()+"(); }\n    });\n");
            }else if(listener.getAnnotationClass().equals(onLongClick.class)){
                builder.append("    "+name+".setOnLongClickListener(new View.OnLongClickListener() {\n");
                builder.append("        @Override\n");
                builder.append("        public boolean onLongClick(View v) { "+listener.getMethod()+"(); return true;}\n    });\n");
            }else if(listener.getAnnotationClass().equals(onItemClick.class)){
                builder.append("    "+name+".setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {\n");
                builder.append("        @Override\n");
                builder.append("        public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) { "+listener.getMethod()+"(parent,view,position,id); }\n    });\n");
            }else if(listener.getAnnotationClass().equals(onItemLongClick.class)){
                builder.append("    "+name+"..setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {\n");
                builder.append("        @Override\n");
                builder.append("        public boolean onItemLongClick(android.widget.AdapterView<?> parent, View view, int position, long id) { "+listener.getMethod()+"(parent,view,position,id); return true;}\n    });\n");
            }
        }
    }

    private void afterInject(StringBuilder builder){
        if(after!=null){
            builder.append("    afterInject();\n");
        }
        builder.append("  }\n");
        if(after!=null){
            builder.append("  void afterInject(){\n");
            builder.append("    "+after.getMethod()+"();\n");
            addRe(builder);
            builder.append("\n  } \n");
        }
    }

    private void addRe(StringBuilder builder){
        for(RequestVo vo : getRequestVos()){
            builder.append("\tStringRequest strReq = new StringRequest(Request.Method.GET,\n" +
                    "\t\t"+"\""+vo.getUrl()+"\""+", new Response.Listener<String>() {\n" +
                    "\t\t\t@Override\n" +
                    "\t\t\tpublic void onResponse(String result) {\n" +
                    "\t\t\t" +vo.getName()+"(result,null);\n"+
                    "\t\t\t}\n" +
                    "\t\t}, new ErrorListener() {\n" +
                    "\t\t\t@Override\n" +
                    "\t\t\tpublic void onErrorResponse(VolleyError error) {\n" +
                    "\t\t\t" +vo.getName()+"(null,error);\n"+
                    "\t\t\t}\n" +
                    "\t\t});\n"+
                    "\tqueue.add(strReq);\n");
        }
    }

    private void lifeCycle(StringBuilder builder,Listener lifeListener,boolean isAct){
        String key;
        if(isAct){
            key = "protected";
        }else{
            key = "public";
        }
        builder.append("  @Override\n");
        if(lifeListener.getAnnotationClass().equals(onStart.class)){
            builder.append("  "+key+" void onStart() { \n");
            builder.append("      super.onStart();\n");
            builder.append("      "+lifeListener.getMethod()+"();\n");
            builder.append("  } \n");
        }else if(lifeListener.getAnnotationClass().equals(onPause.class)){
            builder.append("  "+key+" void onPause() { \n");
            builder.append("      super.onPause();\n");
            builder.append("      "+lifeListener.getMethod()+"();\n");
            builder.append("  } \n");
        }else if(lifeListener.getAnnotationClass().equals(onResume.class)){
            builder.append("  "+key+" void onResume() { \n");
            builder.append("      super.onResume();\n");
            builder.append("      "+lifeListener.getMethod()+"();\n");
            builder.append("  } \n");
        }else if(lifeListener.getAnnotationClass().equals(onStop.class)){
            builder.append("  "+key+" void onStop() { \n");
            builder.append("      super.onStop();\n");
            builder.append("      "+lifeListener.getMethod()+"();\n");
            builder.append("  } \n");
        }else if(lifeListener.getAnnotationClass().equals(onDestroy.class)){
            builder.append("  "+key+" void onDestroy() { \n");
            builder.append("      super.onDestroy();\n");
            builder.append("      "+lifeListener.getMethod()+"();\n");
            builder.append("  } \n");
        }
    }

}
