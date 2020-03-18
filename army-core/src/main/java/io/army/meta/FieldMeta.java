package io.army.meta;

import io.army.domain.IDomain;

/**
 * <p> this interface representing a Java class then tableMeta column mapping.</p>
 *
 * @param <T> representing Entity Java class
 * @param <F> representing Entity property Java class
 */
public interface FieldMeta<T extends IDomain, F> extends FieldExp<T, F>, Meta {


}
