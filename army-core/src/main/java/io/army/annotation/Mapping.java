package io.army.annotation;

import io.army.meta.mapping.MappingMeta;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {

    /**
     *
     */
    String value() default "";

    Class<? extends MappingMeta> mapping() default MappingMeta.class;
}
