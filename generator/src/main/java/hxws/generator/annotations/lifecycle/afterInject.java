package hxws.generator.annotations.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by suhaishuang
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface afterInject {
    
}
