package hxws.generator.generation;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by suhaishuang
 */
public class ViewBindListener {

    private Set<Listener> listeners = new HashSet<Listener>();

    private int id;
    private String name;
    private String type;

    public ViewBindListener(int id,String name,String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public void addListener(Listener listener){
        listeners.add(listener);
    }

    public Set<Listener> getListeners() {
        return listeners;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
