package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.ValueExpression;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;
import io.army.wrapper.ParamWrapper;

import java.util.Collection;

final class CollectionExpressionImpl<E> extends AbstractNoNOperationExpression<Collection<E>>
        implements ValueExpression<Collection<E>> {

    static <E> CollectionExpressionImpl<E> build(MappingMeta mappingType, Collection<E> collection) {
        Assert.notEmpty(collection, "collection must not empty.");
        return new CollectionExpressionImpl<>(mappingType, collection);
    }

    private final MappingMeta mappingType;

    private final Collection<E> collection;

    private CollectionExpressionImpl(MappingMeta mappingType, Collection<E> collection) {
        this.mappingType = mappingType;
        this.collection = collection;
    }

    @Override
    public Object value() {
        return this.collection;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        StringBuilder builder = context.sqlBuilder();
        builder.append("(");

        int index = 0;
        for (E value : this.collection) {
            if (index > 0) {
                builder.append(",");
            }
            Assert.notNull(value, "value of collection can't be null.");
            builder.append("?");
            context.appendParam(ParamWrapper.build(mappingType, value));
            index++;
        }
        builder.append(")");
    }

    @Override
    public MappingMeta mappingMeta() {
        return this.mappingType;
    }

}
