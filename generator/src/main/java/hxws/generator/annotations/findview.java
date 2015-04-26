package hxws.generator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by suhaishuang
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface findview {
    int value();
}
