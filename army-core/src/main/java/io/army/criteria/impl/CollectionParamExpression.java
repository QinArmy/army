package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.dialect._Constant;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;
import io.army.util._CollectionUtils;

import java.util.Collection;
import java.util.List;

final class CollectionParamExpression extends NonOperationExpression {

    static <E> CollectionParamExpression strict(ParamMeta paramMeta, Collection<E> values) {
        return create(paramMeta, values, false);
    }

    static <E> CollectionParamExpression optimizing(ParamMeta paramMeta, Collection<E> values) {
        return create(paramMeta, values, true);
    }

    private static <E> CollectionParamExpression create(final ParamMeta paramMeta, final Collection<E> values
            , final boolean optimizing) {
        if (values.size() == 0) {
            throw new CriteriaException("Collection parameter expression must not empty");
        }
        return new CollectionParamExpression(paramMeta, values, optimizing);
    }

    private final ParamMeta paramMeta;

    private final List<?> value;

    private final boolean optimizing;

    private CollectionParamExpression(final ParamMeta paramMeta, final Collection<?> values, final boolean optimizing) {
        this.paramMeta = paramMeta;
        this.value = _CollectionUtils.asUnmodifiableList(values);
        this.optimizing = optimizing;
    }

    @Override
    public ParamMeta paramMeta() {
        return this.paramMeta;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(_Constant.SPACE_LEFT_PAREN);

        final ParamMeta paramMeta = this.paramMeta;
        final boolean optimizing = this.optimizing && paramMeta.mappingType() instanceof _ArmyNoInjectionMapping;
        final _Dialect dialect = context.dialect();
        int index = 0;
        for (Object v : this.value) {
            if (index > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            if (optimizing) {
                dialect.spaceAndLiteral(paramMeta, v, builder);
            } else {
                context.appendParam(ParamValue.build(paramMeta, v));
            }
            index++;
        }
        builder.append(_Constant.SPACE_RIGHT_PAREN);
    }


    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()
                .append(_Constant.SPACE_LEFT_PAREN);

        final int size = this.value.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(" ?");
            i++;
        }
        builder.append(_Constant.SPACE_RIGHT_PAREN);
        return builder.toString();
    }


}
