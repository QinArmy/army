package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.GenericField;
import io.army.dialect.Constant;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class CollectionParamExpression<E> extends NoNOperationExpression<Collection<E>> {

    static <E> CollectionParamExpression<E> strict(Expression<?> type, Collection<E> values) {
        return create(type, values, false);
    }

    static <E> CollectionParamExpression<E> optimizing(Expression<?> type, Collection<E> values) {
        return create(type, values, true);
    }

    private static <E> CollectionParamExpression<E> create(final Expression<?> type, final Collection<E> values
            , final boolean optimizing) {
        final int size = values.size();
        if (size == 0) {
            throw new CriteriaException("Collection parameter expression must not empty");
        }
        final ParamMeta paramMeta;
        if (type instanceof GenericField) {
            paramMeta = (GenericField<?, ?>) type;
        } else {
            paramMeta = type.paramMeta();
        }
        final List<E> list = new ArrayList<>(size);
        for (E v : values) {
            if (v == null) {
                throw elementIsNull();
            }
            list.add(v);
        }
        return new CollectionParamExpression<>(paramMeta, list, optimizing);
    }


    private final ParamMeta paramMeta;

    private final List<E> value;

    private final boolean optimizing;

    private CollectionParamExpression(final ParamMeta paramMeta, final List<E> valueList, final boolean optimizing) {
        this.paramMeta = paramMeta;
        this.value = Collections.unmodifiableList(valueList);
        this.optimizing = optimizing && paramMeta.mappingType() instanceof _ArmyNoInjectionMapping;
    }

    @Override
    public ParamMeta paramMeta() {
        return this.paramMeta;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE_LEFT_BRACKET);

        final ParamMeta paramMeta = this.paramMeta;
        final boolean optimizing = this.optimizing;
        final _Dialect dialect = context.dialect();
        int index = 0;
        for (E v : this.value) {
            if (v == null) {
                throw elementIsNull();
            }
            if (index > 0) {
                builder.append(Constant.SPACE_COMMA);
            }
            if (optimizing) {
                builder.append(Constant.SPACE)
                        .append(dialect.literal(paramMeta, v));
            } else {
                context.appendParam(ParamValue.build(paramMeta, v));
            }
            index++;
        }
        builder.append(Constant.SPACE_RIGHT_BRACKET);
    }


    private static CriteriaException elementIsNull() {
        return new CriteriaException("Collection element must not null.");
    }


}
