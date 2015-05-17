package hxws.generator.annotations.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by suhaishuang
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface addRequest {
    String methodType();
    String url();
    String[] headers();
    String[] params();
}
