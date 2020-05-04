package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;
import io.army.wrapper.ParamWrapper;

import java.util.Collection;

final class CollectionExpression<E> extends AbstractNoNOperationExpression<E> {

    static <E> CollectionExpression<E> build(MappingMeta mappingType, Collection<E> collection) {
        Assert.notEmpty(collection, "collection must not empty.");
        return new CollectionExpression<>(mappingType, collection);
    }

    private final MappingMeta mappingType;

    private final Collection<E> collection;

    private CollectionExpression(MappingMeta mappingType, Collection<E> collection) {
        this.mappingType = mappingType;
        this.collection = collection;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        StringBuilder builder = context.sqlBuilder();
        builder.append("(");

        int index = 0;
        for (E value : collection) {
            if (index > 0) {
                builder.append(",");
            }
            Assert.notNull(value, "param must not null.");
            builder.append("?");
            context.appendParam(ParamWrapper.build(mappingType, value));
            index++;
        }
        builder.append(")");
    }

    @Override
    public MappingMeta mappingType() {
        return this.mappingType;
    }

}
