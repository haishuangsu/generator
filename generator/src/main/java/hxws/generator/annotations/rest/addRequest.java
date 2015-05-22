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
    String requestType() default "";
    String methodType();
    String url();
    String[] headers() default {};
    String[] params() default {};
    Class convert() default None.class;
    int ref_id() default 0;
    String ref() default "";
}

class None{}
