package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

class CastExpression<I extends Item> extends OperationExpression<I> {

    static <I extends Item> CastExpression<I> cast(OperationExpression<I> expression, TypeMeta paramMeta) {
        return new CastExpression<>(expression, paramMeta);
    }

    private final _Expression expression;

    private final TypeMeta typeMeta;

    private CastExpression(OperationExpression<I> expression, TypeMeta typeMeta) {
        super(expression.function);
        this.expression = expression;
        this.typeMeta = typeMeta;
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        this.expression.appendSql(context);
    }


    @Override
    public TypeMeta typeMeta() {
        return this.typeMeta;
    }


    @Override
    public final String toString() {
        return _StringUtils.builder()
                .append(_Constant.SPACE)
                .append(this.expression)
                .append(" typeMeta:")
                .append(this.typeMeta)
                .toString();
    }


}
