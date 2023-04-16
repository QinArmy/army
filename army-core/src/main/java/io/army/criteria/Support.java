package io.army.criteria;

import io.army.dialect.Database;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(SOURCE)
@Documented
public @interface Support {

    Database[] value();

}
