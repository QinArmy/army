package io.army.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShardingRoute {

    /**
     * @return route function class name.
     */
    String value();

    String[] routeFields() default {};

    /**
     * @return database route field array.
     */
    String[] databaseRouteFields() default {};

    String[] tableRouteFields() default {};
}
