package io.army.criteria;

import io.army.meta.FieldMeta;
import io.army.meta.GenericField;
import io.army.meta.TableMeta;

import java.util.Collection;

/**
 * this interface representing a {@link IPredicate} than at least contains a {@link GenericField}.
 */
public interface FieldPredicate extends IPredicate, FieldExpression<Boolean> {

    @Override
    void appendSQL(SQLContext context);

    void appendPredicate(SQLContext context);

    @Override
    boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas);

    @Override
    boolean containsFieldOf(TableMeta<?> tableMeta);

    @Override
    int containsFieldCount(TableMeta<?> tableMeta);
}
