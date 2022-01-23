package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

public interface _Expression<E> extends Expression<E>, _SelfDescribed {

    default boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        throw new UnsupportedOperationException();
    }

    default boolean containsFieldOf(TableMeta<?> tableMeta) {
        throw new UnsupportedOperationException();
    }

    default int containsFieldCount(TableMeta<?> tableMeta) {
        throw new UnsupportedOperationException();
    }

    default boolean containsSubQuery() {
        throw new UnsupportedOperationException();
    }

    /**
     * design for non batch update
     */
    boolean nullableExp();

}
