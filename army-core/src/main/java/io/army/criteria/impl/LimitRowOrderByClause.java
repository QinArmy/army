package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SqlValueParam;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
class LimitRowOrderByClause<OR, LR> extends OrderByClause<OR> implements Statement._DmlRowCountLimitClause<LR>
        , _Statement._RowCountSpec {

    private ArmyExpression rowCount;

    LimitRowOrderByClause(CriteriaContext context) {
        super(context);
    }

    @Override
    public final LR limit(final Expression rowCount) {
        if (!(rowCount instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (rowCount instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.dontSupportMultiParam(this.context);
        }
        this.rowCount = (ArmyExpression) rowCount;
        return (LR) this;
    }

    @Override
    public final LR limit(BiFunction<MappingType, Number, Expression> operator, final long rowCount) {
        if (rowCount < 0) {
            throw CriteriaUtils.limitParamError(this.context, rowCount);
        }
        return this.limit(operator.apply(LongType.INSTANCE, rowCount));
    }

    @Override
    public final LR limit(BiFunction<MappingType, String, Expression> operator, String paramName) {
        return this.limit(operator.apply(LongType.INSTANCE, paramName));
    }

    @Override
    public final <N extends Number> LR limit(BiFunction<MappingType, Number, Expression> operator
            , Supplier<N> supplier) {
        return this.limit(operator.apply(LongType.INSTANCE, supplier.get()));
    }

    @Override
    public final LR limit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function
            , String keyName) {
        final long number;
        number = CriteriaUtils.asLimitParam(this.context, function.apply(keyName));
        return this.limit(operator.apply(LongType.INSTANCE, number));
    }

    @Override
    public final <N extends Number> LR ifLimit(BiFunction<MappingType, Number, Expression> operator
            , Supplier<N> supplier) {
        final long number;
        number = CriteriaUtils.asIfLimitParam(this.context, supplier.get());
        if (number >= 0) {
            this.limit(operator.apply(LongType.INSTANCE, number));
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function
            , String keyName) {
        final long number;
        number = CriteriaUtils.asIfLimitParam(this.context, function.apply(keyName));
        if (number >= 0) {
            this.limit(operator.apply(LongType.INSTANCE, number));
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.limit(expression);
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(BiFunction<MappingType, String, Expression> operator, @Nullable String paramName) {
        if (paramName != null) {
            this.limit(operator.apply(LongType.INSTANCE, paramName));
        }
        return (LR) this;
    }

    @Override
    public final _Expression rowCount() {
        return this.rowCount;
    }


}
