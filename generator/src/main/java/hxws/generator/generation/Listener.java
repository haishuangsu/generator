package hxws.generator.generation;

import java.lang.annotation.Annotation;

/**
 * Created by suhaishuang
 */
public class Listener {
    private String method;
    private Class<? extends Annotation> annotationClass;

    public Listener(String method,Class<? extends Annotation> annotationClass) {
        this.method = method;
        this.annotationClass = annotationClass;
    }

    public String getMethod() {
        return method;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }
}
