package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.ValueExpression;
import io.army.dialect.SQLBuilder;
import io.army.meta.ParamMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;
import io.army.wrapper.ParamWrapper;

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
    protected void afterSpace(SQLContext context) {
        SQLBuilder builder = context.sqlBuilder();
        builder.append("(");

        int index = 0;
        for (E value : this.collection) {
            if (index > 0) {
                builder.append(",");
            }
            Assert.notNull(value, "value of collection can't be null.");
            builder.append("?");
            context.appendParam(ParamWrapper.build(this.paramMeta, value));
            index++;
        }
        builder.append(")");
    }

    @Override
    public MappingMeta mappingMeta() {
        return this.paramMeta.mappingMeta();
    }

}
