package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.SetValuePart;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

public interface _Expression<E> extends Expression<E>, SetValuePart, _SelfDescribed {

    boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas);

    boolean containsFieldOf(TableMeta<?> tableMeta);

    int containsFieldCount(TableMeta<?> tableMeta);

    boolean containsSubQuery();

    /**
     * design for non batch update
     */
    boolean nullableExp();

}
