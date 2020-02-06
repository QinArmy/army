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
     *
     * tableMeta's column name ,the column mapping property must is {@link Enum} that implements {@link CodeEnum}.
     */
    String value();


}
