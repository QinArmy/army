package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.GenericField;
import io.army.criteria.ValueExpression;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

import java.util.Collection;

final class CollectionParamExpression<E> extends NoNOperationExpression<Collection<E>>
        implements ValueExpression<Collection<E>> {

    static <E> CollectionParamExpression<E> create(Expression<?> type, Collection<E> value) {
        if (value.size() == 0) {
            throw new CriteriaException("Collection parameter expression must not empty");
        }
        final ParamMeta paramMeta;
        if (type instanceof GenericField) {
            paramMeta = (GenericField<?, ?>) type;
        } else {
            paramMeta = type.mappingType();
        }
        return new CollectionParamExpression<>(paramMeta, value);
    }

    private final ParamMeta paramMeta;

    private final Collection<E> value;

    private CollectionParamExpression(ParamMeta paramMeta, Collection<E> value) {
        this.paramMeta = paramMeta;
        this.value = value;
    }

    @Override
    public Object value() {
        return this.value;
    }

    @Override
    public void appendSql(_SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE)
                .append(Constant.LEFT_BRACKET);

        int index = 0;
        for (E v : this.value) {
            if (index > 0) {
                builder.append(Constant.SPACE)
                        .append(Constant.COMMA);
            }
            if (v == null) {
                throw new CriteriaException("Collection element must not null.");
            }
            context.appendParam(ParamValue.build(this.paramMeta, v));
            index++;
        }
        builder.append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET);
    }

    @Override
    public MappingType mappingType() {
        return this.paramMeta.mappingType();
    }


}
