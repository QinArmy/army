package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Statement;
import io.army.dialect._SqlContext;
import io.army.mapping.BigDecimalType;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.meta.TypeMeta;

import javax.annotation.Nullable;
import java.util.function.*;

@SuppressWarnings("unchecked")
abstract class LimitRowOrderByClause<OR, OD, LR, LO, LF> extends OrderByClause<OR, OD>
        implements Statement._LimitClause<LR>,
        Statement._QueryOffsetClause<LO>,
        Statement._FetchPercentClause<LF>,
        Statement._DmlRowCountLimitClause<LR>,
        Statement._RowCountLimitAllClause<LR>,
        _Statement._SQL2008LimitClauseSpec {

    private ArmyExpression offsetExp;

    private SQLWords offsetRow;

    private SQLWords fethFirstNext;

    private ArmyExpression rowCountOrPercent;

    private SQLWords fetchRowPercent;
    private SQLWords fetchRow;

    private SQLWords fetchOnlyWithTies;

    LimitRowOrderByClause(CriteriaContext context) {
        super(context);
    }


    @Override
    public final LR limit(final Expression rowCount) {
        if (!(rowCount instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (rowCount instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.dontSupportMultiParam(this.context);
        } else if (this.rowCountOrPercent != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.rowCountOrPercent = (ArmyExpression) rowCount;
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
    public final <N extends Number> LR limit(BiFunction<MappingType, Number, Expression> operator,
                                             Supplier<N> supplier) {
        return this.limit(operator.apply(LongType.INSTANCE, CriteriaUtils.asLimitParam(this.context, supplier.get())));
    }

    @Override
    public final LR limit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function,
                          String keyName) {
        final long number;
        number = CriteriaUtils.asLimitParam(this.context, function.apply(keyName));
        return this.limit(operator.apply(LongType.INSTANCE, number));
    }

    @Override
    public final <N extends Number> LR ifLimit(BiFunction<MappingType, Number, Expression> operator,
                                               Supplier<N> supplier) {
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
    public final LR limit(final Expression offset, final Expression rowCount) {
        if (!(offset instanceof ArmyExpression && rowCount instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (this.offsetExp != null || this.rowCountOrPercent != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.offsetExp = (ArmyExpression) offset;
        this.rowCountOrPercent = (ArmyExpression) rowCount;
        return (LR) this;
    }

    @Override
    public final LR limit(BiFunction<MappingType, Number, Expression> operator, long offset, long rowCount) {
        return this.limit(operator.apply(LongType.INSTANCE, offset), operator.apply(LongType.INSTANCE, rowCount));
    }

    @Override
    public final <N extends Number> LR limit(BiFunction<MappingType, Number, Expression> operator
            , Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier) {
        final Expression offsetExp, rowCountExp;
        offsetExp = operator.apply(LongType.INSTANCE, offsetSupplier.get());
        rowCountExp = operator.apply(LongType.INSTANCE, rowCountSupplier.get());
        return this.limit(offsetExp, rowCountExp);
    }

    @Override
    public final LR limit(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function
            , String offsetKey, String rowCountKey) {
        final Expression offsetExp, rowCountExp;
        offsetExp = operator.apply(LongType.INSTANCE, function.apply(offsetKey));
        rowCountExp = operator.apply(LongType.INSTANCE, function.apply(rowCountKey));
        return this.limit(offsetExp, rowCountExp);
    }

    @Override
    public final LR limit(Consumer<BiConsumer<Expression, Expression>> consumer) {
        consumer.accept(this::limit);
        if (this.offsetExp == null || this.rowCountOrPercent == null) {
            throw ContextStack.criteriaError(this.context, "no limit clause");
        }
        return (LR) this;
    }

    @Override
    public final <N extends Number> LR ifLimit(BiFunction<MappingType, Number, Expression> operator
            , Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier) {
        final Number offsetNumber, rowCountNumber;
        if ((offsetNumber = offsetSupplier.get()) != null
                && (rowCountNumber = rowCountSupplier.get()) != null) {
            final Expression offsetExp, rowCountExp;
            offsetExp = operator.apply(LongType.INSTANCE, offsetNumber);
            rowCountExp = operator.apply(LongType.INSTANCE, rowCountNumber);
            this.limit(offsetExp, rowCountExp);
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(BiFunction<MappingType, Object, Expression> operator
            , Function<String, ?> function, String offsetKey, String rowCountKey) {
        final Object offsetNumber, rowCountNumber;
        if ((offsetNumber = function.apply(offsetKey)) != null
                && (rowCountNumber = function.apply(rowCountKey)) != null) {
            final Expression offsetExp, rowCountExp;
            offsetExp = operator.apply(LongType.INSTANCE, offsetNumber);
            rowCountExp = operator.apply(LongType.INSTANCE, rowCountNumber);
            this.limit(offsetExp, rowCountExp);
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Consumer<BiConsumer<Expression, Expression>> consumer) {
        consumer.accept(this::limit);
        return (LR) this;
    }

    @Override
    public final LR limitAll() {
        return this.limit(AllWord.INSTANCE);
    }

    @Override
    public final LR ifLimitAll(BooleanSupplier supplier) {
        if (supplier.getAsBoolean()) {
            this.limit(AllWord.INSTANCE);
        } else {
            this.rowCountOrPercent = null;
        }
        return (LR) this;
    }

    @Override
    public final LO offset(final @Nullable Expression start, final Query.FetchRow row) {
        if (start == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (row != SQLs.ROW && row != SQLs.ROWS) {
            throw CriteriaUtils.unknownWords(this.context);
        }
        this.offsetExp = (ArmyExpression) start;
        this.offsetRow = (SQLWords) row;
        return (LO) this;
    }

    @Override
    public final LO offset(BiFunction<MappingType, Number, Expression> operator, long start, Query.FetchRow row) {
        return this.offset(operator.apply(LongType.INSTANCE, start), row);
    }


    @Override
    public final <N extends Number> LO offset(BiFunction<MappingType, Number, Expression> operator
            , Supplier<N> supplier, Query.FetchRow row) {
        return this.offset(operator.apply(LongType.INSTANCE, supplier.get()), row);
    }

    @Override
    public final LO offset(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function
            , String keyName, Query.FetchRow row) {
        return this.offset(operator.apply(LongType.INSTANCE, function.apply(keyName)), row);
    }

    @Override
    public final LO ifOffset(BiFunction<MappingType, Number, Expression> operator, final @Nullable Number start
            , Query.FetchRow row) {
        if (start != null) {
            this.offset(operator.apply(LongType.INSTANCE, start), row);
        }
        return (LO) this;
    }

    @Override
    public final <N extends Number> LO ifOffset(BiFunction<MappingType, Number, Expression> operator
            , Supplier<N> supplier, Query.FetchRow row) {
        final N start;
        start = supplier.get();
        if (start != null) {
            this.offset(operator.apply(LongType.INSTANCE, start), row);
        }
        return (LO) this;
    }

    @Override
    public final LO ifOffset(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function
            , String keyName, Query.FetchRow row) {
        final Object start;
        start = function.apply(keyName);
        if (start != null) {
            this.offset(operator.apply(LongType.INSTANCE, start), row);
        }
        return (LO) this;
    }

    @Override
    public final LF fetch(Query.FetchFirstNext firstOrNext, final @Nullable Expression count, Query.FetchRow row
            , Query.FetchOnlyWithTies onlyWithTies) {
        if (count == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (this.rowCountOrPercent != null) {
            throw limitAndFetch(this.context);
        } else if (firstOrNext != SQLs.FIRST && firstOrNext != SQLs.NEXT) {
            throw CriteriaUtils.unknownWords(this.context, firstOrNext);
        } else if (row != SQLs.ROW && row != SQLs.ROWS) {
            throw CriteriaUtils.unknownWords(this.context, row);
        } else if (onlyWithTies != SQLs.ONLY && onlyWithTies != SQLs.WITH_TIES) {
            throw CriteriaUtils.unknownWords(this.context, onlyWithTies);
        }
        this.fethFirstNext = (SQLWords) firstOrNext;
        this.rowCountOrPercent = (ArmyExpression) count;
        this.fetchRowPercent = null;
        this.fetchRow = (SQLWords) row;
        this.fetchOnlyWithTies = (SQLWords) onlyWithTies;
        return (LF) this;
    }

    @Override
    public final LF fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
            , long count, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies) {
        return this.fetch(firstOrNext, operator.apply(LongType.INSTANCE, count), row, onlyWithTies);
    }

    @Override
    public final <N extends Number> LF fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number
            , Expression> operator, Supplier<N> supplier, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies) {
        return this.fetch(firstOrNext, operator.apply(LongType.INSTANCE, supplier.get()), row, onlyWithTies);
    }

    @Override
    public final LF fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
            , Function<String, ?> function, String keyName, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies) {
        return this.fetch(firstOrNext, operator.apply(LongType.INSTANCE, function.apply(keyName)), row, onlyWithTies);
    }

    @Override
    public final LF ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
            , final @Nullable Number count, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies) {
        if (count != null) {
            this.fetch(firstOrNext, operator.apply(LongType.INSTANCE, count), row, onlyWithTies);
        }
        return (LF) this;
    }

    @Override
    public final <N extends Number> LF ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number
            , Expression> operator, Supplier<N> supplier, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies) {
        final N count;
        count = supplier.get();
        if (count != null) {
            this.fetch(firstOrNext, operator.apply(LongType.INSTANCE, count), row, onlyWithTies);
        }
        return (LF) this;
    }

    @Override
    public final LF ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
            , Function<String, ?> function, String keyName, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies) {
        final Object count;
        count = function.apply(keyName);
        if (count != null) {
            this.fetch(firstOrNext, operator.apply(LongType.INSTANCE, count), row, onlyWithTies);
        }
        return (LF) this;
    }

    @Override
    public final LF fetch(final Query.FetchFirstNext firstOrNext, final @Nullable Expression percent,
                          final SQLs.WordPercent wordPercent, final Query.FetchRow row,
                          final Query.FetchOnlyWithTies onlyWithTies) {
        if (percent == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (firstOrNext != SQLs.FIRST && firstOrNext != SQLs.NEXT) {
            throw CriteriaUtils.unknownWords(this.context, firstOrNext);
        } else if (wordPercent != SQLs.PERCENT) {
            throw CriteriaUtils.unknownWords(this.context, wordPercent);
        } else if (row != SQLs.ROW && row != SQLs.ROWS) {
            throw CriteriaUtils.unknownWords(this.context, row);
        } else if (onlyWithTies != SQLs.ONLY && onlyWithTies != SQLs.WITH_TIES) {
            throw CriteriaUtils.unknownWords(this.context, onlyWithTies);
        } else if (this.rowCountOrPercent != null) {
            throw limitAndFetch(this.context);
        }
        this.fethFirstNext = (SQLWords) firstOrNext;
        this.rowCountOrPercent = (ArmyExpression) percent;
        this.fetchRowPercent = (SQLWords) wordPercent;
        this.fetchRow = (SQLWords) row;
        this.fetchOnlyWithTies = (SQLWords) onlyWithTies;
        return (LF) this;
    }


    @Override
    public final LF fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
            , Number percent, SQLs.WordPercent wordPercent, Query.FetchRow row
            , Query.FetchOnlyWithTies onlyWithTies) {
        final Expression percentExp;
        percentExp = operator.apply(BigDecimalType.INSTANCE, percent);
        return this.fetch(firstOrNext, percentExp, wordPercent, row, onlyWithTies);
    }

    @Override
    public final <N extends Number> LF fetch(Query.FetchFirstNext firstOrNext
            , BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier
            , SQLs.WordPercent wordPercent, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies) {
        final Expression percentExp;
        percentExp = operator.apply(BigDecimalType.INSTANCE, supplier.get());
        return this.fetch(firstOrNext, percentExp, wordPercent, row, onlyWithTies);
    }

    @Override
    public final LF fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
            , Function<String, ?> function, String keyName, SQLs.WordPercent wordPercent, Query.FetchRow row
            , Query.FetchOnlyWithTies onlyWithTies) {
        final Expression percentExp;
        percentExp = operator.apply(BigDecimalType.INSTANCE, function.apply(keyName));
        return this.fetch(firstOrNext, percentExp, wordPercent, row, onlyWithTies);
    }

    @Override
    public final LF ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
            , @Nullable Number percent, SQLs.WordPercent wordPercent, Query.FetchRow row
            , Query.FetchOnlyWithTies onlyWithTies) {
        if (percent != null) {
            final Expression percentExp;
            percentExp = operator.apply(BigDecimalType.INSTANCE, percent);
            return this.fetch(firstOrNext, percentExp, wordPercent, row, onlyWithTies);
        }
        return (LF) this;
    }

    @Override
    public final <N extends Number> LF ifFetch(Query.FetchFirstNext firstOrNext
            , BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier
            , SQLs.WordPercent wordPercent, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies) {
        final N percent;
        percent = supplier.get();
        if (percent != null) {
            final Expression percentExp;
            percentExp = operator.apply(BigDecimalType.INSTANCE, percent);
            return this.fetch(firstOrNext, percentExp, wordPercent, row, onlyWithTies);
        }
        return (LF) this;
    }

    @Override
    public final LF ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
            , Function<String, ?> function, String keyName, SQLs.WordPercent wordPercent
            , Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies) {
        final Object percent;
        percent = function.apply(keyName);
        if (percent != null) {
            final Expression percentExp;
            percentExp = operator.apply(BigDecimalType.INSTANCE, percent);
            return this.fetch(firstOrNext, percentExp, wordPercent, row, onlyWithTies);
        }
        return (LF) this;
    }

    @Override
    public final _Expression offsetExp() {
        return this.offsetExp;
    }

    @Override
    public final SQLWords offsetRowModifier() {
        return this.offsetRow;
    }

    @Override
    public final _Expression rowCountExp() {
        return this.rowCountOrPercent;
    }


    @Override
    public final SQLWords fetchFirstOrNext() {
        return this.fethFirstNext;
    }

    @Override
    public final SQLWords fetchPercentModifier() {
        return this.fetchRowPercent;
    }

    @Override
    public final SQLWords fetchRowModifier() {
        return this.fetchRow;
    }

    @Override
    public final SQLWords fetchOnlyOrWithTies() {
        return this.fetchOnlyWithTies;
    }

    final boolean hasLimitClause() {
        return this.offsetExp != null || this.rowCountOrPercent != null;
    }

    private static CriteriaException limitAndFetch(CriteriaContext context) {
        return ContextStack.criteriaError(context, "Can't use LIMIT clause with FETCH clause");
    }


    private static final class AllWord extends NonOperationExpression {

        private static final AllWord INSTANCE = new AllWord();

        private AllWord() {
        }

        @Override
        public TypeMeta typeMeta() {
            throw unsupportedOperation(this);
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(" ALL");
        }

    }//AllWord


}
