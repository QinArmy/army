package io.army.annotation;

import java.lang.annotation.*;


/**
 * @see DiscriminatorValue
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inheritance {


    /**
     * Domain's filed name ,the column mapping property must be {@link Enum} that implements {@code io.army.struct.CodeEnum}.
     */
    String value();


}
