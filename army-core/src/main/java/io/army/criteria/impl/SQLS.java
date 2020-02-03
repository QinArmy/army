package io.army.criteria.impl;

import io.army.criteria.ConstantExpression;
import io.army.criteria.SingleSetAble;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;


public abstract class SQLS {

    protected SQLS() {
        throw new UnsupportedOperationException();
    }

    public static <T extends IDomain> SingleSetAble<T> update(TableMeta<T> tableMeta) {
        return new SingleUpatableImpl<>(tableMeta);
    }

    public static <E> ConstantExpression<E> constant(@Nullable E value) {
        return ConstantExpressionImpl.build(value);
    }

}
