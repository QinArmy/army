package io.army.criteria.impl;

import io.army.criteria.ValueExpression;
import io.army.criteria._SqlContext;
import io.army.dialect.SqlBuilder;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;
import io.army.util.Assert;

import java.util.Collection;

final class CollectionExpressionImpl<E> extends AbstractNoNOperationExpression<Collection<E>>
        implements ValueExpression<Collection<E>> {

    static <E> CollectionExpressionImpl<E> build(ParamMeta paramMeta, Collection<E> collection) {
        Assert.notEmpty(collection, "collection must not empty.");

        return new CollectionExpressionImpl<>(paramMeta, collection);
    }

    private final ParamMeta paramMeta;

    private final Collection<E> collection;

    private CollectionExpressionImpl(ParamMeta paramMeta, Collection<E> collection) {
        this.paramMeta = paramMeta;
        this.collection = collection;
    }

    @Override
    public Object value() {
        return this.collection;
    }

    @Override
    protected void afterSpace(_SqlContext context) {
        SqlBuilder builder = context.sqlBuilder();
        builder.append("(");

        int index = 0;
        for (E value : this.collection) {
            if (index > 0) {
                builder.append(",");
            }
            Assert.notNull(value, "value of collection can't be null.");
            builder.append("?");
            context.appendParam(ParamValue.build(this.paramMeta, value));
            index++;
        }
        builder.append(")");
    }

    @Override
    public MappingType mappingMeta() {
        return this.paramMeta.mappingMeta();
    }

}
