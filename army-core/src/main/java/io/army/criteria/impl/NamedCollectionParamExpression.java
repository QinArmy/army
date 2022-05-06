package io.army.criteria.impl;

import io.army.criteria.NamedElementParam;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;

import java.util.Objects;

/**
 * <p>
 * This class is the implementation of named {@link java.util.Collection} parameter.
 * </p>
 *
 * @since 1.0
 */
final class NamedCollectionParamExpression extends OperationExpression implements NamedElementParam {


    static NamedCollectionParamExpression named(String name, ParamMeta paramMeta, int size) {
        Objects.requireNonNull(name);
        if (size < 1) {
            throw new IllegalArgumentException("size must > 0");
        }
        return new NamedCollectionParamExpression(name, paramMeta, size);
    }


    private final String name;

    private final ParamMeta paramMeta;

    private final int size;

    private NamedCollectionParamExpression(String name, ParamMeta paramMeta, int size) {
        this.name = name;
        this.paramMeta = paramMeta;
        this.size = size;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public ParamMeta paramMeta() {
        return this.paramMeta;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Object value() {
        throw _Exceptions.namedParamsInNonBatch(this);
    }

    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder sqlBuilder = context.sqlBuilder()
                .append(Constant.SPACE_LEFT_BRACKET);
        final int size = this.size;
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(Constant.SPACE_COMMA);
            }
            context.appendParam(this);
        }
        sqlBuilder.append(Constant.SPACE_RIGHT_BRACKET);
    }


    @Override
    public String toString() {
        final StringBuilder sqlBuilder = new StringBuilder()
                .append(Constant.SPACE_LEFT_BRACKET);
        final int size = this.size;
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(Constant.SPACE_COMMA);
            }
            sqlBuilder.append(" ?");
        }
        sqlBuilder.append(Constant.SPACE_RIGHT_BRACKET);
        return sqlBuilder.toString();
    }


}
