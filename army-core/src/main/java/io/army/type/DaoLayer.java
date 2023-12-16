package io.army.type;


import java.lang.annotation.*;

/**
 * A  Army annotation to declare that annotated elements present only DAO layer,not service layer,business layer,web layer.
 *
 * @since 0.6.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface DaoLayer {


}
