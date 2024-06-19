package io.army.struct;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;


/**
 * <p>This annotation just for {@code io.army.mapping.NameEnumType}
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumName {

    /**
     * <p>More info see {@code io.army.mapping.NameEnumType}
     * <p><strong>NOTE</strong>: Don't support CamelCase.
     *
     * @return database enum name for some database (eg : postgre)
     */
    String value();

}
