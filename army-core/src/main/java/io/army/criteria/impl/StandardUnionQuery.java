package io.army.criteria.impl;

import io.army.criteria.LockMode;
import io.army.criteria.Query;
import io.army.criteria.Select;
import io.army.criteria.impl.inner._UnionQuery;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.function.Function;
import java.util.function.Supplier;

abstract class StandardUnionQuery<Q extends Query, C> extends StandardPartQuery<Q, C> implements _UnionQuery {

//    static <C> StandardUnionSelect<C> union(Select left, UnionType unionType, Select right, @Nullable C criteria) {
//        left.prepared();
//        right.prepared();
//        return new StandardUnionSelect<>(left, unionType, right, criteria);
//    }

    private StandardUnionQuery(@Nullable C criteria) {
        super(criteria);
    }

    @Override
    public final QuerySpec<Q> lock(LockMode lockMode) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    public final QuerySpec<Q> lock(Function<C, LockMode> function) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    public final QuerySpec<Q> ifLock(@Nullable LockMode lockMode) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    public final QuerySpec<Q> ifLock(Supplier<LockMode> supplier) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    public final QuerySpec<Q> ifLock(Function<C, LockMode> function) {
        throw _Exceptions.castCriteriaApi();
    }


    private static final class BracketSelect<C> extends StandardUnionQuery<Select, C> implements Select {

        private final Select select;

        public BracketSelect(Select select, @Nullable C criteria) {
            super(criteria);
            this.select = select;
        }

        @Override
        void internalClear() {
            //no-op
        }

        @Override
        public UnionSpec<Select, C> bracketsQuery() {
            // always return this.
            return this;
        }

        @Override
        UnionSpec<Select, C> create(Select left, UnionType unionType, Select right) {
            return null;
        }

        @Override
        Select internalAsQuery() {
            // always return this.
            return this;
        }

        @Override
        SelectPartSpec<Select, C> asQueryAndSelect(UnionType unionType) {
            return SQLs.nullableTableSelect(this.criteria);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);

            context.dialect().select(this.select, context);

            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }


    }


}
