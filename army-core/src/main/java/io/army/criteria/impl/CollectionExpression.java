package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;
import io.army.wrapper.ParamWrapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
        List<ParamWrapper> paramList = context.paramList();
        builder.append("(");
        Object value;

        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext(); ) {
            value = iterator.next();
            Assert.notNull(value, "param must not null.");
            builder.append("?");
            paramList.add(ParamWrapper.build(mappingType, value));
            if (iterator.hasNext()) {
                builder.append(",");
            }

        }
        builder.append(")");
    }

    @Override
    public MappingMeta mappingType() {
        return this.mappingType;
    }

}
