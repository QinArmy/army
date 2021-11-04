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
     * class only is the implementation of below interface:
     * <ul>
     *     <li>{@link io.army.sharding.ShardingRoute}</li>
     *     <li>{@link io.army.sharding.TableRoute}</li>
     * </ul>
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
