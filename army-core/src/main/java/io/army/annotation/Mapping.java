package io.army.annotation;

import io.army.meta.mapping.MappingType;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {

    /**
     *
     */
    String value() default "";

    Class<? extends MappingType> mapping() default MappingType.class;
}
