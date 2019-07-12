package io.army.annotation;

import io.army.struct.CodeEnum;

import java.lang.annotation.*;


/**
 * created  on 2018/9/27.
 *
 * @see DiscriminatorValue
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inheritance {


    /**
     * property must is  {@link Enum},and implements  {@link CodeEnum}
     */
    String value();


}
