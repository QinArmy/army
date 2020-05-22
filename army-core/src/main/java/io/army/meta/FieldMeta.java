package io.army.meta;

import io.army.domain.IDomain;

/**
 * <p> this interface representing a Java class then tableMeta column mapping.</p>
 *
 * @param <T> representing Domain Java Type
 * @param <F> representing Domain property Java Type
 */
public interface FieldMeta<T extends IDomain, F> extends FieldExpression<T, F>, ParamMeta {

}
