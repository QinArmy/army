package io.army.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * This annotation representing a child table view,so this annotation must be used with {@link DiscriminatorValue}.
 * This classic use case is oracle dialect,because oracle don't support with clause( or multi-table dml) for update or delete statement.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@MappedSuperclass
@Documented
public @interface DomainView {


}
