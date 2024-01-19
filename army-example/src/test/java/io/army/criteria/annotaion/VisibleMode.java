package io.army.criteria.annotaion;

import io.army.criteria.Visible;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VisibleMode {

    Visible value() default Visible.ONLY_VISIBLE;

}
