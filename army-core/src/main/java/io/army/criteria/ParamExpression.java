package io.army.criteria;

import io.army.dialect.ParamWrapper;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingType;

import java.math.BigInteger;
import java.util.Collection;

/**
 * extends {@link ParamWrapper} to avoid new instance of {@link ParamWrapper}
 * created  on 2018/12/4.
 */
public interface ParamExpression<E> extends Expression<E>, ParamWrapper {

    String MSG = "operation isn'field supported by ParamExpression";

    E value();

    /**
     *
     */
    @Override
    void appendSQL(SQLContext context);

    @Override
    MappingType mappingType();

    @Override
    String toString();


}
