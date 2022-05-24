package io.army.criteria.impl;

import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.NonNull;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>
 * This class representing sql literal expression.
 * </p>
 */
final class LiteralExpression extends OperationExpression implements ValueExpression {

    private static final ConcurrentMap<Object, LiteralExpression> CONSTANT_EXP_CACHE = new ConcurrentHashMap<>();


    static LiteralExpression literal(final ParamMeta paramMeta, final Object literal) {
        return new LiteralExpression(paramMeta, literal);
    }

    static LiteralExpression literal(final Object constant) {
        Objects.requireNonNull(constant);
        return literal(_MappingFactory.getDefault(constant.getClass()), constant);
    }

    private final ParamMeta paramMeta;

    private final Object literal;

    private LiteralExpression(ParamMeta paramMeta, Object literal) {
        this.paramMeta = paramMeta;
        this.literal = literal;
    }


    @Override
    public void appendSql(final _SqlContext context) {
        context.sqlBuilder()
                .append(_Constant.SPACE)
                .append(context.dialect().literal(this.paramMeta, this.literal));
    }


    @Override
    public ParamMeta paramMeta() {
        return this.paramMeta;
    }

    @NonNull
    @Override
    public Object value() {
        return this.literal;
    }

    @Override
    public String toString() {
        return String.valueOf(this.literal);
    }


}
