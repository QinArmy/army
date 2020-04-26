package io.army.annotation;

import io.army.struct.CodeEnum;

import java.lang.annotation.*;


/**
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
